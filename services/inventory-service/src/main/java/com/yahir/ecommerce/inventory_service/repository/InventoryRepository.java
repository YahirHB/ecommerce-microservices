package com.yahir.ecommerce.inventory_service.repository;

import com.yahir.ecommerce.inventory_service.Enum.InventoryStatus;
import com.yahir.ecommerce.inventory_service.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> findByProductId(Long id);

    boolean existsByProductId(Long productId);

    @Query("SELECT i FROM InventoryEntity i WHERE i.stockQuantity <= i.minimumStock AND status = :status")
    List<InventoryEntity> findLowStockItems(InventoryStatus status);

    @Query("SELECT i FROM InventoryEntity i WHERE (i.stockQuantity - i.reservedQuantity) >= :quantity AND i.productId = :productId")
    Optional<InventoryEntity> findAvailableStock(Long productId, Integer quantity);

}
