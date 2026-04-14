package com.yahir.ecommerce.order_service.domain.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent (
        Long orderId,
        Long paymentId,
        BigDecimal amount
)implements DomainEvent {

    @Override
    public String eventType() { return "PAYMENT_COMPLETED"; }
}
