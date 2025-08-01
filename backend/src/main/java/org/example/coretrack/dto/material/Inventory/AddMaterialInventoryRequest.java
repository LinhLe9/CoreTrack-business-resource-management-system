package org.example.coretrack.dto.material.Inventory;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public class AddMaterialInventoryRequest {
    @NotBlank(message = "Material SKU cannot be empty")
    private String materialVariantSku;

    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;

    // constructor
    public AddMaterialInventoryRequest(@NotBlank(message = "Material SKU cannot be empty") String materialVariantSku,
            BigDecimal currentStock, BigDecimal minAlertStock, BigDecimal maxStockLevel) {
        this.materialVariantSku = materialVariantSku;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
    }

    // getter & setter
    public AddMaterialInventoryRequest(){}

    public String getMaterialVariantSku() {
        return materialVariantSku;
    }

    public void setMaterialVariantSku(String materialVariantSku) {
        this.materialVariantSku = materialVariantSku;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getMinAlertStock() {
        return minAlertStock;
    }

    public void setMinAlertStock(BigDecimal minAlertStock) {
        this.minAlertStock = minAlertStock;
    }

    public BigDecimal getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(BigDecimal maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }
}
