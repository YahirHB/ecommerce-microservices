package com.yahir.ecommerce.product.domain.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException (Long productId,int quantity){
        super ("Insufficient stock for product id: " + productId + "requested: " + quantity);
    }
}
