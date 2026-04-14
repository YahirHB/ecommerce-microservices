package com.yahir.ecommerce.payment_service.domain.model;

import com.yahir.ecommerce.payment_service.domain.exception.InvalidPaymentStateException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Payment {
    private Long id;
    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment(Long orderId, Long customerId,
                   BigDecimal amount, String paymentMethod) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(){
        if (this.status != PaymentStatus.PENDING){
            new InvalidPaymentStateException(this.id, this.status, PaymentStatus.COMPLETED);
        }
        this.status = PaymentStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(this.id, this.status, PaymentStatus.FAILED);
        }
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
}
