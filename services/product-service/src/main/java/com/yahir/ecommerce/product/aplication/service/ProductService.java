package com.yahir.ecommerce.product.aplication.service;

import com.yahir.ecommerce.product.domain.exception.DuplicateSkuException;
import com.yahir.ecommerce.product.domain.exception.ProductNotDeactivatedException;
import com.yahir.ecommerce.product.domain.exception.ProductNotFoundException;
import com.yahir.ecommerce.product.domain.model.Product;
import com.yahir.ecommerce.product.domain.model.ProductStatus;
import com.yahir.ecommerce.product.domain.port.in.ProductUseCase;
import com.yahir.ecommerce.product.domain.port.out.ProductRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public ProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    // ─── CRUD ────────────────────────────────────────────

    @Transactional
    @Override
    public Product createProduct(Product product) {
        productRepositoryPort.findBySku(product.getSku())
                .ifPresent(p -> {
                    throw new DuplicateSkuException(product.getSku());
                });
        return productRepositoryPort.save(product);
    }

    @Transactional
    @Override
    public Product updateProduct(Long id, Product product) {
        productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setId(id);
        return productRepositoryPort.save(product);
    }

    @Transactional
    @Override
    public void deactivateProduct(Long id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setStatus(ProductStatus.INACTIVE);
        productRepositoryPort.save(product);
    }

    @Transactional
    @Override
    public void restoreProduct(Long id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setStatus(ProductStatus.ACTIVE);
        productRepositoryPort.save(product);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (product.getStatus() != ProductStatus.INACTIVE) {
            throw new ProductNotDeactivatedException(id);
        }
        productRepositoryPort.deleteById(id);
    }

    // ─── CONSULTAS ───────────────────────────────────────

    @Transactional(readOnly = true)
    @Override
    public Product findById(Long id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Product findBySku(String sku) {
        return productRepositoryPort.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Product> findAllPaginated(Pageable pageable) {
        return productRepositoryPort.findAllPaginated(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Product> findByCategory(Long categoryId, Pageable pageable) {
        return productRepositoryPort.findByCategory(categoryId, pageable);
    }

}
