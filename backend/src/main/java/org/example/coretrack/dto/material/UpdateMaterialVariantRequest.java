package org.example.coretrack.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateMaterialVariantRequest {
    private Long id; // For existing variants
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Short description must not exceed 1000 characters")
    private String shortDescription;
    
    private String imageUrl;
    
    // SKU field - null for new variants, existing SKU for updates
    private String sku;
    
    public UpdateMaterialVariantRequest() {}
    
    public UpdateMaterialVariantRequest(Long id, String name, String shortDescription, String imageUrl, String sku) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.sku = sku;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    // Helper method to check if this is a new variant
    public boolean isNewVariant() {
        return sku == null || sku.trim().isEmpty();
    }
    
    // Helper method to check if this is an existing variant
    public boolean isExistingVariant() {
        return sku != null && !sku.trim().isEmpty();
    }
} 