package com.yahir.ecommerce.inventory_service.service;

import com.yahir.ecommerce.inventory_service.dto.request.AdjustStockCommand;
import com.yahir.ecommerce.inventory_service.dto.request.ReleaseStockCommand;
import com.yahir.ecommerce.inventory_service.dto.request.ReserveStockBatchRequest;
import com.yahir.ecommerce.inventory_service.dto.request.ReserveStockCommand;
import com.yahir.ecommerce.inventory_service.dto.response.InventoryResponse;
import com.yahir.ecommerce.inventory_service.dto.response.ReservationResponse;
import com.yahir.ecommerce.inventory_service.dto.response.StockSummaryResponse;

import java.util.List;

public interface InventoryService {
    InventoryResponse createInventory(AdjustStockCommand command);
    InventoryResponse getInventoryByProductId(Long productId);
    StockSummaryResponse getStockSummary(Long productId);
    ReservationResponse reserveStock(ReserveStockCommand command);
    void releaseStock(ReleaseStockCommand command);
    void confirmReservation(String reservationId, Long orderId);
    List<ReservationResponse> reserveAllStockByOrderId(ReserveStockBatchRequest command);
    void confirmReservationByOrderId(Long orderId);
    void releaseStockByOrderId(Long orderId);
    InventoryResponse adjustStock(AdjustStockCommand command);
    List<StockSummaryResponse> getLowStockItems();
}
