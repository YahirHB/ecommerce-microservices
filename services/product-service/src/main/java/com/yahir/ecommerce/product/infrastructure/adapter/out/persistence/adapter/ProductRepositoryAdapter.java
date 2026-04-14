package com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.adapter;

import com.yahir.ecommerce.product.domain.model.Product;
import com.yahir.ecommerce.product.domain.port.out.ProductRepositoryPort;
import com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import com.yahir.ecommerce.product.infrastructure.adapter.out.persistence.repository.JpaProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final JpaProductRepository productRepository;

    public ProductRepositoryAdapter(JpaProductRepository jpaProductRepository) {
        this.productRepository = jpaProductRepository;
    }

    // ─── CRUD ────────────────────────────────────────────

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = ProductMapper.toEntity(product);
        ProductEntity productEntitySave = productRepository.save(productEntity);
        return ProductMapper.toDomain(productEntitySave);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    // ─── CONSULTAS ───────────────────────────────────────

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository
                .findById(id)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository
                .findBySku(sku)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Page<Product> findAllPaginated(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Page<Product> findByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(ProductMapper::toDomain);
    }
}
