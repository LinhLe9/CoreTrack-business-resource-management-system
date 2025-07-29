package org.example.coretrack.dto.material;

import java.time.LocalDateTime;
import java.util.List;

public class UpdateMaterialResponse {
    private String sku;
    private String name;
    private String shortDes;
    private String groupMaterial;
    private boolean isActive;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String createdBy;
    private List<MaterialVariantResponse> variants;
    private List<MaterialSupplierResponse> suppliers;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Constructors
    public UpdateMaterialResponse() {}

    public UpdateMaterialResponse(String sku, String name, String shortDes, String groupMaterial,
                                boolean isActive, String imageUrl, LocalDateTime createdAt, String createdBy,
                                List<MaterialVariantResponse> variants, List<MaterialSupplierResponse> suppliers,
                                LocalDateTime updatedAt, String updatedBy) {
        this.sku = sku;
        this.name = name;
        this.shortDes = shortDes;
        this.groupMaterial = groupMaterial;
        this.isActive = isActive;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.variants = variants;
        this.suppliers = suppliers;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
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

    public String getShortDes() {
        return shortDes;
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public String getGroupMaterial() {
        return groupMaterial;
    }

    public void setGroupMaterial(String groupMaterial) {
        this.groupMaterial = groupMaterial;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<MaterialVariantResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<MaterialVariantResponse> variants) {
        this.variants = variants;
    }

    public List<MaterialSupplierResponse> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<MaterialSupplierResponse> suppliers) {
        this.suppliers = suppliers;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
} 