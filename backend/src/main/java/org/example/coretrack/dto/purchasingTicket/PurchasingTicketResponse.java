package org.example.coretrack.dto.purchasingTicket;

import java.time.LocalDateTime;
import java.util.List;

public class PurchasingTicketResponse {
    private Long id;
    private String name;
    private LocalDateTime completed_date;
    private String status;

    // audit
    private LocalDateTime createdAt;
    private String createdBy;
    private String createdBy_role;

    private LocalDateTime lastUpdatedAt;
    private String lastUpdateBy;
    private String lastUpdateBy_role;

    List<PurchasingTicketDetailShortResponse> detail;
    List<PurchasingTicketStatusLogResponse> logs;

    // construc
    public PurchasingTicketResponse(Long id, String name, LocalDateTime completed_date, String status,
            LocalDateTime createdAt, String createdBy, String createdBy_role, LocalDateTime lastUpdatedAt,
            String lastUpdateBy, String lastUpdateBy_role, List<PurchasingTicketDetailShortResponse> detail,
            List<PurchasingTicketStatusLogResponse> logs) {
        this.id = id;
        this.name = name;
        this.completed_date = completed_date;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.createdBy_role = createdBy_role;
        this.lastUpdatedAt = lastUpdatedAt;
        this.lastUpdateBy = lastUpdateBy;
        this.lastUpdateBy_role = lastUpdateBy_role;
        this.detail = detail;
        this.logs = logs;
    }

    public PurchasingTicketResponse(){}

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

    public LocalDateTime getCompleted_date() {
        return completed_date;
    }

    public void setCompleted_date(LocalDateTime completed_date) {
        this.completed_date = completed_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCreatedBy_role() {
        return createdBy_role;
    }

    public void setCreatedBy_role(String createdBy_role) {
        this.createdBy_role = createdBy_role;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getLastUpdateBy_role() {
        return lastUpdateBy_role;
    }

    public void setLastUpdateBy_role(String lastUpdateBy_role) {
        this.lastUpdateBy_role = lastUpdateBy_role;
    }

    public List<PurchasingTicketDetailShortResponse> getDetail() {
        return detail;
    }

    public void setDetail(List<PurchasingTicketDetailShortResponse> detail) {
        this.detail = detail;
    }

    public List<PurchasingTicketStatusLogResponse> getLogs() {
        return logs;
    }

    public void setLogs(List<PurchasingTicketStatusLogResponse> logs) {
        this.logs = logs;
    }

    
}
