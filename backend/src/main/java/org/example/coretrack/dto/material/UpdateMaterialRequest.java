package org.example.coretrack.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UpdateMaterialRequest {
    @NotBlank(message = "Material name is required")
    @Size(max = 255, message = "Material name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String shortDes;

    @NotBlank(message = "Unit of measure is required")
    private String uom;

    private String imageUrl;

    private String materialGroupId;
    private String newMaterialGroupName;

    private List<MaterialVariantRequest> variants;
    private List<MaterialSupplierRequest> suppliers;

    // Constructors
    public UpdateMaterialRequest() {}

    public UpdateMaterialRequest(String name, String shortDes, String uom, String imageUrl,
                               String materialGroupId, String newMaterialGroupName,
                               List<MaterialVariantRequest> variants, List<MaterialSupplierRequest> suppliers) {
        this.name = name;
        this.shortDes = shortDes;
        this.uom = uom;
        this.imageUrl = imageUrl;
        this.materialGroupId = materialGroupId;
        this.newMaterialGroupName = newMaterialGroupName;
        this.variants = variants;
        this.suppliers = suppliers;
    }

    // Getters and Setters
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