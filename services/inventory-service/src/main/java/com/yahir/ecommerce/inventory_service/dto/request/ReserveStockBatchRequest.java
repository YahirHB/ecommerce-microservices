package com.yahir.ecommerce.inventory_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReserveStockBatchRequest(
        @NotNull(message = "orderId is required")
        Long orderId,

        @NotNull(message = "customerId is Required")
        Long customerId,

        @NotEmpty(message = "The list can't be empty")
        @Valid
        List<@NotNull ItemReserveRequest> items
) {}
