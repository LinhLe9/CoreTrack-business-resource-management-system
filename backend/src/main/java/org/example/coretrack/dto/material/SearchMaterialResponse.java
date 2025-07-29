package org.example.coretrack.dto.material;

import org.example.coretrack.model.material.Material;

public class SearchMaterialResponse {
    private Long id;
    private String sku;
    private String name;
    private String groupMaterial;
    private String uom;
    private String status; 
    private String imageUrl;

    // Constructors
    public SearchMaterialResponse() {
    }

    public SearchMaterialResponse(Long id, String sku, String name, String groupMaterial, 
                                String uom, String status, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.groupMaterial = groupMaterial;
        this.status = status;
        this.uom = uom;
        this.imageUrl = imageUrl;
    }

    // Constructor to convert from Entity to DTO
    public SearchMaterialResponse(Material material) {
        this.id = material.getId();
        this.sku = material.getSku();
        this.name = material.getName();
        this.groupMaterial = material.getGroup() != null ? material.getGroup().getName() : null;
        this.status = material.getStatus() != null ? material.getStatus().name() : null; 
        this.uom = material.getUom() != null ? material.getUom().name() : null;
        this.imageUrl = material.getImageUrl(); 
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

    public String getGroupMaterial() {
        return groupMaterial;
    }

    public void setGroupMaterial(String groupMaterial) {
        this.groupMaterial = groupMaterial;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
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

   
    // Getters and Setters
    
}
