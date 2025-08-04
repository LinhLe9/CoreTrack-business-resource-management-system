package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.coretrack.model.product.inventory.InventoryStatus;

public class SearchInventoryResponse {
    private Long id;
    private String sku;
    private String name;
    private String group;
    private InventoryStatus inventoryStatus; 
    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;
    private String imageUrl;
    private LocalDateTime updatedAt;

    public SearchInventoryResponse(Long id, String sku, String name, String group, InventoryStatus inventoryStatus,
            BigDecimal currentStock, BigDecimal minAlertStock, BigDecimal maxStockLevel, String imageUrl, LocalDateTime updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.group = group;
        this.inventoryStatus = inventoryStatus;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
        this.imageUrl = imageUrl;
        this.updatedAt = updatedAt;
    }

    public SearchInventoryResponse(){}

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
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
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
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
