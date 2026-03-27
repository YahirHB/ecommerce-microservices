package com.yahir.ecommerce.payment_service.aplication.service;

import com.yahir.ecommerce.payment_service.aplication.command.ProcessPaymentCommand;
import com.yahir.ecommerce.payment_service.domain.event.PaymentCompletedEvent;
import com.yahir.ecommerce.payment_service.domain.event.PaymentFailedEvent;
import com.yahir.ecommerce.payment_service.domain.exception.PaymentNotFoundException;
import com.yahir.ecommerce.payment_service.domain.model.Payment;
import com.yahir.ecommerce.payment_service.domain.port.in.PaymentUseCase;
import com.yahir.ecommerce.payment_service.domain.port.out.EventPublisherPort;
import com.yahir.ecommerce.payment_service.domain.port.out.PaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService implements PaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    public Payment processPayment(ProcessPaymentCommand command) {
        // 1. Crear el pago en estado PENDING
        Payment payment = new Payment(
                command.orderId(),
                command.customerId(),
                command.amount(),
                command.paymentMethod()
        );

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created [id={}, orderId={}]", saved.getId(), saved.getOrderId());

        // 2. Simular procesamiento — aquí enchufas Stripe/PayPal en el futuro
        return simulatePaymentProcessing(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment findById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("orderId", orderId));
    }

    // ─── Simulación ──────────────────────────────────────
    // En producción esto sería un PaymentGatewayPort (Stripe, PayPal, etc.)
    private Payment simulatePaymentProcessing(Payment payment) {
        try {
            // Simula éxito — puedes cambiar esto para probar el flujo de fallo
            // --- SIMULACIÓN DE FALLO ---
            // Si el monto es exactamente 21499.00, lanzamos un error para probar
            if (payment.getAmount().doubleValue() == 21499.00) {
                throw new RuntimeException("Simulated Gateway Timeout - Stripe is down");
            }
            // ---------------------------
            payment.complete();
            paymentRepository.save(payment);

            eventPublisher.publish(new PaymentCompletedEvent(
                    payment.getOrderId(),
                    payment.getId(),
                    payment.getCustomerId(),
                    payment.getAmount()
            ));

            log.info("Payment completed [id={}, orderId={}]",
                    payment.getId(), payment.getOrderId());

        } catch (Exception ex) {
            payment.fail(ex.getMessage());
            paymentRepository.save(payment);

            eventPublisher.publish(new PaymentFailedEvent(
                    payment.getOrderId(),
                    payment.getId(),
                    ex.getMessage()
            ));

            log.error("Payment failed [id={}, orderId={}, reason={}]",
                    payment.getId(), payment.getOrderId(), ex.getMessage());
        }

        return payment;
    }
}
