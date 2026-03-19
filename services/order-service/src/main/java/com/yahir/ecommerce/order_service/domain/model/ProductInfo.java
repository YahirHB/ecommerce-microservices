package com.yahir.ecommerce.order_service.domain.model;

import java.math.BigDecimal;

public record ProductInfo(
        Long productId,
        String name,
        BigDecimal price) {}
