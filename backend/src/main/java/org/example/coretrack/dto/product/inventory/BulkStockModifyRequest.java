package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BulkStockModifyRequest {
    @NotEmpty(message = "Product variant IDs cannot be empty")
    private List<Long> variantIds;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal quantity;

    private String note;
    private String referenceDocumentType;
    private Long referenceDocumentId;
    private String transactionSource;

    public BulkStockModifyRequest() {}

    public BulkStockModifyRequest(List<Long> variantIds, BigDecimal quantity, String note,
            String referenceDocumentType, Long referenceDocumentId, String transactionSource) {
        this.variantIds = variantIds;
        this.quantity = quantity;
        this.note = note;
        this.referenceDocumentType = referenceDocumentType;
        this.referenceDocumentId = referenceDocumentId;
        this.transactionSource = transactionSource;
    }

    public List<Long> getVariantIds() {
        return variantIds;
    }

    public void setVariantIds(List<Long> variantIds) {
        this.variantIds = variantIds;
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
} 