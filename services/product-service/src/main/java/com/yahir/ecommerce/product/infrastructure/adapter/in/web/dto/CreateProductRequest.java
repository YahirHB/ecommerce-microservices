package com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest (
        @NotBlank(message= "Product name is required")
        String name,
        String description,
        @NotBlank(message = "Sku is required")
        String sku,
        @Positive(message = "Price must be positive")
        @NotNull(message= "Price is required")
        BigDecimal price,
        @NotNull(message= "The id Category is required")
        Long categoryId){}
