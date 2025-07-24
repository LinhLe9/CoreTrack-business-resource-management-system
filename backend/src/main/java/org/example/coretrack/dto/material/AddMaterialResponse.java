package org.example.coretrack.dto.material;

import java.time.LocalDateTime;
import java.util.List;

public class AddMaterialResponse {
    private String sku;
    private String name;
    private String shortDes;
    private String materialGroupName; // Product group name for display, instead of just ID
    private String imageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private String createdByUsername; // Creator's username for display
    private List<MaterialVariantResponse> variants;
    private List<MaterialSupplierResponse> suppliers;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public AddMaterialResponse(String sku, String name, String shortDes, String materialGroupName, 
            boolean isActive, String imageUrl, LocalDateTime createdAt, String createdByUsername,
            List<MaterialVariantResponse> variants, List<MaterialSupplierResponse> suppliers,
            LocalDateTime updatedAt, String updatedBy) {
        this.sku = sku;
        this.name = name;
        this.shortDes = shortDes;
        this.materialGroupName = materialGroupName;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.createdByUsername = createdByUsername;
        this.variants = variants;
        this.suppliers = suppliers;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public AddMaterialResponse(){}

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

    public String getMaterialGroupName() {
        return materialGroupName;
    }

    public void setMaterialGroupName(String materialGroupName) {
        this.materialGroupName = materialGroupName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
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

}
