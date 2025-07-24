package org.example.coretrack.dto.material;

import java.util.List;

public class MaterialDetailResponse {
     private Long id;
    private String sku;
    private String name;
    private String shortDes;
    private String groupMaterial;
    private String status; 
    private String uom;
    private String imageUrl;

    private List <MaterialVariantInventoryResponse> variants;
    private List <MaterialSupplierResponse> suppliers;

    public MaterialDetailResponse(){}
    
    public MaterialDetailResponse(Long id, String sku, String name, String shortDes, String groupMaterial,
            String status, String uom, String imageUrl, List<MaterialVariantInventoryResponse> variants,
            List<MaterialSupplierResponse> suppliers) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.shortDes = shortDes;
        this.groupMaterial = groupMaterial;
        this.status = status;
        this.uom = uom;
        this.imageUrl = imageUrl;
        this.variants = variants;
        this.suppliers = suppliers;
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
    public String getShortDes() {
        return shortDes;
    }
    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }
    public String getGroupMaterial() {
        return groupMaterial;
    }
    public void setGroupMaterial(String groupMaterial) {
        this.groupMaterial = groupMaterial;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getUom() {
        return uom;
    }
    public void setUom(String uom) {
        this.uom = uom;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public List<MaterialVariantInventoryResponse> getVariants() {
        return variants;
    }
    public void setVariants(List<MaterialVariantInventoryResponse> variants) {
        this.variants = variants;
    }
    public List<MaterialSupplierResponse> getSuppliers() {
        return suppliers;
    }
    public void setSuppliers(List<MaterialSupplierResponse> suppliers) {
        this.suppliers = suppliers;
    }

    
}
