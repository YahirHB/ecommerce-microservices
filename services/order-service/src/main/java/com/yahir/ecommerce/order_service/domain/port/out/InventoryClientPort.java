package com.yahir.ecommerce.order_service.domain.port.out;

import com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto.*;

import java.util.List;

public interface InventoryClientPort {
    StockSummaryResponse getStockSummary(Long id);

    ReservationResponse reserveStock(ReserveStockRequest request);

    void releaseStock(ReleaseStockRequest request);

    List<ReservationResponse> reserveAllStock(ReserveStockBatchRequest request);
}
