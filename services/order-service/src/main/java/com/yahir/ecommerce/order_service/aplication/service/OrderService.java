package com.yahir.ecommerce.order_service.aplication.service;

import com.yahir.ecommerce.order_service.aplication.command.CreateOrderCommand;
import com.yahir.ecommerce.order_service.aplication.command.OrderItemCommand;
import com.yahir.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.yahir.ecommerce.order_service.domain.event.OrderStatusChangedEvent;
import com.yahir.ecommerce.order_service.domain.exception.InsufficientStockException;
import com.yahir.ecommerce.order_service.domain.exception.OrderNotFoundException;
import com.yahir.ecommerce.order_service.domain.model.Order;
import com.yahir.ecommerce.order_service.domain.model.OrderItem;
import com.yahir.ecommerce.order_service.domain.model.OrderStatus;
import com.yahir.ecommerce.order_service.domain.model.ProductInfo;
import com.yahir.ecommerce.order_service.domain.port.in.OrderUseCase;
import com.yahir.ecommerce.order_service.domain.port.out.EventPublisherPort;
import com.yahir.ecommerce.order_service.domain.port.out.InventoryClientPort;
import com.yahir.ecommerce.order_service.domain.port.out.OrderRepositoryPort;
import com.yahir.ecommerce.order_service.domain.port.out.ProductClientPort;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService implements OrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final ProductClientPort productClient;
    private final InventoryClientPort inventoryClient;
    private final EventPublisherPort eventPublisher;

    @Transactional
    @Override
    public Order createOrder(CreateOrderCommand command) {

        // 1. Validar stock de todos los items primero — sin reservar aún
        for (OrderItemCommand item : command.getItems()) {
            StockSummaryResponse stock = inventoryClient.getStockSummary(item.getProductId());
            if (stock.availableQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                        item.getProductId(), item.getQuantity());
            }
        }

        // 2. Construir items con snapshot de precio desde Product Service
        List<OrderItem> items = command.getItems().stream()
                .map(this::toOrderItem)
                .toList();

        // 3. Crear y persistir Order en PENDING — sin orderId aún necesitamos persistir primero
        Order order = new Order(
                null,
                command.getCustomerId(),
                items,
                OrderStatus.PENDING,
                LocalDateTime.now()
        );
        Order saved = orderRepository.save(order);  // ya tenemos el orderId real

        // 4. UNA SOLA LLAMADA al Inventory Service
        try {
            List<ItemReserveRequest> reserveItems = command.getItems().stream()
                    .map(i -> new ItemReserveRequest(i.getProductId(), i.getQuantity()))
                    .toList();

            inventoryClient.reserveAllStock(new ReserveStockBatchRequest(
                    saved.getId(), saved.getCustomerId(), reserveItems
            ));
        } catch (Exception ex) {
            // Si falla la única llamada, cancelamos orden.
            // El @Transactional se encarga del rollback local.
            saved.transitionTo(OrderStatus.CANCELLED);
            orderRepository.save(saved);
            throw new RuntimeException("Error al reservar inventario: " + ex.getMessage());
        }

        // 5. Publicar evento — Payment solo necesita el orderId
        eventPublisher.publish(new OrderCreatedEvent(
                saved.getId(),
                saved.getCustomerId(),
                saved.getTotalAmount()
        ));
        return saved;
    }

    @Transactional
    @Override
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderOrThrow(orderId);
        OrderStatus previousStatus = order.getStatus();

        order.transitionTo(newStatus);
        orderRepository.save(order);

        eventPublisher.publish(new OrderStatusChangedEvent(
                order.getId(),
                order.getCustomerId(),
                previousStatus,
                newStatus
        ));
    }

    @Transactional(readOnly = true)
    @Override
    public Order findById(Long orderId) {
        return getOrderOrThrow(orderId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId,pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    // ─── Helpers ─────────────────────────────────────────

    private Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderItem toOrderItem(OrderItemCommand cmd) {
        ProductInfo info = productClient.getProductInfo(cmd.getProductId());
        return new OrderItem(null, info.productId(), info.name(), cmd.getQuantity(), info.price());
    }
}
