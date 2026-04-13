package com.yahir.ecommerce.inventory_service.client;

import com.yahir.ecommerce.inventory_service.exception.ProductNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "product-service", fallbackMethod = "fallbackProductExists")
    @Retry(name = "product-service")
    public boolean productExiste(Long productId){
        try {
            restTemplate.headForHeaders("/api/v1/products/{id}", productId);
            return true;
        }catch (HttpClientErrorException ex) {
            throw new ProductNotFoundException(productId);
        }catch (Exception ex){
            log.error("Error checking product existence for id {}: {}", productId, ex.getMessage());
            throw ex;
        }
    }

    // fallback si el circuit breaker está abierto
    public boolean fallbackProductExists (Long productId, Exception ex){
        // Si la excepción es porque el producto NO existe, NO debemos devolver true.
        // Debemos relanzarla para que el inventario NO se guarde.
        if (ex instanceof ProductNotFoundException) {
            throw (ProductNotFoundException) ex;
        }

        // Solo devolvemos true (o aplicamos resiliencia) si el servicio está caído (Timeout, 500, etc.)
        log.warn("Fallback: product-service no disponible para ID {}. Permitiendo por resiliencia.", productId);
        return true;
    }
}
