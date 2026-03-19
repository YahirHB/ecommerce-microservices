package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import com.yahir.ecommerce.order_service.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        List<orderItemResponse> items,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt
        ) {
    public record orderItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subTotal
        ){}
    }

