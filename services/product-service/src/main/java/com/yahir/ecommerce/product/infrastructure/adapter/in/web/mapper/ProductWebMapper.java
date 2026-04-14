package com.yahir.ecommerce.product.infrastructure.adapter.in.web.mapper;

import com.yahir.ecommerce.product.domain.model.Category;
import com.yahir.ecommerce.product.domain.model.Product;
import com.yahir.ecommerce.product.domain.model.ProductStatus;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto.CreateProductRequest;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto.ProductResponse;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto.UpdateProductRequest;

public class ProductWebMapper {
    public static Product toDomain(CreateProductRequest request){
        return new Product(
                null,
                request.name(),
                request.description(),
                request.sku(),
                request.price(),
                new Category(request.categoryId(), null, null, null),
                ProductStatus.ACTIVE,
                null
        );
    }
    public static Product toDomain(Long id, UpdateProductRequest request) {
        return new Product(
                id,
                request.name(),
                request.description(),
                null,
                request.price(),
                new Category(request.categoryId(), null, null, null),
                null,
                null
        );
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                CategoryWebMapper.toResponse(product.getCategory()),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}
