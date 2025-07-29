package org.example.coretrack.dto.product;

public class ProductVariantAutoCompleteResponse {
    private Long variantId;
    private String productName;
    private String productSku;
    private String variantSku;
    private String variantName;
    private String productGroup;

    public ProductVariantAutoCompleteResponse() {}

    public ProductVariantAutoCompleteResponse(Long variantId, String productName, String productSku, 
                                           String variantSku, String variantName, String productGroup) {
        this.variantId = variantId;
        this.productName = productName;
        this.productSku = productSku;
        this.variantSku = variantSku;
        this.variantName = variantName;
        this.productGroup = productGroup;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
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

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }
} 