package org.example.coretrack.model.material.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.product.inventory.InventoryTransactionType;

import jakarta.persistence.*;

@Entity
@Table(name = "materialInventoryLog")
public class MaterialInventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime transactionTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_inventory_id", nullable = false) 
    private MaterialInventory materialInventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private InventoryTransactionType transactionType; 

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_source_type", nullable = false)
    private materialInventoryTransactionSourceType transactionSourceType;
    
    @Column(precision = 10, scale = 4, nullable = false) 
    private BigDecimal quantity;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_document_type") 
    private materialInventoryReferenceDocumentType referenceDocumentType;

    @Column(name = "reference_document_id")
    private Long referenceDocumentId;

    // --- AUDIT FIELDS ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    public MaterialInventoryLog() {
    }

    public MaterialInventoryLog(LocalDateTime transactionTimestamp, MaterialInventory materialInventory,
                                InventoryTransactionType transactionType, materialInventoryTransactionSourceType transactionSourceType,
                                BigDecimal quantity, String note,
                                materialInventoryReferenceDocumentType referenceDocumentType, Long referenceDocumentId, User createdBy) {
        this.transactionTimestamp = transactionTimestamp;
        this.materialInventory = materialInventory;
        this.transactionType = transactionType;
        this.transactionSourceType = transactionSourceType;
        this.quantity = quantity;
        this.note = note;
        this.referenceDocumentType = referenceDocumentType;
        this.referenceDocumentId = referenceDocumentId;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(LocalDateTime transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    public MaterialInventory getMaterialInventory() {
        return materialInventory;
    }

    public void setMaterialInventory(MaterialInventory materialInventory) {
        this.materialInventory = materialInventory;
    }

    public InventoryTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(InventoryTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public materialInventoryTransactionSourceType getTransactionSourceType() {
        return transactionSourceType;
    }

    public void setTransactionSourceType(materialInventoryTransactionSourceType transactionSourceType) {
        this.transactionSourceType = transactionSourceType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public materialInventoryReferenceDocumentType getReferenceDocumentType() {
        return referenceDocumentType;
    }

    public void setReferenceDocumentType(materialInventoryReferenceDocumentType referenceDocumentType) {
        this.referenceDocumentType = referenceDocumentType;
    }

    public Long getReferenceDocumentId() {
        return referenceDocumentId;
    }

    public void setReferenceDocumentId(Long referenceDocumentId) {
        this.referenceDocumentId = referenceDocumentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}
