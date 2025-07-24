package org.example.coretrack.dto.material;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MaterialSupplierRequest{
    @NotNull(message = "Supplier ID cannot be empty for a material supplier.")
    private Long supplierId;
    
    private BigDecimal price; 

    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency; 
    private Integer leadTimeDays;
    private Integer minOrderQuantity;
    private String supplierMaterialCode;
    
    public MaterialSupplierRequest(
            @NotNull(message = "Supplier ID cannot be empty for a material supplier.") Long supplierId,
            BigDecimal price, @Size(min = 3, max = 3, message = "Currency must be 3 characters") String currency,
            Integer leadTimeDays, Integer minOrderQuantity, String supplierMaterialCode) {
        this.supplierId = supplierId;
        this.price = price;
        this.currency = currency;
        this.leadTimeDays = leadTimeDays;
        this.minOrderQuantity = minOrderQuantity;
        this.supplierMaterialCode = supplierMaterialCode;
    }

    public MaterialSupplierRequest(){}

    public Long getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
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