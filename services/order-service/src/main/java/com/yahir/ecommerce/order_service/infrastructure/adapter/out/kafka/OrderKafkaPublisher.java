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
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;
    @Value("${kafka.topics.order-status-changed}")
    private String orderStatusChangedTopic;

    @Override
    public void publish(DomainEvent event) {
        String topic = resolveTopic(event);
        kafkaTemplate.send(topic, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} [orderId={}]: {}",
                                event.eventType(), event.orderId(), ex.getMessage());
                    } else {
                        log.info("{} published [orderId={}, partition={}, offset={}]",
                                event.eventType(),
                                event.orderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
    private String resolveTopic(DomainEvent event) {
        return switch (event.eventType()) {
            case "ORDER_CREATED"        -> orderCreatedTopic;
            case "ORDER_STATUS_CHANGED" -> orderStatusChangedTopic;
            default -> throw new IllegalArgumentException("Unknown event type: " + event.eventType());
        };
    }
}
