package com.yahir.ecommerce.inventory_service.dto.response;

import com.yahir.ecommerce.inventory_service.Enum.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        String reservationId,
        Long productId,
        Long userId,
        Integer quantity,
        ReservationStatus status,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {}