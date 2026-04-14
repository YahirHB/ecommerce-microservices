package com.yahir.ecommerce.payment_service.domain.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id);
    }

    public PaymentNotFoundException(String field, Long value) {
        super(String.format("Payment not found with %s: %d", field, value));
    }
}
