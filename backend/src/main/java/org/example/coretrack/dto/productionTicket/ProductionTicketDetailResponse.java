package org.example.coretrack.dto.productionTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductionTicketDetailResponse {
    private Long id;
    private String productVariantSku;
    private BigDecimal quantity;

    List<BomItemProductionTicketResponse> boms;
    private String status;

    private LocalDateTime expected_complete_date;
    private LocalDateTime completed_date;

    private List<ProductionTicketDetailStatusLogResponse> logs;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    private String createdBy_name;
    private String createdBy_role;

    private String lastUpdatedAt_name;
    private String lastUpdatedAt_role;

    // constructor
    public ProductionTicketDetailResponse(Long id, String productVariantSku, BigDecimal quantity,
            List<BomItemProductionTicketResponse> boms, String status, LocalDateTime expected_complete_date,
            LocalDateTime completed_date, List<ProductionTicketDetailStatusLogResponse> logs,
            LocalDateTime createdAt, LocalDateTime lastUpdatedAt, String createdBy_name,
            String createdBy_role, String lastUpdatedAt_name, String lastUpdatedAt_role) {
        this.id = id;
        this.productVariantSku = productVariantSku;
        this.quantity = quantity;
        this.boms = boms;
        this.status = status;
        this.expected_complete_date = expected_complete_date;
        this.completed_date = completed_date;
        this.logs = logs;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.createdBy_name = createdBy_name;
        this.createdBy_role = createdBy_role;
        this.lastUpdatedAt_name = lastUpdatedAt_name;
        this.lastUpdatedAt_role = lastUpdatedAt_role;
    }

    public ProductionTicketDetailResponse(){}
    
    //getter and setter
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public List<BomItemProductionTicketResponse> getBoms() {
        return boms;
    }

    public void setBoms(List<BomItemProductionTicketResponse> boms) {
        this.boms = boms;
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

    public List<ProductionTicketDetailStatusLogResponse> getLogs() {
        return logs;
    }

    public void setLogs(List<ProductionTicketDetailStatusLogResponse> logs) {
        this.logs = logs;
    }
}
