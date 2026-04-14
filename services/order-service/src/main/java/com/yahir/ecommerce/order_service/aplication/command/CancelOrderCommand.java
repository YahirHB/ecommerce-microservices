package com.yahir.ecommerce.order_service.aplication.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CancelOrderCommand {
    private Long id;
}
