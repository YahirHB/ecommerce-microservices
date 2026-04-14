package com.yahir.ecommerce.product.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Category category;
    private ProductStatus status;
    private LocalDate createdAt;
}
