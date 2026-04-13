package com.yahir.ecommerce.inventory_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseStockCommand {

    @NotBlank(message = "Reservation ID is required")
    private String reservationId;
}
