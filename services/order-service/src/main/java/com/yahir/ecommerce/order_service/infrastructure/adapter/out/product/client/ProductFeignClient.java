package com.yahir.ecommerce.order_service.infrastructure.adapter.out.product.client;

import com.yahir.ecommerce.order_service.infrastructure.adapter.out.product.client.dto.ProductResponse;
import com.yahir.ecommerce.order_service.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "product-service",
        url = "${services.product.url}",
        configuration = FeignConfig.class
)
public interface ProductFeignClient {

    @GetMapping("/api/v1/products/{id}")
    ProductResponse getProduct(@PathVariable Long id);

    @GetMapping("/api/v1/products/{id}/check-stock")
    boolean checkStock(@PathVariable Long id, @RequestParam int quantity);

    @PostMapping("/api/v1/products/{id}/reduce-stock")
    void reduceStock(@PathVariable Long id, @RequestParam int quantity);
}