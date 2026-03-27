package com.yahir.ecommerce.order_service.infrastructure.config;

import com.yahir.ecommerce.order_service.domain.event.DomainEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    // Método privado para evitar repetir código si creas varios Producers
    private Map<String, Object> baseProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Espera confirmación de todos los brokers (máxima fiabilidad)
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // Reintenta el envío automáticamente si hay micro-cortes
        // Agrega el nombre de la clase al Header del mensaje para que el receptor sepa qué objeto es
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        return props;
    }

    @Bean
    public KafkaTemplate<String, DomainEvent> kafkaTemplate() {
        // Encapsula la fábrica de productores en una plantilla fácil de usar para enviar mensajes
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(baseProducerProps()));
    }

    // ─── CONSUMER ────────────────────────────────
    // Configuración base para cualquier consumidor que crees en esta app
    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Si el grupo es nuevo, lee desde el primer mensaje disponible en el tópico
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Filtro de seguridad: solo permite convertir JSON a objetos que estén en este paquete
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.yahir.ecommerce.*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        // Crea la instancia que se encarga de generar los clientes consumidores reales
        return new DefaultKafkaConsumerFactory<>(baseConsumerProps());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        // Inyecta la fábrica para que Spring pueda gestionar los hilos de los @KafkaListener
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

