package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.ProductStatus;
import java.util.Set;

public class ProductStatusTransitionResponse {
    private Long productId;
    private ProductStatus currentStatus;
    private Set<ProductStatus> availableTransitions;
    private String message;

    public ProductStatusTransitionResponse() {
    }

    public ProductStatusTransitionResponse(Long productId, ProductStatus currentStatus, Set<ProductStatus> availableTransitions, String message) {
        this.productId = productId;
        this.currentStatus = currentStatus;
        this.availableTransitions = availableTransitions;
        this.message = message;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public ProductStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ProductStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Set<ProductStatus> getAvailableTransitions() {
        return availableTransitions;
    }

    public void setAvailableTransitions(Set<ProductStatus> availableTransitions) {
        this.availableTransitions = availableTransitions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 