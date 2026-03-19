package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import com.yahir.ecommerce.order_service.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest (
        @NotNull(message = "Status is required")
        OrderStatus status
){}
