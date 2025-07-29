package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import org.example.coretrack.model.product.inventory.InventoryStatus;

public class AllSearchProductInventoryResponse {
    private Long id;
    private String sku;
    private String name;
    private InventoryStatus inventoryStatus; 
    private BigDecimal currentStock;
    private String imageUrl;
    
    public AllSearchProductInventoryResponse() {}
    
    public AllSearchProductInventoryResponse(Long id, String sku, String name, InventoryStatus inventoryStatus,
            BigDecimal currentStock, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.inventoryStatus = inventoryStatus;
        this.currentStock = currentStock;
        this.imageUrl = imageUrl;
    }
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
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    
}
