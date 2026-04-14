package com.yahir.ecommerce.product.domain.port.in;

import com.yahir.ecommerce.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductUseCase {
    // ─── CRUD ────────────────────────────────────────────
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deactivateProduct(Long id);
    void restoreProduct(Long id);
    void deleteProduct(Long id);

    // ─── CONSULTAS ───────────────────────────────────────
    Product findById(Long id);
    Product findBySku(String sku);
    Page<Product> findAllPaginated(Pageable pageable);
    Page<Product> findByCategory(Long categoryId, Pageable pageable);

}
