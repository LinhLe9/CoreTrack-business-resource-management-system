package org.example.coretrack.dto.material.Inventory;

import java.math.BigDecimal;

public class AddMaterialInventoryResponse {
    private String materialVariantSku;
    private String materialVariantName;
    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;
    private String materialStatus;
    private String inventoryStatus;

    // constructor
    public AddMaterialInventoryResponse(String materialVariantSku, String materialVariantName, BigDecimal currentStock,
            BigDecimal minAlertStock, BigDecimal maxStockLevel, String materialStatus, String inventoryStatus) {
        this.materialVariantSku = materialVariantSku;
        this.materialVariantName = materialVariantName;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
        this.materialStatus = materialStatus;
        this.inventoryStatus = inventoryStatus;
    }

    public AddMaterialInventoryResponse(){}

    // getter & setter
    public String getMaterialVariantSku() {
        return materialVariantSku;
    }
    public void setMaterialVariantSku(String materialVariantSku) {
        this.materialVariantSku = materialVariantSku;
    }
    public String getMaterialVariantName() {
        return materialVariantName;
    }
    public void setMaterialVariantName(String materialVariantName) {
        this.materialVariantName = materialVariantName;
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
    public String getMaterialStatus() {
        return materialStatus;
    }
    public void setMaterialStatus(String materialStatus) {
        this.materialStatus = materialStatus;
    }
    public String getInventoryStatus() {
        return inventoryStatus;
    }
    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }
}
