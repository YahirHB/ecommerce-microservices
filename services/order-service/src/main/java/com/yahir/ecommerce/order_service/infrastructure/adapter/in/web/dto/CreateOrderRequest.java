package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest (

        @NotNull(message = "customerId is required")
        @Positive(message = "customerId must be positive")
        Long customerId,

        @NotEmpty(message = "items cannot be empty")
        List<OrderItemRequest> items
) {}
