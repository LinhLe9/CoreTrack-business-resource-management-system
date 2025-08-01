package org.example.coretrack.dto.material;

public class MaterialVariantAutoCompleteResponse {
    private Long variantId;
    private String materialName;
    private String materialSku;
    private String variantSku;
    private String variantName;
    private String materialGroup;

    public MaterialVariantAutoCompleteResponse() {
    }

    public MaterialVariantAutoCompleteResponse(Long variantId, String materialName, String materialSku, 
                                           String variantSku, String variantName, String materialGroup) {
        this.variantId = variantId;
        this.materialName = materialName;
        this.materialSku = materialSku;
        this.variantSku = variantSku;
        this.variantName = variantName;
        this.materialGroup = materialGroup;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialSku() {
        return materialSku;
    }

    public void setMaterialSku(String materialSku) {
        this.materialSku = materialSku;
    }

    public String getVariantSku() {
        return variantSku;
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
    }
} 