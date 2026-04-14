package com.yahir.ecommerce.order_service.domain.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        BigDecimal totalAmount
) implements DomainEvent {

    @Override
    public String eventType() { return "ORDER_PENDING"; }
}