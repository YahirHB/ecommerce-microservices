package com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.repository;

import com.yahir.ecommerce.payment_service.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByOrderId(Long orderId);
}
