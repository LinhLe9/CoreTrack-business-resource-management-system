package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BulkInitInventoryRequest {
    @NotEmpty(message = "Product variant SKUs cannot be empty")
    private List<String> productVariantSkus;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal currentStock;

    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;

    public BulkInitInventoryRequest() {}

    public BulkInitInventoryRequest(List<String> productVariantSkus, BigDecimal currentStock,
            BigDecimal minAlertStock, BigDecimal maxStockLevel) {
        this.productVariantSkus = productVariantSkus;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
    }

    public List<String> getProductVariantSkus() {
        return productVariantSkus;
    }

    public void setProductVariantSkus(List<String> productVariantSkus) {
        this.productVariantSkus = productVariantSkus;
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