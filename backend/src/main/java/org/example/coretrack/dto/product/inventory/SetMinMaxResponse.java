package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SetMinMaxResponse {
    private Long inventoryId;
    private String sku;
    private String productName;
    private BigDecimal oldValue;
    private BigDecimal newValue;
    private String message;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public SetMinMaxResponse() {}

    public SetMinMaxResponse(Long inventoryId, String sku, String productName, 
                           BigDecimal oldValue, BigDecimal newValue, 
                           String message, String updatedBy, LocalDateTime updatedAt) {
        this.inventoryId = inventoryId;
        this.sku = sku;
        this.productName = productName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.message = message;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getOldValue() {
        return oldValue;
    }

    public void setOldValue(BigDecimal oldValue) {
        this.oldValue = oldValue;
    }

    public BigDecimal getNewValue() {
        return newValue;
    }

    public void setNewValue(BigDecimal newValue) {
        this.newValue = newValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 