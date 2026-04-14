package com.yahir.ecommerce.order_service.domain.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Long id){
        super("Order not found with id: " + id);
    }
}
