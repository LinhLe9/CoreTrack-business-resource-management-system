package org.example.coretrack.service;

import org.example.coretrack.model.material.MaterialStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class MaterialStatusValidator {
    
    private static final Map<MaterialStatus, Set<MaterialStatus>> VALID_TRANSITIONS = new HashMap<>();
    
    static {
        // ACTIVE can transition to INACTIVE, DISCONTINUED, DELETED
        VALID_TRANSITIONS.put(MaterialStatus.ACTIVE, 
            Set.of(MaterialStatus.INACTIVE, MaterialStatus.DISCONTINUED, MaterialStatus.DELETED));
        
        // INACTIVE can transition to ACTIVE, DISCONTINUED, DELETED
        VALID_TRANSITIONS.put(MaterialStatus.INACTIVE, 
            Set.of(MaterialStatus.ACTIVE, MaterialStatus.DISCONTINUED, MaterialStatus.DELETED));
        
        // DISCONTINUED can transition to ACTIVE, INACTIVE, DELETED
        VALID_TRANSITIONS.put(MaterialStatus.DISCONTINUED, 
            Set.of(MaterialStatus.ACTIVE, MaterialStatus.INACTIVE, MaterialStatus.DELETED));
        
        // DELETED cannot transition to any other status (soft delete)
        VALID_TRANSITIONS.put(MaterialStatus.DELETED, Set.of());
    }
    
    /**
     * Validates if a status transition is allowed
     * @param currentStatus The current status of the material
     * @param newStatus The desired new status
     * @return true if the transition is valid, false otherwise
     */
    public boolean isValidTransition(MaterialStatus currentStatus, MaterialStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        
        Set<MaterialStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
        return allowedTransitions != null && allowedTransitions.contains(newStatus);
    }
    
    /**
     * Gets the error message for an invalid transition
     * @param currentStatus The current status
     * @param newStatus The desired new status
     * @return Error message explaining why the transition is invalid
     */
    public String getInvalidTransitionMessage(MaterialStatus currentStatus, MaterialStatus newStatus) {
        if (currentStatus == MaterialStatus.DELETED) {
            return "Cannot change status of deleted material.";
        }
        
        if (currentStatus == newStatus) {
            return "Material is already in " + newStatus + " status.";
        }
        
        return String.format("Invalid status change: '%s' material cannot be set to '%s' status.", 
                           currentStatus, newStatus);
    }
    
    /**
     * Gets all valid transitions for a given status
     * @param currentStatus The current status
     * @return Set of valid status transitions
     */
    public Set<MaterialStatus> getValidTransitions(MaterialStatus currentStatus) {
        return VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
    }
} 