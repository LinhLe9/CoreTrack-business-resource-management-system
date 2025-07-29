package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class StockModifyRequest {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal newQuantity; 

    private String note; 

    private String referenceDocumentType;
    private Long referenceDocumentId;
    private String transactionSource;

    public StockModifyRequest(@NotNull @DecimalMin("0.0") BigDecimal newQuantity, String note,
            String referenceDocumentType, Long referenceDocumentId, String transactionSource) {
        this.newQuantity = newQuantity;
        this.note = note;
        this.referenceDocumentType = referenceDocumentType;
        this.referenceDocumentId = referenceDocumentId;
        this.transactionSource = transactionSource;
    }

    public StockModifyRequest(){}

    public BigDecimal getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(BigDecimal newQuantity) {
        this.newQuantity = newQuantity;
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
