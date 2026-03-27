package com.yahir.ecommerce.payment_service.infrastructure.adapter.out.kafka;

import com.yahir.ecommerce.payment_service.domain.event.DomainEvent;
import com.yahir.ecommerce.payment_service.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j// Habilita los logs (log.info, log.error)
public class PaymentKafkaPublisher implements EventPublisherPort {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate; // Herramienta inyectada que ya sabe CÓMO enviar (trae tu config)

    @Value("${kafka.topics.payment-completed}")
    private String paymentCompletedTopic; // Nombre del canal (topic) para pagos exitosos

    @Value("${kafka.topics.payment-failed}")
    private String paymentFailedTopic; // Nombre del canal (topic) para pagos fallidos

    @Override
    public void publish(DomainEvent event) {
        String topic = resolveTopic(event); // Decide a qué canal enviar según el tipo de evento

        // .send(topic, key, payload) envía el mensaje de forma asíncrona
        kafkaTemplate.send(topic, event.orderId().toString(), event)
                .whenComplete((result, ex) -> { // Callback: se ejecuta cuando Kafka responde
                    if (ex != null) {
                        // El envío falló (después de agotar los 3 reintentos que configuraste)
                        log.error("Failed to publish {} [orderId={}]: {}",
                                event.eventType(), event.orderId(), ex.getMessage());
                    } else {
                        // El envío fue exitoso. El "result" trae metadatos como la partición y posición (offset)
                        log.info("{} published [orderId={}, partition={}, offset={}]",
                                event.eventType(),
                                event.orderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    private String resolveTopic(DomainEvent event) {
        // Lógica simple para elegir el String del tópico basado en el contenido del evento
        return switch (event.eventType()) {
            case "PAYMENT_COMPLETED" -> paymentCompletedTopic;
            case "PAYMENT_FAILED"    -> paymentFailedTopic;
            default -> throw new IllegalArgumentException(
                    "Unknown event type: " + event.eventType());
        };
    }
}
