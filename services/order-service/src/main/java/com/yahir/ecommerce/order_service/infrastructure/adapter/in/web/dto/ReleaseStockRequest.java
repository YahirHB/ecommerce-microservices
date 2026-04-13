package com.yahir.ecommerce.order_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseStockRequest {

    @NotBlank(message = "Reservation ID is required")
    private String reservationId;
}
