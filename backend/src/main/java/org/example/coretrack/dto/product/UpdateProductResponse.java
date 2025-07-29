package org.example.coretrack.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateProductResponse {
    private String sku;
    private String name;
    private String description;
    private String group;
    private boolean isActive;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String createdBy;
    private List<UpdateProductVariantResponse> variants;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private BigDecimal price;
    private String currency;

    public UpdateProductResponse() {
    }

    public UpdateProductResponse(String sku, String name, String description, String group, 
                               boolean isActive, String imageUrl, LocalDateTime createdAt, 
                               String createdBy, List<UpdateProductVariantResponse> variants, 
                               LocalDateTime updatedAt, String updatedBy, BigDecimal price, String currency) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.group = group;
        this.isActive = isActive;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.variants = variants;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.price = price;
        this.currency = currency;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public List<UpdateProductVariantResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<UpdateProductVariantResponse> variants) {
        this.variants = variants;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
} 