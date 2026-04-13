package com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.mapper;

import com.yahir.ecommerce.product.domain.model.Product;
import com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;

public class ProductMapper {
    public static ProductEntity toEntity(Product product){
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                CategoryMapper.toEntity(product.getCategory()),
                product.getStatus(),
                product.getCreatedAt()
        );
    }

    public static Product toDomain(ProductEntity entity){
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSku(),
                entity.getPrice(),
                CategoryMapper.toDomain(entity.getCategory()),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
