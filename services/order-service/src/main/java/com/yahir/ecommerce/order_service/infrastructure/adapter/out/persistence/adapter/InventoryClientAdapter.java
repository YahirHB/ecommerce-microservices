package com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.adapter;

import com.yahir.ecommerce.order_service.domain.port.out.InventoryClientPort;
import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.*;
import com.yahir.ecommerce.order_service.infrastructure.adapter.out.product.client.InventoryFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryClientAdapter implements InventoryClientPort {

    private final InventoryFeignClient inventoryFeignClient;

    @Override
    public StockSummaryResponse getStockSummary(Long id) {
        return inventoryFeignClient.getStockSummary(id);
    }

    @Override
    public ReservationResponse reserveStock(ReserveStockRequest request) {
        return inventoryFeignClient.reserveStock(request);
    }

    @Override
    public void releaseStock(ReleaseStockRequest request) {
        inventoryFeignClient.releaseStock(request);
    }

    @Override
    public List<ReservationResponse> reserveAllStock(ReserveStockBatchRequest request) {
        List<ReservationResponse> s = inventoryFeignClient.reserveAllStock(request);
        s.stream().forEach(System.out::println);
       return s;
    }
}
