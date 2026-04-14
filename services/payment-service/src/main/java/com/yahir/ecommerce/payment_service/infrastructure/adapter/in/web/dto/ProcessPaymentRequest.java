package com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProcessPaymentRequest (
        @NotNull(message = "orderId is required")
        @Positive(message = "orderId must be positive")
        Long orderId,

        @NotNull
        @Positive(message = "customerId must be positive")
        Long customerId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "paymentMethod is required")
        String paymentMethod
) {}
