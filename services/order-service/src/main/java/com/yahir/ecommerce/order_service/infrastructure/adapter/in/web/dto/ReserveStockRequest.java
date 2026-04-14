package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "User ID is required")
    private Long customerId;

    @NotNull(message = "Quantity is required")
    @Min(value=1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Long orderId; //Opcional, se asigna cuando viene del checkout
}