package org.example.coretrack.dto.product;

import java.util.List;

public class UpdateProductVariantResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private String imageUrl;
    private List<BOMItemResponse> bomItems;

    public UpdateProductVariantResponse() {
    }

    public UpdateProductVariantResponse(Long id, String sku, String name, String description, String imageUrl, List<BOMItemResponse> bomItems) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.bomItems = bomItems;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
} 