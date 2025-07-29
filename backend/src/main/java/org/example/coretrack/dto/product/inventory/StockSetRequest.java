package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class StockSetRequest {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal newQuantity; 

    private String note; 

    private String referenceDocumentType;
    private Long referenceDocumentId;
    
    public StockSetRequest(@NotNull @DecimalMin("0.0") BigDecimal newQuantity, String note,
            String referenceDocumentType, Long referenceDocumentId) {
        this.newQuantity = newQuantity;
        this.note = note;
        this.referenceDocumentType = referenceDocumentType;
        this.referenceDocumentId = referenceDocumentId;
    }
    
    public StockSetRequest() {
    }

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

    
}
