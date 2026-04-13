package com.yahir.ecommerce.inventory_service.dto.event;

public record OrderStatusChangedEvent(
        Long orderId,
        Long customerId,
        OrderStatus previousStatus,
        OrderStatus newStatus
) implements DomainEvent {

    @Override
    public String eventType() { return "ORDER_STATUS_CHANGED"; }
}
