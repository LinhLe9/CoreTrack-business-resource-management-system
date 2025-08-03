package org.example.coretrack.dto.purchasingTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchasingTicketDetailShortResponse {
    private Long id;
    private String materialVariantSku;
    private BigDecimal quantity;

    private String status;
    
    private LocalDateTime expected_ready_date;
    private LocalDateTime ready_date;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    private String createdBy_name;
    private String createdBy_role;

    private String lastUpdatedAt_name;
    private String lastUpdatedAt_role;

    // constructor
    public PurchasingTicketDetailShortResponse(Long id, String materialVariantSku, BigDecimal quantity, String status,
            LocalDateTime expected_ready_date, LocalDateTime ready_date, LocalDateTime createdAt,
            LocalDateTime lastUpdatedAt, String createdBy_name, String createdBy_role, String lastUpdatedAt_name,
            String lastUpdatedAt_role) {
        this.id = id;
        this.materialVariantSku = materialVariantSku;
        this.quantity = quantity;
        this.status = status;
        this.expected_ready_date = expected_ready_date;
        this.ready_date = ready_date;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.createdBy_name = createdBy_name;
        this.createdBy_role = createdBy_role;
        this.lastUpdatedAt_name = lastUpdatedAt_name;
        this.lastUpdatedAt_role = lastUpdatedAt_role;
    }

    public PurchasingTicketDetailShortResponse(){}


    //setter & getter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialVariantSku() {
        return materialVariantSku;
    }

    public void setMaterialVariantSku(String materialVariantSku) {
        this.materialVariantSku = materialVariantSku;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpected_ready_date() {
        return expected_ready_date;
    }

    public void setExpected_ready_date(LocalDateTime expected_ready_date) {
        this.expected_ready_date = expected_ready_date;
    }

    public LocalDateTime getReady_date() {
        return ready_date;
    }

    public void setReady_date(LocalDateTime ready_date) {
        this.ready_date = ready_date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getCreatedBy_name() {
        return createdBy_name;
    }

    public void setCreatedBy_name(String createdBy_name) {
        this.createdBy_name = createdBy_name;
    }

    public String getCreatedBy_role() {
        return createdBy_role;
    }

    public void setCreatedBy_role(String createdBy_role) {
        this.createdBy_role = createdBy_role;
    }

    public String getLastUpdatedAt_name() {
        return lastUpdatedAt_name;
    }

    public void setLastUpdatedAt_name(String lastUpdatedAt_name) {
        this.lastUpdatedAt_name = lastUpdatedAt_name;
    }

    public String getLastUpdatedAt_role() {
        return lastUpdatedAt_role;
    }

    public void setLastUpdatedAt_role(String lastUpdatedAt_role) {
        this.lastUpdatedAt_role = lastUpdatedAt_role;
    }
    
    
}
