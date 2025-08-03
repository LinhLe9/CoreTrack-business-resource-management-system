package org.example.coretrack.dto.purchasingTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreatePurchasingTicketResponse {
    private Long id;
    private String name;

    // productVariant
    private String materialVariantSku;
    private Long materialVariantId;
    private BigDecimal quantity;

    private String status;

    private LocalDateTime expected_ready_date;
    private LocalDateTime ready_date;

    // Audit Fields
    private LocalDateTime createdAt;
    private String createdBy;
    private String role;

    //constructor
    public CreatePurchasingTicketResponse(Long id, String name, String materialVariantSku, Long materialVariantId,
            BigDecimal quantity, String status, LocalDateTime expected_ready_date, LocalDateTime ready_date,
            LocalDateTime createdAt, String createdBy, String role) {
        this.id = id;
        this.name = name;
        this.materialVariantSku = materialVariantSku;
        this.materialVariantId = materialVariantId;
        this.quantity = quantity;
        this.status = status;
        this.expected_ready_date = expected_ready_date;
        this.ready_date = ready_date;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.role = role;
    }

    public CreatePurchasingTicketResponse(){}

    //getter setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterialVariantSku() {
        return materialVariantSku;
    }

    public void setMaterialVariantSku(String materialVariantSku) {
        this.materialVariantSku = materialVariantSku;
    }

    public Long getMaterialVariantId() {
        return materialVariantId;
    }

    public void setMaterialVariantId(Long materialVariantId) {
        this.materialVariantId = materialVariantId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    
}
