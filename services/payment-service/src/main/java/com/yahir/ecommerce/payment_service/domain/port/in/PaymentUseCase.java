package com.yahir.ecommerce.payment_service.domain.port.in;

import com.yahir.ecommerce.payment_service.aplication.command.ProcessPaymentCommand;
import com.yahir.ecommerce.payment_service.domain.model.Payment;

public interface PaymentUseCase{
    Payment processPayment(ProcessPaymentCommand command);
    Payment findById(Long paymentId);
    Payment findByOrderId(Long orderId);
}
