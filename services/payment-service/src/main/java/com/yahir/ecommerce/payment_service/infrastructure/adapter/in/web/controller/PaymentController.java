package com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.controller;

import com.yahir.ecommerce.payment_service.domain.port.in.PaymentUseCase;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.dto.PaymentResponse;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.dto.ProcessPaymentRequest;
import com.yahir.ecommerce.payment_service.infrastructure.adapter.in.web.mapper.PaymentWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;
    private final PaymentWebMapper mapper;

    @PostMapping
    @Operation(summary = "Process a payment manually")
    public ResponseEntity<PaymentResponse> process(
            @Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(paymentUseCase.processPayment(mapper.toCommand(request))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(paymentUseCase.findById(id)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    public ResponseEntity<PaymentResponse> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(mapper.toResponse(paymentUseCase.findByOrderId(orderId)));
    }
}