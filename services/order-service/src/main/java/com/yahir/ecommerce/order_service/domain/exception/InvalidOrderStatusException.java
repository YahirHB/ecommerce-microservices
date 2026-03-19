package com.yahir.ecommerce.order_service.domain.exception;

import com.yahir.ecommerce.order_service.domain.model.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException{
    public InvalidOrderStatusException(Long orderId, OrderStatus current, OrderStatus intended){
        super("Order " + orderId + "cannot transaction from " + current + "to " + intended);
    }
}
