package org.example.coretrack.dto.productionTicket;

import java.time.LocalDateTime;

public class ProductionTicketDetailStatusLogResponse {
    private Long id;
    private Long productionDetailTicketId;
    private String new_status;
    private String old_status;
    private String note;
    private LocalDateTime updatedAt;
    private String updatedByName;
    private String updatedByRole;

    // constructor
    public ProductionTicketDetailStatusLogResponse(Long id, Long productionDetailTicketId,
            String new_status, String old_status, String note, LocalDateTime updatedAt, String updatedByName,
            String updatedByRole) {
        this.id = id;
        this.productionDetailTicketId = productionDetailTicketId;
        this.new_status = new_status;
        this.old_status = old_status;
        this.note = note;
        this.updatedAt = updatedAt;
        this.updatedByName = updatedByName;
        this.updatedByRole = updatedByRole;
    }

    public ProductionTicketDetailStatusLogResponse(){}

    // getter and setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductionDetailTicketId() {
        return productionDetailTicketId;
    }
    public void setProductionDetailTicketId(Long productionDetailTicketId) {
        this.productionDetailTicketId = productionDetailTicketId;
    }
    public String getNew_status() {
        return new_status;
    }
    public void setNew_status(String new_status) {
        this.new_status = new_status;
    }
    public String getOld_status() {
        return old_status;
    }
    public void setOld_status(String old_status) {
        this.old_status = old_status;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getUpdatedByName() {
        return updatedByName;
    }
    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }
    public String getUpdatedByRole() {
        return updatedByRole;
    }
    public void setUpdatedByRole(String updatedByRole) {
        this.updatedByRole = updatedByRole;
    }

    
}
