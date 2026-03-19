package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.controller;

import com.yahir.ecommerce.order_service.domain.model.Order;
import com.yahir.ecommerce.order_service.domain.port.in.OrderUseCase;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.OrderResponse;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.UpdateOrderStatusRequest;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.mapper.OrderWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name="Orders", description = "Order management endpoints")
public class OrderController {
    private final OrderUseCase orderUseCase;
    private final OrderWebMapper webMapper;


    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request){
        Order order = orderUseCase.createOrder(webMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toResponse(order));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> findByIdOrder(@PathVariable Long id){
        return ResponseEntity.ok(webMapper.toResponse(orderUseCase.findById(id)));
    }

    @GetMapping
    @Operation(description = "Get all orders paginated")
    public ResponseEntity<Page<OrderResponse>> findAllPages(Pageable pageable){
        return ResponseEntity.ok(orderUseCase.findAll(pageable).map(webMapper::toResponse));
    }

    @GetMapping("/customerId/{customerId}")
    @Operation(summary = "Get Order by CustomerId")
    public ResponseEntity<Page<OrderResponse>> finByCustomer(@PathVariable Long customerId,Pageable pageable){
        return ResponseEntity.ok(orderUseCase.findByCustomerId(customerId,pageable).map(webMapper::toResponse));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        orderUseCase.updateStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }

}
