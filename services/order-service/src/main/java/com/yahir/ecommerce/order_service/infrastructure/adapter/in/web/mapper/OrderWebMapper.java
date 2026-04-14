package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.mapper;

import com.yahir.ecommerce.order_service.aplication.command.CreateOrderCommand;
import com.yahir.ecommerce.order_service.aplication.command.OrderItemCommand;
import com.yahir.ecommerce.order_service.domain.model.Order;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderWebMapper {
    public CreateOrderCommand toCommand(CreateOrderRequest request){
        List<OrderItemCommand> itemCommands = request.items().stream()
                .map(i-> new OrderItemCommand(i.productId(), i.quantity()))
                .toList();
        return new CreateOrderCommand(request.customerId(), itemCommands);
    }
    public OrderResponse toResponse(Order order){

        List<OrderResponse.orderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderResponse.orderItemResponse(i.getId(),i.getProductId(),
                        i.getProductName(),i.getQuantity(),i.getUnitPrice(),i.getSubTotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getCustomerId(), items,
                order.getTotalAmount(), order.getStatus(),order.getCreatedAt());
    }

}
