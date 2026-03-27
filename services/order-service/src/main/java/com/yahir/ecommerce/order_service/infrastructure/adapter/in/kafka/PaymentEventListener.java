package com.yahir.ecommerce.order_service.infrastructure.adapter.in.kafka;

import com.yahir.ecommerce.order_service.domain.event.PaymentCompletedEvent;
import com.yahir.ecommerce.order_service.domain.event.PaymentFailedEvent;
import com.yahir.ecommerce.order_service.domain.model.OrderStatus;
import com.yahir.ecommerce.order_service.domain.port.in.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final OrderUseCase orderUseCase; // Inyecta la lógica para actualizar los pedidos

    @KafkaListener(
            topics = "${kafka.topics.payment-completed}", // Escucha el canal de pagos EXITOSOS
            groupId = "${kafka.consumer.group-id}",
            properties = {
                    "spring.json.use.type.headers=false", // Ignora el paquete Java del emisor
                    "spring.json.value.default.type=com.yahir.ecommerce.order_service.domain.event.PaymentCompletedEvent" // Mapea a tu clase local
            }
    )
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Payment completed received [orderId={}]", event.orderId());
        // Acción de negocio: Si el pago fue OK, confirmamos el pedido en nuestra DB
        orderUseCase.updateStatus(event.orderId(), OrderStatus.CONFIRMED);
    }

    @KafkaListener(
            topics = "${kafka.topics.payment-failed}", // Escucha el canal de pagos FALLIDOS
            groupId = "${kafka.consumer.group-id}",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.yahir.ecommerce.order_service.domain.event.PaymentFailedEvent"
            }
    )
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Payment failed received [orderId={}, reason={}]",
                event.orderId(), event.reason());
        // Acción de negocio: Si el pago falló, cancelamos el pedido automáticamente
        orderUseCase.updateStatus(event.orderId(), OrderStatus.CANCELLED);
    }
}
