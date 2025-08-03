package org.example.coretrack.model.product.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.coretrack.model.auth.User;

import jakarta.persistence.*;

@Entity
@Table(name = "ProductInventoryLog")
public class ProductInventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime transactionTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_inventory_id", nullable = false) 
    private ProductInventory productInventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_type", nullable = false)
    private StockType stockType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private InventoryTransactionType transactionType; 

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_source_type", nullable = false)
    private ProductInventoryTransactionSourceType transactionSourceType;

    @Column(precision = 10, scale = 4, nullable = false) 
    private BigDecimal quantity;

    @Column(precision = 10, scale = 4, nullable = false)
    private BigDecimal beforeQuantity;

    @Column(precision = 10, scale = 4, nullable = false)
    private BigDecimal afterQuantity;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_document_type") 
    private ProductInventoryReferenceDocumentType referenceDocumentType;

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

    public ProductInventoryLog() {}

    public ProductInventoryLog(LocalDateTime transactionTimestamp, ProductInventory productInventory, StockType stockType,
                                InventoryTransactionType transactionType, ProductInventoryTransactionSourceType transactionSourceType,
                                BigDecimal quantity, BigDecimal beforeQuantity, BigDecimal afterQuantity, String note,
                                ProductInventoryReferenceDocumentType referenceDocumentType, Long referenceDocumentId, User createdBy) {
        this.transactionTimestamp = transactionTimestamp;
        this.productInventory = productInventory;
        this.stockType = stockType;
        this.transactionType = transactionType;
        this.transactionSourceType = transactionSourceType;
        this.quantity = quantity;
        this.beforeQuantity = beforeQuantity;
        this.afterQuantity = afterQuantity;
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

    public ProductInventory getProductInventory() {
        return productInventory;
    }

    public void setProductInventory(ProductInventory productInventory) {
        this.productInventory = productInventory;
    }

    public InventoryTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(InventoryTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public ProductInventoryTransactionSourceType getTransactionSourceType() {
        return transactionSourceType;
    }

    public void setTransactionSourceType(ProductInventoryTransactionSourceType transactionSourceType) {
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

    public ProductInventoryReferenceDocumentType getReferenceDocumentType() {
        return referenceDocumentType;
    }

    public void setReferenceDocumentType(ProductInventoryReferenceDocumentType referenceDocumentType) {
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

    public BigDecimal getBeforeQuantity() {
        return beforeQuantity;
    }

    public void setBeforeQuantity(BigDecimal beforeQuantity) {
        this.beforeQuantity = beforeQuantity;
    }

    public BigDecimal getAfterQuantity() {
        return afterQuantity;
    }

    public void setAfterQuantity(BigDecimal afterQuantity) {
        this.afterQuantity = afterQuantity;
    }

    public StockType getStockType() {
        return stockType;
    }

    public void setStockType(StockType stockType) {
        this.stockType = stockType;
    }
}
