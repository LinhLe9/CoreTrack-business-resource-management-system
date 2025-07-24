package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.Product;

public class AllProductSearchResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private String status; 
    private String imageUrl;

    // Constructors
    public AllProductSearchResponse() {
    }

    public AllProductSearchResponse(Long id, String sku, String name, String description,
                                    String status, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Constructor để chuyển đổi từ Entity sang DTO
    public AllProductSearchResponse(Product product) {
        this.sku = product.getSku();
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus().name(); 
        this.imageUrl = product.getImageUrl();
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
