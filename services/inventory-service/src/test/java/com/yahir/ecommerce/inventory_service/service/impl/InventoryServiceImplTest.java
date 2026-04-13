package com.yahir.ecommerce.inventory_service.service.impl;

import com.yahir.ecommerce.inventory_service.Enum.InventoryStatus;
import com.yahir.ecommerce.inventory_service.client.ProductClient;
import com.yahir.ecommerce.inventory_service.dto.request.ReserveStockCommand;
import com.yahir.ecommerce.inventory_service.dto.response.ReservationResponse;
import com.yahir.ecommerce.inventory_service.entity.InventoryEntity;
import com.yahir.ecommerce.inventory_service.event.InventoryEventProducer;
import com.yahir.ecommerce.inventory_service.exception.InsufficientStockException;
import com.yahir.ecommerce.inventory_service.repository.InventoryRepository;
import com.yahir.ecommerce.inventory_service.repository.StockReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private StockReservationRepository reservationRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private InventoryEventProducer eventProducer;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryEntity inventory;

    @BeforeEach
    void setUp() {
        inventory = InventoryEntity.builder()
                .id(1L)
                .productId(10L)
                .stockQuantity(100)
                .reservedQuantity(0)
                .minimumStock(5)
                .status(InventoryStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should reserve stock successfully when enough stock available")
    void shouldReserveStockSuccessfully() {
        ReserveStockCommand command = ReserveStockCommand.builder()
                .productId(10L)
                .userId(1L)
                .quantity(10)
                .build();

        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        ReservationResponse response = inventoryService.reserveStock(command);

        assertThat(response).isNotNull();
        assertThat(response.quantity()).isEqualTo(10);
        assertThat(response.productId()).isEqualTo(10L);
        verify(eventProducer).publishStockReserved(anyString(), eq(10L), eq(1L), eq(10));
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when not enough stock")
    void shouldThrowWhenInsufficientStock() {
        inventory.setStockQuantity(5);

        ReserveStockCommand command = ReserveStockCommand.builder()
                .productId(10L)
                .userId(1L)
                .quantity(10)
                .build();

        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> inventoryService.reserveStock(command))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");

        verify(inventoryRepository, never()).save(any());
        verify(eventProducer, never()).publishStockReserved(any(), any(), any(), any());
    }
}
