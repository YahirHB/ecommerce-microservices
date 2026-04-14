package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public record ReservationResponse(
        String reservationId,
        Long productId,
        Long userId,
        Integer quantity,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {}