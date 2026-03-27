package com.yahir.ecommerce.payment_service.domain.event;

public record PaymentFailedEvent(
        Long orderId,
        Long paymentId,
        String reason
) implements DomainEvent {

    @Override
    public String eventType() { return "PAYMENT_FAILED"; }
}
