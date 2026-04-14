package com.yahir.ecommerce.payment_service.infrastructure.adapter.in.kafka;

import com.yahir.ecommerce.payment_service.aplication.command.ProcessPaymentCommand;
import com.yahir.ecommerce.payment_service.domain.event.OrderCreatedEvent;
import com.yahir.ecommerce.payment_service.domain.port.in.PaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final PaymentUseCase paymentUseCase; // Inyección de la lógica de negocio (tu caso de uso)

    @KafkaListener(
            topics = "${kafka.topics.order-pending}", // El "canal" que este método se queda escuchando
            groupId = "${kafka.consumer.group-id}",   // Identifica a este consumidor para repartir mensajes
            properties = {
                    // Ignora el "Type ID" que envía el emisor en el header (evita errores de paquetes distintos)
                    "spring.json.use.type.headers=false",
                    // Fuerza a que el JSON se convierta directamente a esta clase local
                    "spring.json.value.default.type=com.yahir.ecommerce.payment_service.domain.event.OrderCreatedEvent"
            }
    )
    public void onOrderCreated(OrderCreatedEvent event) { // Se ejecuta automáticamente cada vez que llega un mensaje
        log.info("Order created received [orderId={}], processing payment...",
                event.orderId());

        // Transforma el Evento (datos que llegaron) en un Command (acción para tu dominio)
        paymentUseCase.processPayment(new ProcessPaymentCommand(
                event.orderId(),
                event.customerId(),
                event.totalAmount(),
                "AUTOMATIC" // Dato fijo que no viene en el evento, pero tu lógica requiere
        ));
    }
}