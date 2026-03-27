package com.yahir.ecommerce.order_service.infrastructure.adapter.out.kafka;

import com.yahir.ecommerce.order_service.domain.event.DomainEvent;
import com.yahir.ecommerce.order_service.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaPublisher implements EventPublisherPort {
    // Inyecta el Bean configurado; ya sabe cómo serializar a JSON y reintentar envíos
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic; // Tópico para cuando una orden nace

    @Value("${kafka.topics.order-status-changed}")
    private String orderStatusChangedTopic; // Tópico para cambios de estado posteriores

    @Override
    public void publish(DomainEvent event) {
        String topic = resolveTopic(event);

        // .send(tópico, llave, valor)
        // Usar orderId como "llave" garantiza que todos los eventos de una misma orden
        // caigan en la misma partición y se procesen en orden cronológico.
        kafkaTemplate.send(topic, event.orderId().toString(), event)
                .whenComplete((result, ex) -> { // Manejo asíncrono de la respuesta del broker
                    if (ex != null) {
                        // Error crítico: El mensaje no llegó a Kafka tras los reintentos
                        log.error("Failed to publish {} [orderId={}]: {}",
                                event.eventType(), event.orderId(), ex.getMessage());
                    } else {
                        // Éxito: El mensaje está persistido en el log de Kafka
                        log.info("{} published [orderId={}, partition={}, offset={}]",
                                event.eventType(),
                                event.orderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    private String resolveTopic(DomainEvent event) {
        // Estrategia de enrutamiento: decide el destino según el tipo de evento de dominio
        return switch (event.eventType()) {
            case "ORDER_CREATED"        -> orderCreatedTopic;
            case "ORDER_STATUS_CHANGED" -> orderStatusChangedTopic;
            default -> throw new IllegalArgumentException("Unknown event type: " + event.eventType());
        };
    }
}
