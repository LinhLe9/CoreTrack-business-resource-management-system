package org.example.coretrack.service;

import org.example.coretrack.model.product.ProductStatus;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ProductStatusValidator {
    private static final Map<ProductStatus, Set<ProductStatus>> VALID_TRANSITIONS = new HashMap<>();

    static {
        VALID_TRANSITIONS.put(ProductStatus.ACTIVE, Set.of(ProductStatus.INACTIVE, ProductStatus.DISCONTINUED, ProductStatus.DELETED));
        VALID_TRANSITIONS.put(ProductStatus.INACTIVE, Set.of(ProductStatus.ACTIVE, ProductStatus.DISCONTINUED, ProductStatus.DELETED));
        VALID_TRANSITIONS.put(ProductStatus.DISCONTINUED, Set.of(ProductStatus.ACTIVE, ProductStatus.INACTIVE, ProductStatus.DELETED));
        VALID_TRANSITIONS.put(ProductStatus.DELETED, Set.of());
    }

    public boolean isValidTransition(ProductStatus currentStatus, ProductStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        if (currentStatus == newStatus) {
            return false;
        }

        Set<ProductStatus> validTransitions = VALID_TRANSITIONS.get(currentStatus);
        return validTransitions != null && validTransitions.contains(newStatus);
    }

    public String getInvalidTransitionMessage(ProductStatus currentStatus, ProductStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return "Invalid status transition: null status provided";
        }

        if (currentStatus == newStatus) {
            return String.format("Product is already in %s status", currentStatus);
        }

        if (currentStatus == ProductStatus.DELETED) {
            return "Cannot change status of deleted product";
        }

        Set<ProductStatus> validTransitions = VALID_TRANSITIONS.get(currentStatus);
        if (validTransitions == null || !validTransitions.contains(newStatus)) {
            return String.format("Invalid transition from %s to %s", currentStatus, newStatus);
        }

        return "Valid transition";
    }

    public Set<ProductStatus> getValidTransitions(ProductStatus currentStatus) {
        if (currentStatus == null) {
            return Set.of();
        }
        return VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
    }
} 