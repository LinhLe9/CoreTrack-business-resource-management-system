package org.example.coretrack.dto.product;

import java.time.LocalDateTime;

public class DeleteProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String status;
    private boolean isActive;
    private LocalDateTime deletedAt;
    private String deletedBy;

    public DeleteProductResponse() {
    }

    public DeleteProductResponse(Long id, String sku, String name, String status, boolean isActive, LocalDateTime deletedAt, String deletedBy) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.status = status;
        this.isActive = isActive;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
} 