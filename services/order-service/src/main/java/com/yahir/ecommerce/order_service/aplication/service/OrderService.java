package com.yahir.ecommerce.order_service.aplication.service;

import com.yahir.ecommerce.order_service.aplication.command.CreateOrderCommand;
import com.yahir.ecommerce.order_service.aplication.command.OrderItemCommand;
import com.yahir.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.yahir.ecommerce.order_service.domain.event.OrderStatusChangedEvent;
import com.yahir.ecommerce.order_service.domain.exception.InsufficientStockException;
import com.yahir.ecommerce.order_service.domain.exception.OrderNotFoundException;
import com.yahir.ecommerce.order_service.domain.model.Order;
import com.yahir.ecommerce.order_service.domain.model.OrderItem;
import com.yahir.ecommerce.order_service.domain.model.OrderStatus;
import com.yahir.ecommerce.order_service.domain.model.ProductInfo;
import com.yahir.ecommerce.order_service.domain.port.in.OrderUseCase;
import com.yahir.ecommerce.order_service.domain.port.out.EventPublisherPort;
import com.yahir.ecommerce.order_service.domain.port.out.OrderRepositoryPort;
import com.yahir.ecommerce.order_service.domain.port.out.ProductClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService implements OrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final ProductClientPort productClient;
    private final EventPublisherPort eventPublisher;

    @Override
    public Order createOrder(CreateOrderCommand command) {
        // 1. Validar stock de cada producto
        for (OrderItemCommand i:command.getItems()) {
            boolean hasStock = productClient.checkoutStock(i.getProductId(), i.getQuantity());
            if (!hasStock){
                throw new InsufficientStockException(i.getProductId(), i.getQuantity());
            }
        }

        // 2. Reducir stock
        for (OrderItemCommand i: command.getItems()){
            productClient.reduceStock(i.getProductId(), i.getQuantity());
        }

        // 3. Construir los OrderItems del domain
        List<OrderItem> items = command.getItems().stream()
                .map(this::toOrderItem)
                .toList();

        // 4. Construir la Order del domain
        Order order = new Order(
                null,
                command.getCustomerId(),
                items,
                OrderStatus.CREATED,
                LocalDateTime.now()
        );

        // 5. Persistir
        Order saved = orderRepository.save(order);

        // 6. Publicar evento
        eventPublisher.publish(new OrderCreatedEvent(
                saved.getId(),
                saved.getCustomerId(),
                saved.getTotalAmount()
        ));
        return saved;
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderOrThrow(orderId);
        OrderStatus previousStatus = order.getStatus();

        order.transitionTo(newStatus);
        orderRepository.save(order);

        eventPublisher.publish(new OrderStatusChangedEvent(
                order.getId(),
                order.getCustomerId(),
                previousStatus,
                newStatus
        ));
    }

    @Transactional(readOnly = true)
    @Override
    public Order findById(Long orderId) {
        return getOrderOrThrow(orderId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId,pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    // ─── Helpers ─────────────────────────────────────────

    private Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderItem toOrderItem(OrderItemCommand cmd) {
        ProductInfo info = productClient.getProductInfo(cmd.getProductId());
        return new OrderItem(null, info.productId(), info.name(), cmd.getQuantity(), info.price());
    }
}
