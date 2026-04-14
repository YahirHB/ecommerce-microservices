package com.yahir.ecommerce.order_service.infrastructure.adapter.out.product.client.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock
) {}
