package org.example.coretrack.dto.purchasingTicket;

import java.time.LocalDateTime;

public class PurchasingTicketCardResponse {
    private Long id;
    private String name;
    private LocalDateTime completed_date;
    private String status;

    // audit
    private LocalDateTime createdAt;

    public PurchasingTicketCardResponse(Long id, String name, LocalDateTime completed_date, String status,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.completed_date = completed_date;
        this.status = status;
        this.createdAt = createdAt;
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

    
}
