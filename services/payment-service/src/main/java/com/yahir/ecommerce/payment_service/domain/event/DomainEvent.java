package com.yahir.ecommerce.payment_service.domain.event;

public interface DomainEvent {
    Long orderId();
    String eventType();
}
