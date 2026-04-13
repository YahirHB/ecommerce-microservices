package com.yahir.ecommerce.inventory_service.dto.event;

public interface DomainEvent {
    Long orderId();
    String eventType();
}
