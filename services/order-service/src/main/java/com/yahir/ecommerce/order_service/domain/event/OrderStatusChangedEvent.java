package com.yahir.ecommerce.order_service.domain.event;


import com.yahir.ecommerce.order_service.domain.model.OrderStatus;

public record OrderStatusChangedEvent(
        Long orderId,
        Long customerId,
        OrderStatus previousStatus,
        OrderStatus newStatus
) implements DomainEvent {

    @Override
    public String eventType() { return "ORDER_STATUS_CHANGED"; }
}
