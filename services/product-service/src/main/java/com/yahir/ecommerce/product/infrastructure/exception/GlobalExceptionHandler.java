package com.yahir.ecommerce.product.infrastructure.exception;

import com.yahir.ecommerce.product.domain.exception.DuplicateSkuException;
import com.yahir.ecommerce.product.domain.exception.InsufficientStockException;
import com.yahir.ecommerce.product.domain.exception.ProductNotDeactivatedException;
import com.yahir.ecommerce.product.domain.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateSkuException.class)
    public ResponseEntity<String> handleDuplicateSku(DuplicateSkuException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProductNotDeactivatedException.class)
    public ResponseEntity<String> handleProductNotDeactivated(ProductNotDeactivatedException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStock(InsufficientStockException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}
