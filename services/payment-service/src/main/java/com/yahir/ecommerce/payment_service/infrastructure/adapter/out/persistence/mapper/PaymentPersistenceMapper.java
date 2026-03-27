package com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.mapper;

import com.yahir.ecommerce.payment_service.domain.model.Payment;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentPersistenceMapper {

    public Payment toDomain(PaymentEntity entity) {
        Payment payment = new Payment();
        payment.setId(entity.getId());
        payment.setOrderId(entity.getOrderId());
        payment.setCustomerId(entity.getCustomerId());
        payment.setAmount(entity.getAmount());
        payment.setStatus(entity.getStatus());
        payment.setPaymentMethod(entity.getPaymentMethod());
        payment.setFailureReason(entity.getFailureReason());
        payment.setCreatedAt(entity.getCreatedAt());
        payment.setUpdatedAt(entity.getUpdatedAt());
        return payment;
    }

    public PaymentEntity toEntity(Payment domain) {
        return PaymentEntity.builder()
                .id(domain.getId())
                .orderId(domain.getOrderId())
                .customerId(domain.getCustomerId())
                .amount(domain.getAmount())
                .status(domain.getStatus())
                .paymentMethod(domain.getPaymentMethod())
                .failureReason(domain.getFailureReason())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
