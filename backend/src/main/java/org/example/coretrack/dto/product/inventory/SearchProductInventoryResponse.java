package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import org.example.coretrack.model.product.inventory.InventoryStatus;

public class SearchProductInventoryResponse {
    private Long id;
    private String sku;
    private String name;
    private String groupProduct;
    private InventoryStatus inventoryStatus; 
    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;
    private String imageUrl;

    public SearchProductInventoryResponse(Long id, String sku, String name, String groupProduct, InventoryStatus inventoryStatus,
            BigDecimal currentStock, BigDecimal minAlertStock, BigDecimal maxStockLevel, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.groupProduct = groupProduct;
        this.inventoryStatus = inventoryStatus;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
        this.imageUrl = imageUrl;
    }

    public SearchProductInventoryResponse(){}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGroupProduct() {
        return groupProduct;
    }
    public void setGroupProduct(String groupProduct) {
        this.groupProduct = groupProduct;
    }
    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }
    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
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
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
