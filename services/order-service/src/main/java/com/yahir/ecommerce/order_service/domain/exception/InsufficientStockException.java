package com.yahir.ecommerce.order_service.domain.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(Long productId, int quantity) {
        super(String.format(
                "Insufficient stock for product [%d], requested quantity: %d",
                productId, quantity));
    }
}
