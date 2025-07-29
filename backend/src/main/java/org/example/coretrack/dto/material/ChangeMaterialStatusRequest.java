package org.example.coretrack.dto.material;

import org.example.coretrack.model.material.MaterialStatus;

import jakarta.validation.constraints.NotNull;

public class ChangeMaterialStatusRequest {
    @NotNull(message = "New status is required")
    private MaterialStatus newStatus;
    
    private String reason;

    public ChangeMaterialStatusRequest() {
    }

    public ChangeMaterialStatusRequest(MaterialStatus newStatus, String reason) {
        this.newStatus = newStatus;
        this.reason = reason;
    }

    public MaterialStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(MaterialStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 