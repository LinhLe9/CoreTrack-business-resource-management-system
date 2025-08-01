package org.example.coretrack.dto.productionTicket;

import java.time.LocalDateTime;

import org.example.coretrack.model.productionTicket.ProductionTicket;

public class ProductionTicketCardResponse {
    private Long id;
    private String name;
    private LocalDateTime completed_date;
    private String status;

    // audit
    private LocalDateTime createdAt;

    public ProductionTicketCardResponse(Long id, String name, LocalDateTime completed_date, String status,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.completed_date = completed_date;
        this.status = status;
        this.createdAt = createdAt;
    }

    public ProductionTicketCardResponse (ProductionTicket pt){
        this.id = pt.getId();
        this.name = pt.getName();
        this.completed_date = pt.getCompleted_date();
        this.status = pt.getStatus().getDisplayName();
        this.createdAt = pt.getCreatedAt();
    }
    
    public ProductionTicketCardResponse(){}

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
