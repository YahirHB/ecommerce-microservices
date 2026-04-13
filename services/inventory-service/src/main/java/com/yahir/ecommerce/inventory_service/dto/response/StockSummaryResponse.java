package com.yahir.ecommerce.inventory_service.dto.response;

 public record StockSummaryResponse(
        Long productId,
        Integer stockQuantity,
        Integer reservedQuantity,
        Integer availableQuantity,
        boolean lowStock
) {}