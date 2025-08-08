package org.example.coretrack.dto.material.Inventory;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.coretrack.dto.product.inventory.InventoryTransactionResponse;

public class MaterialInventoryDetailResponse {
    private Long variantId;
    private String materialName;
    private String materialSku;
    private String variantSku;
    private String variantName;
    
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal currentStock;
    
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal minAlertStock;
    
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal maxStockLevel;
    
    private String inventoryStatus;
    private String materialGroup;
    private String imageUrl;
    private List<InventoryTransactionResponse> logs;

    public MaterialInventoryDetailResponse() {}

    public MaterialInventoryDetailResponse(Long variantId, String materialName, String materialSku, 
                                       String variantSku, String variantName, BigDecimal currentStock,
                                       BigDecimal minAlertStock, BigDecimal maxStockLevel, 
                                       String inventoryStatus, String materialGroup, String imageUrl,
                                       List<InventoryTransactionResponse> logs) {
        this.variantId = variantId;
        this.materialName = materialName;
        this.materialSku = materialSku;
        this.variantSku = variantSku;
        this.variantName = variantName;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.maxStockLevel = maxStockLevel;
        this.inventoryStatus = inventoryStatus;
        this.materialGroup = materialGroup;
        this.imageUrl = imageUrl;
        this.logs = logs;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialSku() {
        return materialSku;
    }

    public void setMaterialSku(String materialSku) {
        this.materialSku = materialSku;
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

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
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
