package org.example.coretrack.dto.material;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddMaterialRequest {
    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    @Size(max = 16, message = "SKU cannot exceed 16 characters") // Added size constraint
    private String sku;

    private String uom;
    private String shortDes;
    private String imageUrl; 

    private String materialGroupId;

    @Size(max = 100, message = "New Product Group name cannot exceed 100 characters.")
    private String newMaterialGroupName; 

    // Product Variants (Main Flow - Step 4)
    @Valid // Validate each variant in the list
    private List<MaterialVariantRequest> variants; // Optional, product might not have variants

    @Valid
    private List<MaterialSupplierRequest> suppliers;

    // constructors
    public AddMaterialRequest(){}

    // Getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getShortDes() {
        return shortDes;
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMaterialGroupId() {
        return materialGroupId;
    }

    public void setMaterialGroupId(String materialGroupId) {
        this.materialGroupId = materialGroupId;
    }

    public String getNewMaterialGroupName() {
        return newMaterialGroupName;
    }

    public void setNewMaterialGroupName(String newMaterialGroupName) {
        this.newMaterialGroupName = newMaterialGroupName;
    }

    public List<MaterialVariantRequest> getVariants() {
        return variants;
    }

    public void setVariants(List<MaterialVariantRequest> variants) {
        this.variants = variants;
    }

    public List<MaterialSupplierRequest> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<MaterialSupplierRequest> suppliers) {
        this.suppliers = suppliers;
    }

    
}
