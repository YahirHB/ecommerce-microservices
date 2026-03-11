package com.yahir.ecommerce.product.infrastructure.exception;

public class DuplicateSkuException extends RuntimeException{
    public DuplicateSkuException(String sku) {
        super("Product with SKU already exists: " + sku);
    }
}
