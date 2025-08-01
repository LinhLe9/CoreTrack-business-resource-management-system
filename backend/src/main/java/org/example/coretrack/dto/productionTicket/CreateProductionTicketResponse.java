package org.example.coretrack.dto.productionTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateProductionTicketResponse {
    private Long id;
    private String name;

    // productVariant
    private String productVariantSku;
    private Long productVariantId;
    private BigDecimal quantity;

    private String status;

    private LocalDateTime expected_complete_date;
    private LocalDateTime completed_date;

    // Audit Fields
    private LocalDateTime createdAt;
    private String createdBy;
    private String role;

    public CreateProductionTicketResponse(Long id, String name, String productVariantSku, Long productVariantId, BigDecimal quantity,
            String status, LocalDateTime expected_complete_date,
            LocalDateTime completed_date, LocalDateTime createdAt, String createdBy, String role) {
        
        this.name = name;        
        this.id = id;
        this.productVariantSku = productVariantSku;
        this.productVariantId = productVariantId;
        this.quantity = quantity;
        this.status = status;
        this.expected_complete_date = expected_complete_date;
        this.completed_date = completed_date;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.role = role;
    }

    public CreateProductionTicketResponse(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductVariantSku() {
        return productVariantSku;
    }

    public void setProductVariantSku(String productVariantSku) {
        this.productVariantSku = productVariantSku;
    }

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
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

    public LocalDateTime getExpected_complete_date() {
        return expected_complete_date;
    }

    public void setExpected_complete_date(LocalDateTime expected_complete_date) {
        this.expected_complete_date = expected_complete_date;
    }

    public LocalDateTime getCompleted_date() {
        return completed_date;
    }

    public void setCompleted_date(LocalDateTime completed_date) {
        this.completed_date = completed_date;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
