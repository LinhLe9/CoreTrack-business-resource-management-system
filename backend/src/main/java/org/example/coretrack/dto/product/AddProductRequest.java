package org.example.coretrack.dto.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class AddProductRequest {
    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    private String description;
    private String imageUrl; 

    private String productGroupId;

    @Size(max = 100, message = "New Product Group name cannot exceed 100 characters.")
    private String newProductGroupName; 

    @PositiveOrZero(message = "Price must be a non-negative number")
    @NotNull(message = "Price cannot be empty")
    private BigDecimal price;

    private String currency;

    @Size(max = 16, message = "SKU cannot exceed 16 characters") // Added size constraint
    private String sku;

    // Fields like id, createdAt, createdBy, isActive are not needed in the request
    // as they will be automatically generated/managed by the system.

    // Product Variants (Main Flow - Step 4)
    @Valid // Validate each variant in the list
    private List<ProductVariantRequest> variants; // Optional, product might not have variants
    
    // BOM Items for default variant (when no explicit variants are provided)
    @Valid
    private List<BOMItemRequest> bomItems;

    // Constructors, Getters and Setters
    public AddProductRequest() {}

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

    public String getProductGroupId() { 
        return productGroupId; 
    }

    public void setProductGroupId(String productGroupId) { 
        this.productGroupId = productGroupId; 
    }

    public BigDecimal getPrice() { 
        return price; 
    }

    public void setPrice(BigDecimal price) { 
        this.price = price; 
    }

    public void setImageUrl (String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl (){
        return imageUrl;
    }

    public String getNewProductGroupName() {
        return newProductGroupName;
    }

    public void setNewProductGroupName(String newProductGroupName) {
        this.newProductGroupName = newProductGroupName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<ProductVariantRequest> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantRequest> variants) {
        this.variants = variants;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public List<BOMItemRequest> getBomItems() {
        return bomItems;
    }
    
    public void setBomItems(List<BOMItemRequest> bomItems) {
        this.bomItems = bomItems;
    }
}
