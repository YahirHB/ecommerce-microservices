package com.yahir.ecommerce.order_service.domain.model;

import com.yahir.ecommerce.order_service.domain.exception.InvalidOrderStatusException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Order {
    private Long id;
    private Long customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public Order(Long id, Long customerId, List<OrderItem> items,
                 OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
        this.totalAmount = calculateTotal();
    }

    private BigDecimal calculateTotal(){
        return items.stream().map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void transitionTo(OrderStatus newStatus) {
        if (!isValidTransition(this.status, newStatus)) {
            throw new InvalidOrderStatusException(this.id, this.status, newStatus);
        }
        this.status = newStatus;
    }
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case CREATED   -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.PAID      || next == OrderStatus.CANCELLED;
            case PAID      -> next == OrderStatus.SHIPPED   || next == OrderStatus.CANCELLED;
            case SHIPPED   -> next == OrderStatus.DELIVERED;
            default        -> false; // DELIVERED y CANCELLED son estados terminales
        };
    }
}
