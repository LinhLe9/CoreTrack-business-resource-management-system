package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.ProductStatus;
import jakarta.validation.constraints.NotNull;

public class ChangeProductStatusRequest {
    @NotNull(message = "New status is required")
    private ProductStatus newStatus;
    private String reason;

    public ChangeProductStatusRequest() {
    }

    public ChangeProductStatusRequest(ProductStatus newStatus, String reason) {
        this.newStatus = newStatus;
        this.reason = reason;
    }

    public ProductStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ProductStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 