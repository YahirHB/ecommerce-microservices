package com.yahir.ecommerce.order_service.infrastructure.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL; // cambiar a BASIC en producción
    }
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new IllegalArgumentException(
                        "Product not found — " + methodKey);
            }
            return new RuntimeException(
                    "Feign error [" + response.status() + "] on " + methodKey);
        };
    }
}
