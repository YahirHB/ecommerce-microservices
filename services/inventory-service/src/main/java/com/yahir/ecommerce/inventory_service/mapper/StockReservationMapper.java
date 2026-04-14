package com.yahir.ecommerce.inventory_service.mapper;

import com.yahir.ecommerce.inventory_service.dto.response.ReservationResponse;
import com.yahir.ecommerce.inventory_service.entity.StockReservation;

public class StockReservationMapper {
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
