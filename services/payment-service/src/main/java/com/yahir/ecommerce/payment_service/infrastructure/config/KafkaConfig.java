package com.yahir.ecommerce.payment_service.infrastructure.config;

import com.yahir.ecommerce.payment_service.domain.event.DomainEvent;
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
    private String bootstrapServers; // Dirección del cluster/broker de Kafka

    @Value("${kafka.consumer.group-id}")
    private String groupId; // ID del grupo para repartir la carga entre consumidores

    // ─── Producer ────────────────────────────────────────
    @Bean//Crea este objeto una sola vez y tenlo listo para cuando alguien me lo pida
    public KafkaTemplate<String, DomainEvent> kafkaTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Convierte la llave (String) a bytes
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Convierte el objeto (DomainEvent) a JSON bytes
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Garantiza que todos los nodos confirmen la recepción (máxima seguridad)
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // Reintentos automáticos si el envío falla
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true); // Incluye el nombre de la clase en el header para que el receptor sepa qué objeto es
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props)); // Clase principal para enviar mensajes desde el código
    }

    // ─── Consumer ────────────────────────────────────────
    @Bean //Crea este objeto una sola vez y tenlo listo para cuando alguien me lo pida
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Convierte bytes de vuelta a String
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); // Convierte bytes JSON de vuelta a objeto Java
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Si es un grupo nuevo, lee desde el principio del historial
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.yahir.ecommerce.*"); // Permiso de seguridad para deserializar clases de este paquete
        return new DefaultKafkaConsumerFactory<>(props); // Define cómo se crean los clientes que escuchan mensajes
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory()); // Permite procesar mensajes en múltiples hilos (concurrencia)
        return factory; // Motor que gestiona las anotaciones @KafkaListener
    }
}