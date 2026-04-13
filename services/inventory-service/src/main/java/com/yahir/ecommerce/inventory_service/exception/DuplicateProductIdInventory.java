package com.yahir.ecommerce.inventory_service.exception;

public class DuplicateProductIdInventory extends RuntimeException{
    public DuplicateProductIdInventory(Long productId){
        super("Inventory already exists for product: " + productId);
    }
}
