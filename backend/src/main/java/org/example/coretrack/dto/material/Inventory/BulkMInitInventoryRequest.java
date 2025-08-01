package org.example.coretrack.dto.material.Inventory;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BulkMInitInventoryRequest {
    @NotEmpty(message = "Material variant SKUs cannot be empty")
    private List<String> materialVariantSkus;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal currentStock;

    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;

    public BulkMInitInventoryRequest(
            @NotEmpty(message = "Material variant SKUs cannot be empty") List<String> materialVariantSkus,
            @NotNull @DecimalMin("0.0") BigDecimal currentStock, BigDecimal minAlertStock, BigDecimal maxStockLevel) {
        this.materialVariantSkus = materialVariantSkus;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
    }

    public BulkMInitInventoryRequest(){}
    
    public List<String> getMaterialVariantSkus() {
        return materialVariantSkus;
    }
    public void setMaterialVariantSkus(List<String> materialVariantSkus) {
        this.materialVariantSkus = materialVariantSkus;
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
