package com.yahir.ecommerce.inventory_service.controller;

import com.yahir.ecommerce.inventory_service.dto.request.AdjustStockCommand;
import com.yahir.ecommerce.inventory_service.dto.request.ReleaseStockCommand;
import com.yahir.ecommerce.inventory_service.dto.request.ReserveStockBatchRequest;
import com.yahir.ecommerce.inventory_service.dto.request.ReserveStockCommand;
import com.yahir.ecommerce.inventory_service.dto.response.InventoryResponse;
import com.yahir.ecommerce.inventory_service.dto.response.ReservationResponse;
import com.yahir.ecommerce.inventory_service.dto.response.StockSummaryResponse;
import com.yahir.ecommerce.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Gestión de existencias y reservas")
public class InventoryController {
    private final InventoryService inventoryService;

    @Operation(summary = "Crear registro de inventario para un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventory created"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Inventory already exists")
    })
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody AdjustStockCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(command));
    }

    @Operation(summary = "Obtener inventario por ID de producto")
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @Operation(summary = "Obtener resumen de stock de un producto")
    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<StockSummaryResponse> getStockSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getStockSummary(productId));
    }

    @Operation(summary = "Reservar stock de un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock reserved"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserveStock(
            @Valid @RequestBody ReserveStockCommand command) {
        return ResponseEntity.ok(inventoryService.reserveStock(command));
    }

    @Operation(summary = "Reservar los stocks de una orden")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock reserved"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    @PostMapping("/reserve/all")
    public ResponseEntity<List<ReservationResponse>> reserveAllStock(
            @Valid @RequestBody ReserveStockBatchRequest request) {
        return ResponseEntity.ok(inventoryService.reserveAllStockByOrderId(request));
    }

    @Operation(summary = "Liberar una reserva de stock")
    @PostMapping("/release")
    public ResponseEntity<Void> releaseStock(
            @Valid @RequestBody ReleaseStockCommand command) {
        inventoryService.releaseStock(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Confirmar reservas por orderId — usado por Payment Service")
    @PutMapping("/confirm/order/{orderId}")
    public ResponseEntity<Void> confirmByOrderId(@PathVariable Long orderId) {
        inventoryService.confirmReservationByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Liberar reservas por orderId")
    @PutMapping("/release/order/{orderId}")
    public ResponseEntity<Void> releaseByOrderId(
            @PathVariable Long orderId) {
        inventoryService.releaseStockByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ajustar cantidad de stock")
    @PutMapping("/adjust")
    public ResponseEntity<InventoryResponse> adjustStock(
            @Valid @RequestBody AdjustStockCommand command) {
        return ResponseEntity.ok(inventoryService.adjustStock(command));
    }

    @Operation(summary = "Consigue todos los artículos con poco stock")
    @GetMapping("/low-stock")
    public ResponseEntity<List<StockSummaryResponse>> getLowStock() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
}
