package com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto;

import com.yahir.ecommerce.product.domain.model.Category;
import com.yahir.ecommerce.product.domain.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
public record ProductResponse (Long id,
                               String name,
                               String description,
                               String sku,
                               BigDecimal price,
                               Integer stock,
                               CategoryResponse category,
                               ProductStatus status,
                               LocalDate createdAt){}