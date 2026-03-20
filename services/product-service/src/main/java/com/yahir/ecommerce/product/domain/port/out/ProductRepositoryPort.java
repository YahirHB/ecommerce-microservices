package com.yahir.ecommerce.product.domain.port.out;

import com.yahir.ecommerce.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepositoryPort {

    // ─── CRUD ────────────────────────────────────────────
    Product save(Product product);
    void deleteById(Long id);

    // ─── CONSULTAS ───────────────────────────────────────
    Optional<Product> findById(Long id);
    Optional<Product> findBySku(String sku);
    Page<Product> findAllPaginated(Pageable pageable);
    Page<Product> findByCategory(Long categoryId, Pageable pageable);
}