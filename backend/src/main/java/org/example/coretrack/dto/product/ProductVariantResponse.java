package org.example.coretrack.dto.product;

import java.util.List;

public class ProductVariantResponse {
    private Long id;
    private String sku; // Generated SKU for the variant
    private String name;
    private String shortDescription;
    private String imageUrl;
    private List<BOMItemResponse> bomItems;

    // Constructor to convert from Entity to DTO
    public ProductVariantResponse(Long id, String sku, String name, String shortDescription, String imageUrl, List<BOMItemResponse> bomItemResponses){
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.bomItems = bomItemResponses;
    }

    public ProductVariantResponse(){}

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

    public List<BOMItemResponse> getBomItems() {
        return bomItems;
    }

    public void setBomItems(List<BOMItemResponse> bomItems) {
        this.bomItems = bomItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
