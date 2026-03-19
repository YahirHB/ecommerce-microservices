package com.yahir.ecommerce.order_service.domain.port.out;

import com.yahir.ecommerce.order_service.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryPort {
    // ─── CRUD ────────────────────────────────────────────
    Order save(Order order);
    void deleteById(Long id);

    // ─── CONSULTAS ───────────────────────────────────────
    Optional<Order> findById(Long id);
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
}
