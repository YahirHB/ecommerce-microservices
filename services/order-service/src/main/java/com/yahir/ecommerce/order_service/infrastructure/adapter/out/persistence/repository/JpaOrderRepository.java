package com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.repository;

import com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByCustomerId(Long customerId, Pageable pageable);
}
