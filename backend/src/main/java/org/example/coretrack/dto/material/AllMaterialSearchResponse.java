package org.example.coretrack.dto.material;

import org.example.coretrack.model.material.Material;

public class AllMaterialSearchResponse {
    private Long id;
    private String sku;
    private String name;
    private String status; 
    private String imageUrl;

    // Constructor to convert from Entity to DTO
    public AllMaterialSearchResponse(Material material) {
        this.id = material.getId();
        this.sku = material.getSku();
        this.name = material.getName();
        this.status = material.getStatus().name();
        this.imageUrl = material.getImageUrl();
    }

    public AllMaterialSearchResponse(Long id, String sku, String name, String status, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public AllMaterialSearchResponse(){}

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
