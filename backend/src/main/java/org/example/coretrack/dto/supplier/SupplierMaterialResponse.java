package org.example.coretrack.dto.supplier;

import java.math.BigDecimal;

import org.example.coretrack.model.supplier.MaterialSupplier;

public class SupplierMaterialResponse {
    private Long materialId;
    private String materialName;
    private BigDecimal price; 
    private String currency; 
    private Integer leadTimeDays;
    private Integer minOrderQuantity;
    private String supplierMaterialCode;

    public SupplierMaterialResponse(Long materialId, String materialName,
                                    BigDecimal price, String currency, Integer leadTimeDays, 
                                    Integer minOrderQuantity, String supplierMaterialCode) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.price = price;
        this.currency = currency;
        this.leadTimeDays = leadTimeDays;
        this.minOrderQuantity = minOrderQuantity;
        this.supplierMaterialCode = supplierMaterialCode;
    }

    public SupplierMaterialResponse (){  
    }

    public SupplierMaterialResponse(MaterialSupplier ms){
        this.materialId = ms.getMaterial().getId();
        this.materialName = ms.getMaterial().getName();
        this.price = ms.getPrice();
        this.currency = ms.getCurrency();
        this.leadTimeDays = ms.getLeadTimeDays();
        this.minOrderQuantity = ms.getMinOrderQuantity();
        this.supplierMaterialCode = ms.getSupplierMaterialCode();
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public Integer getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(Integer minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public String getSupplierMaterialCode() {
        return supplierMaterialCode;
    }

    public void setSupplierMaterialCode(String supplierMaterialCode) {
        this.supplierMaterialCode = supplierMaterialCode;
    }
    

}
