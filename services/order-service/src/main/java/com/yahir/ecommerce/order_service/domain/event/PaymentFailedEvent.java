package com.yahir.ecommerce.order_service.domain.event;

public record PaymentFailedEvent(
        Long orderId,
        String reason
)implements DomainEvent {

    @Override
    public String eventType() { return "PAYMENT_FAILED"; }
}
