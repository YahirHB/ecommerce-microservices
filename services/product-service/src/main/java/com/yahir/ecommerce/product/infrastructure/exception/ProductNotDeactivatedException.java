package com.yahir.ecommerce.product.infrastructure.exception;

public class ProductNotDeactivatedException extends RuntimeException{
    public ProductNotDeactivatedException(Long id){
        super("Product must be INACTIVE before deleting, id: " + id);
    }
}
