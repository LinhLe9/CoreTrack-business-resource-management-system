package org.example.coretrack.dto.sale;

import java.time.LocalDateTime;

public class SaleOrderStatusLogResponse {
    private Long id;
    private String new_status;
    private String old_status;
    private String note;
    private LocalDateTime updatedAt;
    private String updatedBy_Username;
    private String updatedBy_Role;
    
    public SaleOrderStatusLogResponse(Long id, String new_status, String old_status, String note,
            LocalDateTime updatedAt, String updatedBy_Username, String updatedBy_Role) {
        this.id = id;
        this.new_status = new_status;
        this.old_status = old_status;
        this.note = note;
        this.updatedAt = updatedAt;
        this.updatedBy_Username = updatedBy_Username;
        this.updatedBy_Role = updatedBy_Role;
    }

    public SaleOrderStatusLogResponse(){}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public String getUpdatedBy_Username() {
        return updatedBy_Username;
    }
    public void setUpdatedBy_Username(String updatedBy_Username) {
        this.updatedBy_Username = updatedBy_Username;
    }
    public String getUpdatedBy_Role() {
        return updatedBy_Role;
    }
    public void setUpdatedBy_Role(String updatedBy_Role) {
        this.updatedBy_Role = updatedBy_Role;
    }
}
