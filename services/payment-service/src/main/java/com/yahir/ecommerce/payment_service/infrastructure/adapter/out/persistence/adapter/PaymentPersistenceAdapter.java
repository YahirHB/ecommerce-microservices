package com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.adapter;

import com.yahir.ecommerce.payment_service.domain.model.Payment;
import com.yahir.ecommerce.payment_service.domain.port.out.PaymentRepositoryPort;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.mapper.PaymentPersistenceMapper;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.repository.JpaPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {
    private final JpaPaymentRepository paymentRepository;
    private final PaymentPersistenceMapper mapper;

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(paymentRepository.save(mapper.toEntity(payment)));
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).map(mapper::toDomain);
    }
}
