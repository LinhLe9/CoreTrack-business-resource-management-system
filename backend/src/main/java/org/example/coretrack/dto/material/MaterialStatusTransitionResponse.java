package org.example.coretrack.dto.material;

import org.example.coretrack.model.material.MaterialStatus;

import java.util.Set;

public class MaterialStatusTransitionResponse {
    private Long materialId;
    private MaterialStatus currentStatus;
    private Set<MaterialStatus> availableTransitions;
    private String message;

    public MaterialStatusTransitionResponse() {
    }

    public MaterialStatusTransitionResponse(Long materialId, MaterialStatus currentStatus, 
                                         Set<MaterialStatus> availableTransitions, String message) {
        this.materialId = materialId;
        this.currentStatus = currentStatus;
        this.availableTransitions = availableTransitions;
        this.message = message;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public MaterialStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(MaterialStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Set<MaterialStatus> getAvailableTransitions() {
        return availableTransitions;
    }

    public void setAvailableTransitions(Set<MaterialStatus> availableTransitions) {
        this.availableTransitions = availableTransitions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 