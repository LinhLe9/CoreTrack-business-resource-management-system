package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import java.util.List;

public class ProductInventoryDetailResponse {
    private Long variantId;
    private String productName;
    private String productSku;
    private String variantSku;
    private String variantName;
    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;
    private String inventoryStatus;
    private String productGroup;
    private String imageUrl;
    private List<InventoryTransactionResponse> logs;

    public ProductInventoryDetailResponse() {}

    public ProductInventoryDetailResponse(Long variantId, String productName, String productSku, 
                                       String variantSku, String variantName, BigDecimal currentStock,
                                       BigDecimal minAlertStock, BigDecimal maxStockLevel, 
                                       String inventoryStatus, String productGroup, String imageUrl,
                                       List<InventoryTransactionResponse> logs) {
        this.variantId = variantId;
        this.productName = productName;
        this.productSku = productSku;
        this.variantSku = variantSku;
        this.variantName = variantName;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
        this.inventoryStatus = inventoryStatus;
        this.productGroup = productGroup;
        this.imageUrl = imageUrl;
        this.logs = logs;
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

    public String getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<InventoryTransactionResponse> getLogs() {
        return logs;
    }

    public void setLogs(List<InventoryTransactionResponse> logs) {
        this.logs = logs;
    }
    
}
