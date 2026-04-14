package com.yahir.ecommerce.product.domain.model;

import java.time.LocalDate;

public class Category {
    private Long id;
    private String name;
    private String description;
    private LocalDate createdAt;

    public Category(Long id, String name,String description, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.description=description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
