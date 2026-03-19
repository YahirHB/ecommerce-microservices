package com.yahir.ecommerce.order_service.aplication.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CreateOrderCommand {
    private Long customerId;
    private List<OrderItemCommand> items;
}
