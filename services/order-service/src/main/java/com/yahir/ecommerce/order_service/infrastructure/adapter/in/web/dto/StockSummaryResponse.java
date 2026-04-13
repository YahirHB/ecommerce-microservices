package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

public record StockSummaryResponse(
        Long productId,
        Integer stockQuantity,
        Integer reservedQuantity,
        Integer availableQuantity,
        boolean lowStock
) {}
