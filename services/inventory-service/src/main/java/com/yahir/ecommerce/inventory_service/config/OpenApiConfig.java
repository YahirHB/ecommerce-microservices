package com.yahir.ecommerce.inventory_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .description("Stock management, reservations and inventory control")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Yahir")
                                .email("yahirhernandezbeltran02@gmail.com")));
    }
}