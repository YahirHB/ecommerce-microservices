package com.yahir.ecommerce.order_service.infrastructure.adapter.out.product.client;

import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.*;
import com.yahir.ecommerce.order_service.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "inventory-service",
        url = "${services.inventory.url}",
        configuration = FeignConfig.class
)
public interface InventoryFeignClient {
    @GetMapping("/api/v1/inventory/product/{productId}/summary")
    StockSummaryResponse getStockSummary(@PathVariable Long productId);

    @PostMapping("/api/v1/inventory/reserve")
    ReservationResponse reserveStock(@RequestBody ReserveStockRequest request);

    @PostMapping("/api/v1/inventory/release")
    void releaseStock(@RequestBody ReleaseStockRequest request);
    @PostMapping("/api/v1/inventory/reserve/all")
    List<ReservationResponse> reserveAllStock(@RequestBody ReserveStockBatchRequest request);

    @PutMapping("/api/v1/inventory/confirm/order/{orderId}")
    void confirmByOrderId(@PathVariable Long orderId);

    @PutMapping("/api/v1/inventory/release/order/{orderId}")
    void releaseByOrderId(@PathVariable Long orderId);

}
