package org.example.coretrack.dto.material;

import java.math.BigDecimal;

import org.example.coretrack.model.supplier.MaterialSupplier;

public class MaterialSupplierResponse {
    private Long supplierId;
    private String supplierName;
    private BigDecimal price; 
    private String currency; 
    private Integer leadTimeDays;
    private Integer minOrderQuantity;
    private String supplierMaterialCode;

    public MaterialSupplierResponse(Long supplierId, String supplierName, BigDecimal price, String currency,
            Integer leadTimeDays, Integer minOrderQuantity, String supplierMaterialCode) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.price = price;
        this.currency = currency;
        this.leadTimeDays = leadTimeDays;
        this.minOrderQuantity = minOrderQuantity;
        this.supplierMaterialCode = supplierMaterialCode;
    }

    public MaterialSupplierResponse(){}

    public MaterialSupplierResponse(MaterialSupplier ms){
        this.supplierId = ms.getSupplier().getId();
        this.supplierName = ms.getSupplier().getName();
        this.price = ms.getPrice();
        this.currency = ms.getCurrency();
        this.leadTimeDays = ms.getLeadTimeDays();
        this.minOrderQuantity = ms.getMinOrderQuantity();
        this.supplierMaterialCode = ms.getSupplierMaterialCode();
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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
