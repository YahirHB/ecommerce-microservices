package com.yahir.ecommerce.payment_service.aplication.command;

import java.math.BigDecimal;

public record ProcessPaymentCommand(
        Long orderId,
        Long customerId,
        BigDecimal amount,
        String paymentMethod) {
}
