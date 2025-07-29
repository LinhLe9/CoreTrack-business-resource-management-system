package org.example.coretrack.dto.product;

import java.math.BigDecimal;

import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.example.coretrack.model.product.inventory.ProductInventory;

public class InventoryResponse {
    private Long id;
    private BigDecimal currentStock;
    private BigDecimal minAlertStock;
    private BigDecimal maxStockLevel;

    
    public InventoryResponse(Long id, BigDecimal currentStock, BigDecimal minAlertStock, BigDecimal maxStockLevel) {
        this.id = id;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
    }

    public InventoryResponse(ProductInventory productInventory){
        if (productInventory != null) {
            this.id = productInventory.getId();
            this.currentStock = productInventory.getCurrentStock();
            this.minAlertStock = productInventory.getMinAlertStock();
            this.maxStockLevel = productInventory.getMaxStockLevel();
        } else {
            this.id = null;
            this.currentStock = null;
            this.minAlertStock = null;
            this.maxStockLevel = null;
        }
    }

    public InventoryResponse(MaterialInventory materialInventory){
        this.id = materialInventory.getId();
        this.currentStock = materialInventory.getCurrentStock();
        this.minAlertStock = materialInventory.getMinAlertStock();
        this.maxStockLevel = materialInventory.getMaxStockLevel();
    }
    public InventoryResponse(){
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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
