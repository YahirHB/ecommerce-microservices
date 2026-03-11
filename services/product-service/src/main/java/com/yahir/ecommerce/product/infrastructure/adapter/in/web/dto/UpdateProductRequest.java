package com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest (
        @NotBlank(message = "Name is Required")
        String name,
        String description,
        @NotNull(message = "Price is Required")
        @DecimalMin(value="0.0",inclusive = false,message = "Price must be greater that 0")
        BigDecimal price,
        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock,
        @NotNull(message = "Category is required")
        Long categoryId
){}
