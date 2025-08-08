package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.coretrack.model.material.inventory.MaterialInventoryLog;
import org.example.coretrack.model.product.inventory.ProductInventoryLog;

public class InventoryTransactionResponse {
    private Long id;
    private String transactionType;
    private BigDecimal quantity;
    private BigDecimal previousStock;
    private BigDecimal newStock;
    private String note;
    private String referenceDocumentType;
    private Long referenceDocumentId;
    private String transactionSource;
    private String stockType;
    private LocalDateTime createdAt;
    private String createdBy;
    private String user_role;

    public InventoryTransactionResponse() {}

    public InventoryTransactionResponse(Long id, String transactionType, String transactionSource,
                                    BigDecimal quantity, BigDecimal previousStock, BigDecimal newStock, 
                                    String note, String referenceDocumentType, Long referenceDocumentId,
                                    String stockType, LocalDateTime createdAt, String createdBy, String user_role) {
        this.id = id;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.note = note;
        this.referenceDocumentType = referenceDocumentType;
        this.referenceDocumentId = referenceDocumentId;
        this.transactionSource = transactionSource;
        this.stockType = stockType;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.user_role = user_role;
    }

    public InventoryTransactionResponse(ProductInventoryLog log){
        this.id = log.getId();
        this.transactionType = log.getTransactionType() != null ? log.getTransactionType().getDisplayName() : null;
        this.transactionSource = log.getTransactionSourceType() != null ? log.getTransactionSourceType().getDisplayName() : null;
        this.quantity = log.getQuantity();
        this.previousStock = log.getBeforeQuantity();
        this.newStock = log.getAfterQuantity();
        this.note = log.getNote();
        this.referenceDocumentType = log.getReferenceDocumentType() != null ? log.getReferenceDocumentType().getDisplayName() : null;
        this.referenceDocumentId = log.getReferenceDocumentId();
        this.stockType = log.getStockType() != null ? log.getStockType().getDisplayName() : null;
        this.createdAt = log.getCreatedAt();
        this.createdBy = log.getCreatedBy() != null ? log.getCreatedBy().getUsername() : null;
        this.user_role = log.getCreatedBy() != null && log.getCreatedBy().getRole() != null ? log.getCreatedBy().getRole().toString() : null;
    }

    public InventoryTransactionResponse(MaterialInventoryLog log){
        System.out.println("DEBUG: Creating InventoryTransactionResponse from MaterialInventoryLog ID: " + log.getId());
        try {
            this.id = log.getId();
            this.transactionType = log.getTransactionType() != null ? log.getTransactionType().getDisplayName() : null;
            this.transactionSource = log.getTransactionSourceType() != null ? log.getTransactionSourceType().getDisplayName() : null;
            this.quantity = log.getQuantity();
            this.note = log.getNote();
            this.previousStock = log.getBeforeQuantity();
            this.newStock = log.getAfterQuantity();
            this.referenceDocumentType = log.getReferenceDocumentType() != null ? log.getReferenceDocumentType().getDisplayName() : null;
            this.referenceDocumentId = log.getReferenceDocumentId();
            this.stockType = log.getStockType() != null ? log.getStockType().getDisplayName() : null;
            this.createdAt = log.getCreatedAt();
            this.createdBy = log.getCreatedBy() != null ? log.getCreatedBy().getUsername() : null;
            this.user_role = log.getCreatedBy() != null && log.getCreatedBy().getRole() != null ? log.getCreatedBy().getRole().toString() : null;
            System.out.println("DEBUG: Successfully created InventoryTransactionResponse for log ID: " + log.getId());
        } catch (Exception e) {
            System.err.println("DEBUG: Error creating InventoryTransactionResponse for log ID " + log.getId() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(BigDecimal previousStock) {
        this.previousStock = previousStock;
    }

    public BigDecimal getNewStock() {
        return newStock;
    }

    public void setNewStock(BigDecimal newStock) {
        this.newStock = newStock;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReferenceDocumentType() {
        return referenceDocumentType;
    }

    public void setReferenceDocumentType(String referenceDocumentType) {
        this.referenceDocumentType = referenceDocumentType;
    }

    public Long getReferenceDocumentId() {
        return referenceDocumentId;
    }

    public void setReferenceDocumentId(Long referenceDocumentId) {
        this.referenceDocumentId = referenceDocumentId;
    }

    public String getTransactionSource() {
        return transactionSource;
    }

    public void setTransactionSource(String transactionSource) {
        this.transactionSource = transactionSource;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }
}
