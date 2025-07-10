package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.inventory.productInventory;

public class InventoryResponse {
    private Long id;
    private Integer currentStock;
    private Integer minAlertStock;
    private Integer maxStockLevel;

    
    public InventoryResponse(Long id, Integer currentStock, Integer minAlertStock, Integer maxStockLevel) {
        this.id = id;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
    }

    public InventoryResponse(productInventory productInventory){
        this.id = productInventory.getId();
        this.currentStock = productInventory.getCurrentStock();
        this.minAlertStock = productInventory.getMinAlertStock();
        this.maxStockLevel = productInventory.getMaxStockLevel();
    }

    public InventoryResponse(){
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Integer getCurrentStock() {
        return currentStock;
    }


    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }


    public Integer getMinAlertStock() {
        return minAlertStock;
    }


    public void setMinAlertStock(Integer minAlertStock) {
        this.minAlertStock = minAlertStock;
    }


    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }


    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }
}
