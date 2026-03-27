package com.yahir.ecommerce.payment_service.domain.exception;

import com.yahir.ecommerce.payment_service.domain.model.PaymentStatus;

public class InvalidPaymentStateException extends RuntimeException{
    public InvalidPaymentStateException(Long paymentId, PaymentStatus currentStatus, PaymentStatus newStatus){
        super(String.format(
                "Payment [%d] cannot transition from %s to %s",
                paymentId, currentStatus, newStatus));
    }
}
