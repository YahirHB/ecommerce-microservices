package com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.repository;

import com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaProductRepository extends JpaRepository <ProductEntity, Long> {
    Optional<ProductEntity> findBySku(String sku);

    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
}
