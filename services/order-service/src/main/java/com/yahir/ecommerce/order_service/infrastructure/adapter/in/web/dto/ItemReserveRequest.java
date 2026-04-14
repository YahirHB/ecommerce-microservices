package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemReserveRequest(

        @NotNull(message = "productId is required")
        Long productId,

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be at least -1")
        Integer quantity
) {}
