package org.example.coretrack.dto.material;

import org.example.coretrack.model.material.MaterialVariant;

public class MaterialVariantResponse {
    private Long id;
    private String sku; 
    private String name;
    private String shortDescription; // Optional
    private String imageUrl;
    
    public MaterialVariantResponse(Long id, String sku, String name, String shortDescription, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
    } 

    public MaterialVariantResponse(){}

    public MaterialVariantResponse(MaterialVariant mv){
        this.id = mv.getId();
        this.sku = mv.getSku();
        this.name = mv.getName();
        this.shortDescription = mv.getShortDes();
        this.imageUrl = mv.getImageUrl();
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
}
