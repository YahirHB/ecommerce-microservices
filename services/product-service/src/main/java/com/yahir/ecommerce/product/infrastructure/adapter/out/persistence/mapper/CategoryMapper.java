package com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.mapper;

import com.yahir.ecommerce.product.domain.model.Category;
import com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.entity.CategoryEntity;

public class CategoryMapper {
    public static Category toDomain(CategoryEntity categoryEntity){
        return new Category(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getDescription(),
                categoryEntity.getCreatedAt()
        );
    }

    public static CategoryEntity toEntity(Category category){
        return new CategoryEntity(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt()
        );
    }
}
