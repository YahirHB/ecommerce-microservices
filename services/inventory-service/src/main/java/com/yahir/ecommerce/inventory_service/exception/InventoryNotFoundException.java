package com.yahir.ecommerce.inventory_service.exception;

public class InventoryNotFoundException extends RuntimeException{
    public InventoryNotFoundException(Long productId){
        super("Inventory record not found for product: " + productId);
    }
}
