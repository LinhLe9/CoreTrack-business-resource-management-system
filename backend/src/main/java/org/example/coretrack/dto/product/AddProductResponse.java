package org.example.coretrack.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AddProductResponse {
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private String productGroupName; // Product group name for display, instead of just ID
    private String imageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private String createdByUsername; // Creator's username for display
    private List<ProductVariantResponse> productVariants;
    private LocalDateTime updatedAt;
    private String updatedBy; 

    // Constructor to convert from Entity to DTO
    public AddProductResponse(String sku, String name, String description, BigDecimal price, String currency,
                                String productGroupName, String imageUrl, boolean isActive, List<ProductVariantResponse> productVariants, 
                                LocalDateTime createdAt, String createdByUsername, LocalDateTime updatedAt, String updatedBy) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.productGroupName = productGroupName;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.createdByUsername = createdByUsername;
        this.productVariants = productVariants;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public String getSku() { 
        return sku; 
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

    public void setSku(String sku) { 
        this.sku = sku; 
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getProductGroupName() { return productGroupName; }
    public void setProductGroupName(String productGroupName) { this.productGroupName = productGroupName; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public List<ProductVariantResponse> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariantResponse> productVariants) {
        this.productVariants = productVariants;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
