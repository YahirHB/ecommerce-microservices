package com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.mapper;

import com.yahir.ecommerce.order_service.domain.model.Order;
import com.yahir.ecommerce.order_service.domain.model.OrderItem;
import com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.entity.OrderEntity;
import com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderPersistenceMapper {

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .toList();

        Order order = new Order();
        order.setId(entity.getId());
        order.setCustomerId(entity.getCustomerId());
        order.setItems(items);
        order.setTotalAmount(entity.getTotalAmount());
        order.setStatus(entity.getStatus());
        order.setCreatedAt(entity.getCreatedAt());
        return order;
    }

    public OrderEntity toEntity(Order domain) {
        OrderEntity entity = OrderEntity.builder()
                .id(domain.getId())
                .customerId(domain.getCustomerId())
                .totalAmount(domain.getTotalAmount())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .build();

        List<OrderItemEntity> itemEntities = domain.getItems().stream()
                .map(item -> toEntityItem(item, entity))
                .toList();

        entity.setItems(itemEntities);
        return entity;
    }

    private OrderItem toDomainItem(OrderItemEntity entity) {
        OrderItem item = new OrderItem();
        item.setId(entity.getId());
        item.setProductId(entity.getProductId());
        item.setProductName(entity.getProductName());
        item.setQuantity(entity.getQuantity());
        item.setUnitPrice(entity.getUnitPrice());
        item.setSubTotal(entity.getSubTotal());
        return item;
    }

    private OrderItemEntity toEntityItem(OrderItem domain, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .id(domain.getId())
                .order(orderEntity)
                .productId(domain.getProductId())
                .productName(domain.getProductName())
                .quantity(domain.getQuantity())
                .unitPrice(domain.getUnitPrice())
                .subTotal(domain.getSubTotal())
                .build();
    }
}
