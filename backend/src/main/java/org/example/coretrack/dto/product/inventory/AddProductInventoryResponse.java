package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;

public class AddProductInventoryResponse {
    private String productVariantSku;
    private String productVariantName;
    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;
    private String productStatus;
    private String inventoryStatus;

    public AddProductInventoryResponse(){}

    public AddProductInventoryResponse(String productVariantSku, String productVariantName, BigDecimal currentStock,
            BigDecimal minAlertStock, BigDecimal maxStockLevel, String productStatus, String inventoryStatus) {
        this.productVariantSku = productVariantSku;
        this.productVariantName = productVariantName;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
        this.productStatus = productStatus;
        this.inventoryStatus = inventoryStatus;
    }

    public String getProductVariantSku() {
        return productVariantSku;
    }
    public void setProductVariantSku(String productVariantSku) {
        this.productVariantSku = productVariantSku;
    }
    public String getProductVariantName() {
        return productVariantName;
    }
    public void setProductVariantName(String productVariantName) {
        this.productVariantName = productVariantName;
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
    public String getProductStatus() {
        return productStatus;
    }
    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }
    public String getInventoryStatus() {
        return inventoryStatus;
    }
    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    
}
