package com.yahir.ecommerce.product.infrastructure.adapter.in.web.controller;

import com.yahir.ecommerce.product.domain.model.Product;
import com.yahir.ecommerce.product.domain.port.in.ProductUseCase;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto.CreateProductRequest;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto.ProductResponse;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.dto.UpdateProductRequest;
import com.yahir.ecommerce.product.infrastructure.adapter.in.web.mapper.ProductWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Gestión de productos del ecommerce")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    // ─── CRUD ────────────────────────────────────────────

    @Operation(summary = "Crear nuevo producto")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        Product product = ProductWebMapper.toDomain(request);
        Product savedProduct = productUseCase.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductWebMapper.toResponse(savedProduct));
    }

    @Operation(summary = "Actualizar producto")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest productRequest) {
        Product product = ProductWebMapper.toDomain(id, productRequest);
        Product productUpdate = productUseCase.updateProduct(id, product);
        return ResponseEntity.ok(ProductWebMapper.toResponse(productUpdate));
    }

    @Operation(summary = "Desactivar producto")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        productUseCase.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restaurar producto")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreProduct(@PathVariable Long id) {
        productUseCase.restoreProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar producto permanentemente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ─── CONSULTAS ───────────────────────────────────────

    @Operation(summary = "Buscar producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        Product product = productUseCase.findById(id);
        return ResponseEntity.ok(ProductWebMapper.toResponse(product));
    }

    @Operation(summary = "Buscar producto por SKU")
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> findBySku(@PathVariable String sku) {
        Product product = productUseCase.findBySku(sku);
        return ResponseEntity.ok(ProductWebMapper.toResponse(product));
    }

    @Operation(summary = "Listar todos los productos paginados")
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(productUseCase.findAllPaginated(pageable)
                .map(ProductWebMapper::toResponse));
    }

    @Operation(summary = "Listar productos por categoría")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> findByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(productUseCase.findByCategory(categoryId, pageable)
                .map(ProductWebMapper::toResponse));
    }
}