package com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.mapper;

import com.yahir.ecommerce.payment_service.aplication.command.ProcessPaymentCommand;
import com.yahir.ecommerce.payment_service.domain.model.Payment;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.dto.PaymentResponse;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.dto.ProcessPaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentWebMapper {

    public ProcessPaymentCommand toCommand(ProcessPaymentRequest request) {
        return new ProcessPaymentCommand(
                request.orderId(),
                request.customerId(),
                request.amount(),
                request.paymentMethod()
        );
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}