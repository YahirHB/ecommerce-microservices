package com.yahir.ecommerce.payment_service.domain.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
    Long orderId,
    Long paymentId,
    Long customerId,
    BigDecimal amount
) implements DomainEvent {
    @Override
    public String eventType() {
        return "PAYMENT_COMPLETED";
    }
}
