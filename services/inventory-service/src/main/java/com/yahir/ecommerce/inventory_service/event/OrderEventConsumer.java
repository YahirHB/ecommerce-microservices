package com.yahir.ecommerce.inventory_service.event;

import com.yahir.ecommerce.inventory_service.dto.event.OrderStatusChangedEvent;
import com.yahir.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order.status.changed",
            groupId = "inventory-service-group",
            properties = {
            // Ignora el "Type ID" que envía el emisor en el header (evita errores de paquetes distintos)
            "spring.json.use.type.headers=false",
            // Fuerza a que el JSON se convierta directamente a esta clase local
            "spring.json.value.default.type=com.yahir.ecommerce.inventory_service.dto.event.OrderStatusChangedEvent"
    })
    public void handleOrderConfirmed(OrderStatusChangedEvent event) {
        log.info("Evento de estado de orden recibido: OrderID = {}, Nuevo Estado = {}",
                event.orderId(), event.newStatus());
        try {
            switch (event.newStatus()){
                case CONFIRMED ->{
                    log.info("Received order.confirmed event: {}", event);
                    // Inventory busca internamente la reserva usando el orderId
                    inventoryService.confirmReservationByOrderId(event.orderId());
                } case CANCELLED -> {
                    log.info("Received order.cancelled event: {}", event);
                    // Inventory busca internamente la reserva usando el orderId y la libera
                    inventoryService.releaseStockByOrderId(event.orderId());
                }
                default -> log.debug("Estado {} ignorado por InventoryService", event.newStatus());
            }
        }catch (Exception e){
            log.error("Error procesando inventario para OrderID {}: {}", event.orderId(), e.getMessage());
        }
    }
}
