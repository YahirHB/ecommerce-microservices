package com.yahir.ecommerce.product.infrastructure.adapter.in.web.mapper;

import com.yahir.ecommerce.product.domain.model.Category;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto.CategoryResponse;

public class CategoryWebMapper {
    public static CategoryResponse toResponse(Category domain) {
        return new CategoryResponse(
                domain.getId(),
                domain.getName()
        );
    }
}
