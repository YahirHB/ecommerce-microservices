package com.yahir.ecommerce.inventory_service.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(Long productId, Integer requested, Integer available){
        super(String.format("Insufficient stock for %d. Request: %d, Available: %d", productId,requested,available));
    }
}
