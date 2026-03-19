package com.yahir.ecommerce.order_service.domain.event;

public interface DomainEvent {
    Long orderId();
    String eventType();
}
