package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(

        @NotNull(message = "productId is required")
        @Positive(message = "productId must be positive")
        Long productId,

        @NotNull(message = "quantity is required")
        @Min(value = 1,message = "quantity must be at least 1")
        Integer quantity
) {}
