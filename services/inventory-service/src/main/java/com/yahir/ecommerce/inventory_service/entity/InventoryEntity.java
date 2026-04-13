package com.yahir.ecommerce.inventory_service.entity;

import com.yahir.ecommerce.inventory_service.Enum.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock = 5;       // umbral para alerta de stock bajo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status = InventoryStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── lógica de dominio dentro de la entidad ──
    public Integer getAvailableQuantity() {
        return stockQuantity - reservedQuantity;
    }

    public boolean hasEnoughStock(Integer quantity) {
        return getAvailableQuantity() >= quantity;
    }

    public void reserve(Integer quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.reservedQuantity += quantity;
    }

    public void release(Integer quantity) {
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    }

    public void deduct(Integer quantity) {
        this.stockQuantity -= quantity;
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    }
}
