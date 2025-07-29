package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public class AddProductInventoryRequest {
    @NotBlank(message = "Product SKU cannot be empty")
    private String productVariantSku;

    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;

    public AddProductInventoryRequest(){}

    public AddProductInventoryRequest(@NotBlank(message = "Product SKU cannot be empty") String productVariantSku,
            BigDecimal currentStock, BigDecimal minAlertStock, BigDecimal maxStockLevel) {
        this.productVariantSku = productVariantSku;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
    }

    public String getProductVariantSku() {
        return productVariantSku;
    }
    public void setProductVariantSku(String productVariantSku) {
        this.productVariantSku = productVariantSku;
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
