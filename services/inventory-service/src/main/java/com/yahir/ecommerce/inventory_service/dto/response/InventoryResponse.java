package com.yahir.ecommerce.inventory_service.dto.response;

import com.yahir.ecommerce.inventory_service.Enum.InventoryStatus;

import java.time.LocalDateTime;

public record InventoryResponse(
        Long id,
        Long productId,
        Integer stockQuantity,
        Integer reservedQuantity,
        Integer availableQuantity,
        Integer minimumStock,
        InventoryStatus status,
        LocalDateTime updatedAt
) {}
