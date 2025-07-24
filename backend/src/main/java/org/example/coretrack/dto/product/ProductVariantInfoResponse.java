package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.ProductVariant;

public class ProductVariantInfoResponse {
    private Long id;
    private String sku; // Generated SKU for the variant
    private String name;
    private String shortDescription;
    private String imageUrl;

    // Constructor to convert from Entity to DTO
    public ProductVariantInfoResponse(Long id, String sku, String name, String shortDescription, String imageUrl){
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
    }

    public ProductVariantInfoResponse(ProductVariant productVariant){
        this.id = productVariant != null ? productVariant.getId() : null;
        this.sku = productVariant != null ? productVariant.getSku() : null;
        this.name = productVariant != null ? productVariant.getName() : null;
        this.shortDescription = productVariant != null ? productVariant.getDescription() : null;
        this.imageUrl = productVariant != null ? productVariant.getImageUrl() : null;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
