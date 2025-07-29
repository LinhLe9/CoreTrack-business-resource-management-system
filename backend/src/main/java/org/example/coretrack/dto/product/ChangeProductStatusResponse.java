package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.ProductStatus;

public class ChangeProductStatusResponse {
    private Long productId;
    private ProductStatus previousStatus;
    private ProductStatus newStatus;
    private String message;
    private boolean success;

    public ChangeProductStatusResponse() {
    }

    public ChangeProductStatusResponse(Long productId, ProductStatus previousStatus, ProductStatus newStatus, String message, boolean success) {
        this.productId = productId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.message = message;
        this.success = success;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public ProductStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ProductStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ProductStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ProductStatus newStatus) {
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