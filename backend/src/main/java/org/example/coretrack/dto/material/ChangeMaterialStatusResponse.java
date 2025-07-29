package org.example.coretrack.dto.material;

import org.example.coretrack.model.material.MaterialStatus;

public class ChangeMaterialStatusResponse {
    private Long materialId;
    private MaterialStatus previousStatus;
    private MaterialStatus newStatus;
    private String message;
    private boolean success;

    public ChangeMaterialStatusResponse() {
    }

    public ChangeMaterialStatusResponse(Long materialId, MaterialStatus previousStatus, 
                                     MaterialStatus newStatus, String message, boolean success) {
        this.materialId = materialId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.message = message;
        this.success = success;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public MaterialStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(MaterialStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public MaterialStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(MaterialStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
} 