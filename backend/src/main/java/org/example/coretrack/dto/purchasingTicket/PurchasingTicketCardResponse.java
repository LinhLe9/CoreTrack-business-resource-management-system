package org.example.coretrack.dto.purchasingTicket;

import java.time.LocalDateTime;

import org.example.coretrack.model.purchasingTicket.PurchasingTicket;

public class PurchasingTicketCardResponse {
    private Long id;
    private String name;
    private LocalDateTime completed_date;
    private String status;

    // audit
    private LocalDateTime createdAt;
    private String createdBy;
    private String createdByRole;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private String updatedByRole;

    public PurchasingTicketCardResponse(Long id, String name, LocalDateTime completed_date, String status,
            LocalDateTime createdAt, String createdBy, String createdByRole, LocalDateTime updatedAt, String updatedBy, String updatedByRole) {
        this.id = id;
        this.name = name;
        this.completed_date = completed_date;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.createdByRole = createdByRole;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.updatedByRole = updatedByRole;
    }

    public PurchasingTicketCardResponse(PurchasingTicket pt) {
        this.id = pt.getId();
        this.name = pt.getName();
        this.completed_date = pt.getCompleted_date();
        this.status = pt.getStatus().getDisplayName();
        this.createdAt = pt.getCreatedAt();
        this.createdBy = pt.getCreatedBy().getUsername();
        this.createdByRole = pt.getCreatedBy().getRole().name();
        this.updatedAt = pt.getUpdatedAt();
        this.updatedBy = pt.getUpdatedBy().getUsername();
        this.updatedByRole = pt.getUpdatedBy().getRole().name();
    }

    public PurchasingTicketCardResponse(){}

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

    public String getCreatedByRole() {
        return createdByRole;
    }

    public void setCreatedByRole(String createdByRole) {
        this.createdByRole = createdByRole;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedByRole() {
        return updatedByRole;
    }

    public void setUpdatedByRole(String updatedByRole) {
        this.updatedByRole = updatedByRole;
    }
}
