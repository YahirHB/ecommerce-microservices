package com.yahir.ecommerce.inventory_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStockReserved(String reservationId, Long productId,
                                     Long userId, Integer quantity) {
        Map<String, Object> event = Map.of(
                "reservationId", reservationId,
                "productId", productId,
                "userId", userId,
                "quantity", quantity,
                "event", "STOCK_RESERVED"
        );
        kafkaTemplate.send("stock.reserved", reservationId, event);
        log.info("Event published: stock.reserved for reservationId {}", reservationId);
    }

    public void publishStockReleased(String reservationId, Long productId, Integer quantity) {
        Map<String, Object> event = Map.of(
                "reservationId", reservationId,
                "productId", productId,
                "quantity", quantity,
                "event", "STOCK_RELEASED"
        );
        kafkaTemplate.send("stock.released", reservationId, event);
        log.info("Event published: stock.released for reservationId {}", reservationId);
    }

    public void publishLowStockAlert(Long productId, Integer availableQuantity) {
        Map<String, Object> event = Map.of(
                "productId", productId,
                "availableQuantity", availableQuantity,
                "event", "LOW_STOCK_ALERT"
        );
        kafkaTemplate.send("stock.low-alert", productId.toString(), event);
        log.warn("Low stock alert published for productId {}", productId);
    }
}
