package com.yahir.ecommerce.inventory_service.mapper;

import com.yahir.ecommerce.inventory_service.Enum.InventoryStatus;
import com.yahir.ecommerce.inventory_service.dto.request.AdjustStockCommand;
import com.yahir.ecommerce.inventory_service.dto.response.InventoryResponse;
import com.yahir.ecommerce.inventory_service.dto.response.ReservationResponse;
import com.yahir.ecommerce.inventory_service.entity.InventoryEntity;
import com.yahir.ecommerce.inventory_service.entity.StockReservation;

public class InventoryMapper {
    public static InventoryEntity toEntity(AdjustStockCommand command){
        InventoryEntity inventoryEntity = InventoryEntity.builder()
                .productId(command.getProductId())
                .stockQuantity(command.getQuantity())
                .reservedQuantity(0)
                .minimumStock(5)
                .status(InventoryStatus.ACTIVE)
                .build();
        return inventoryEntity;
    }

    public static InventoryResponse toResponse(InventoryEntity inv){
        return new InventoryResponse(
                inv.getId(),
                inv.getProductId(),
                inv.getStockQuantity(),
                inv.getReservedQuantity(),
                inv.getAvailableQuantity(),
                inv.getMinimumStock(),
                inv.getStatus(),
                inv.getUpdatedAt()
        );
    }

    public static ReservationResponse toReservationResponse(StockReservation r) {
        return new ReservationResponse(
                r.getReservationId(),
                r.getProductId(),
                r.getUserId(),
                r.getQuantity(),
                r.getStatus(),
                r.getExpiresAt(),
                r.getCreatedAt()
        );
    }
}
