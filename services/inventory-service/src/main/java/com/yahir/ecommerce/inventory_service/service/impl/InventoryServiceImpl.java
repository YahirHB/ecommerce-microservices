package com.yahir.ecommerce.inventory_service.service.impl;

import com.yahir.ecommerce.inventory_service.Enum.InventoryStatus;
import com.yahir.ecommerce.inventory_service.Enum.ReservationStatus;
import com.yahir.ecommerce.inventory_service.client.ProductClient;
import com.yahir.ecommerce.inventory_service.dto.request.*;
import com.yahir.ecommerce.inventory_service.dto.response.InventoryResponse;
import com.yahir.ecommerce.inventory_service.dto.response.ReservationResponse;
import com.yahir.ecommerce.inventory_service.dto.response.StockSummaryResponse;
import com.yahir.ecommerce.inventory_service.entity.InventoryEntity;
import com.yahir.ecommerce.inventory_service.entity.StockReservation;
import com.yahir.ecommerce.inventory_service.event.InventoryEventProducer;
import com.yahir.ecommerce.inventory_service.exception.DuplicateProductIdInventory;
import com.yahir.ecommerce.inventory_service.exception.InsufficientStockException;
import com.yahir.ecommerce.inventory_service.exception.InventoryNotFoundException;
import com.yahir.ecommerce.inventory_service.exception.ReservationNotFoundException;
import com.yahir.ecommerce.inventory_service.mapper.InventoryMapper;
import com.yahir.ecommerce.inventory_service.mapper.StockReservationMapper;
import com.yahir.ecommerce.inventory_service.repository.InventoryRepository;
import com.yahir.ecommerce.inventory_service.repository.StockReservationRepository;
import com.yahir.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository stockReservationRepository;
    private final ProductClient productClient;
    private final InventoryEventProducer inventoryEventProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long RESERVATION_TTL_MINUTES = 15;
    private static final String RESERVATION_KEY = "reservation:";

    @Transactional
    @Override
    public InventoryResponse createInventory(AdjustStockCommand command) {
        productClient.productExiste(command.getProductId());
        log.info("The product exists: " + command.getProductId());
        if (inventoryRepository.existsByProductId(command.getProductId())){
            throw new DuplicateProductIdInventory(command.getProductId());
        }
        InventoryEntity saved = inventoryRepository.save(InventoryMapper.toEntity(command));
        log.info("Inventory created for productId {}", command.getProductId());
        return InventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {
        return InventoryMapper.toResponse(findInventory(productId));
    }

    @Override
    public StockSummaryResponse getStockSummary(Long productId) {
        InventoryEntity inv = findInventory(productId);
        return new StockSummaryResponse(
                inv.getProductId(),
                inv.getStockQuantity(),
                inv.getReservedQuantity(),
                inv.getAvailableQuantity(),
                (inv.getAvailableQuantity() <= inv.getMinimumStock())
        );
    }

    @Override
    public ReservationResponse reserveStock(ReserveStockCommand command) {
        InventoryEntity inventory = findInventory(command.getProductId());
        if (!inventory.hasEnoughStock(command.getQuantity())){
            throw new InsufficientStockException(
                    command.getProductId(),
                    command.getQuantity(),
                    inventory.getAvailableQuantity());
        }

        inventory.reserve(command.getQuantity());
        inventoryRepository.save(inventory);

        String reservationId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(RESERVATION_TTL_MINUTES);

        StockReservation stockReservation = StockReservation.builder()
                .reservationId(reservationId)
                .productId(command.getProductId())
                .userId(command.getUserId())
                .orderId(command.getOrderId())
                .quantity(command.getQuantity())
                .status(ReservationStatus.PENDING)
                .expiresAt(expiresAt)
                .build();

        stockReservationRepository.save(stockReservation);

        /* guardar en Redis con TTL
        redisTemplate.opsForValue().set(
                RESERVATION_KEY + reservationId,
                reservationId,
                Duration.ofMinutes(RESERVATION_TTL_MINUTES)
        );*/

        inventoryEventProducer.publishStockReserved(reservationId, command.getProductId(),
                command.getUserId(), command.getQuantity());

        log.info("Stock reserved: reservationId={}, productId={}, qty={}",
                reservationId, command.getProductId(), command.getQuantity());

        return StockReservationMapper.toReservationResponse(stockReservation);
    }

    @Override
    public void releaseStock(ReleaseStockCommand command) {
        StockReservation reservation = stockReservationRepository
                .findByReservationId(command.getReservationId())
                .orElseThrow(() -> new ReservationNotFoundException(command.getReservationId()));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("Attempted to release non-pending reservation: {}", command.getReservationId());
            return;
        }

        InventoryEntity inventory = findInventory(reservation.getProductId());
        inventory.release(reservation.getQuantity());
        inventoryRepository.save(inventory);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservation.setReleasedAt(LocalDateTime.now());
        stockReservationRepository.save(reservation);

        //redisTemplate.delete(RESERVATION_KEY + command.getReservationId());

        inventoryEventProducer.publishStockReleased(reservation.getReservationId(),
        reservation.getProductId(), reservation.getQuantity());

        log.info("Stock released: reservationId={}", command.getReservationId());
    }

    @Override
    public void confirmReservation(String reservationId, Long orderId) {
        StockReservation reservation = stockReservationRepository
                .findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        InventoryEntity inventory = findInventory(reservation.getProductId());
        inventory.deduct(reservation.getQuantity());
        inventoryRepository.save(inventory);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setOrderId(orderId);
        reservation.setConfirmedAt(LocalDateTime.now());
        stockReservationRepository.save(reservation);

        //redisTemplate.delete(RESERVATION_KEY + reservationId);

        log.info("Reservation confirmed: reservationId={}, orderId={}", reservationId, orderId);
    }

    @Override
    public List<ReservationResponse> reserveAllStockByOrderId(ReserveStockBatchRequest command) {
        log.info("Iniciando reserva masiva para OrderId: {}", command.orderId());

        // 1. Generamos UN SOLO ID de reserva para toda la transacción
        String globalReservationId = UUID.randomUUID().toString();
        List<StockReservation> reservations = new ArrayList<>();
        log.info("reservations: {}", reservations.toString());
        for (ItemReserveRequest item : command.items()) {
            InventoryEntity inventory = findInventory(item.productId());

            // Validar stock
            if (!inventory.hasEnoughStock(item.quantity())) {
                throw new InsufficientStockException(item.productId(), item.quantity(), inventory.getAvailableQuantity());
            }
            log.info("Brinco exception validar Stock");

            // 2. Reservar en la entidad Inventory
            inventory.reserve(item.quantity());
            inventoryRepository.save(inventory);
            log.info("Guardo en inventoryRepository");

            // 3. Crear el registro de la reserva vinculando al globalReservationId y orderId
            StockReservation reservation = StockReservation.builder()
                    .reservationId(globalReservationId) // El mismo para todos los items de la orden
                    .productId(item.productId())
                    .orderId(command.orderId())
                    .userId(command.customerId())
                    .quantity(item.quantity())
                    .status(ReservationStatus.PENDING)
                    .expiresAt(LocalDateTime.now().plusMinutes(RESERVATION_TTL_MINUTES))
                    .build();
            log.info("item id: {}",item.productId());
            log.info("reservation {}", reservation);
            reservations.add(stockReservationRepository.save(reservation));
        }
            log.info("Despues del for stockReservationRepository");

        /* 4. Guardar en Redis una sola vez para la orden completa
        redisTemplate.opsForValue().set(
                RESERVATION_KEY + globalReservationId,
                command.orderId().toString(),
                Duration.ofMinutes(RESERVATION_TTL_MINUTES)
        );
        log.info("redis");*/
        log.info("..........");
        reservations.stream().forEach(System.out::println);
        return reservations.stream()
                .map(StockReservationMapper::toReservationResponse)
                .toList();
    }

    @Override
    public void confirmReservationByOrderId(Long orderId) {
        // 1. Buscamos la reserva activa asociada a esa orden
        List<StockReservation> reservations = stockReservationRepository
                .findAllByOrderIdAndStatus(orderId, ReservationStatus.PENDING);
        reservations.stream().forEach(System.out::println);

        if (reservations.isEmpty()){
            log.warn("No pending reservations found for orderId={}", orderId);
            return;
        }

        for (StockReservation reservation : reservations) {
            InventoryEntity inventory = findInventory(reservation.getProductId());
            inventory.deduct(reservation.getQuantity());          // descuenta stock real
            inventoryRepository.save(inventory);

            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setOrderId(orderId);
            reservation.setConfirmedAt(LocalDateTime.now());
            stockReservationRepository.save(reservation);

            //redisTemplate.delete(RESERVATION_KEY + reservation.getReservationId());

            log.info("Reservation confirmed: reservationId={}, productId={}, orderId={}",
                    reservation.getReservationId(), reservation.getProductId(), orderId);
        }
        log.info("Termina el for de descuento basado en las reservaciones");
    }

    @Override
    public void releaseStockByOrderId(Long orderId) {
        List<StockReservation> reservations = stockReservationRepository
                .findAllByOrderIdAndStatus(orderId, ReservationStatus.PENDING);

        if (reservations.isEmpty()) {
            log.warn("No pending reservations found to release for orderId={}", orderId);
            return;
        }

        for (StockReservation reservation : reservations) {
            InventoryEntity inventory = findInventory(reservation.getProductId());
            inventory.release(reservation.getQuantity());         // devuelve al available
            inventoryRepository.save(inventory);

            reservation.setStatus(ReservationStatus.RELEASED);
            reservation.setReleasedAt(LocalDateTime.now());
            stockReservationRepository.save(reservation);

            //redisTemplate.delete(RESERVATION_KEY + reservation.getReservationId());

            inventoryEventProducer.publishStockReleased(
                    reservation.getReservationId(),
                    reservation.getProductId(),
                    reservation.getQuantity());

            log.info("Stock released: reservationId={}, productId={}, orderId={}",
                    reservation.getReservationId(), reservation.getProductId(), orderId);
        }
    }

    @Override
    public InventoryResponse adjustStock(AdjustStockCommand command) {
        InventoryEntity inventory = findInventory(command.getProductId());
        inventory.setStockQuantity(command.getQuantity());
        return InventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public List<StockSummaryResponse> getLowStockItems() {
        return inventoryRepository.findLowStockItems(InventoryStatus.ACTIVE)
                .stream()
                .map(inv -> new StockSummaryResponse(
                        inv.getProductId(),
                        inv.getStockQuantity(),
                        inv.getReservedQuantity(),
                        inv.getAvailableQuantity(),
                        true
                ))
                .toList();
    }
    // ── helpers ──
    private InventoryEntity findInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }
}
