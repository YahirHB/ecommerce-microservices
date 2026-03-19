package com.yahir.ecommerce.order_service.domain.port.in;

import com.yahir.ecommerce.order_service.aplication.command.CreateOrderCommand;
import com.yahir.ecommerce.order_service.domain.model.Order;
import com.yahir.ecommerce.order_service.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderUseCase {
    // ─── CRUD ────────────────────────────────────────────
    Order createOrder(CreateOrderCommand createOrderCommand);

    // ─── ESTADO ──────────────────────────────────────────
    void updateStatus(Long orderId, OrderStatus newStatus);

    // ─── CONSULTAS ───────────────────────────────────────
    Order findById(Long orderId);
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
}
