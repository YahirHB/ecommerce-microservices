package com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.dto;

import com.yahir.ecommerce.payment_service.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse (
        Long id,
        Long orderId,
        Long customerId,
        BigDecimal amount,
        PaymentStatus status,
        String paymentMethod,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}
