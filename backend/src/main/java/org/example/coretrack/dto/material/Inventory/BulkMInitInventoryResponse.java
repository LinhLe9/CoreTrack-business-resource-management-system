package org.example.coretrack.dto.material.Inventory;

import java.util.List;

public class BulkMInitInventoryResponse {
    private List<AddMaterialInventoryResponse> successfulInits;
    private List<BulkMInitError> failedInits;
    private int totalProcessed;
    private int successCount;
    private int failureCount;

    public BulkMInitInventoryResponse(){}

    public BulkMInitInventoryResponse(List<AddMaterialInventoryResponse> successfulInits,
            List<BulkMInitError> failedInits, int totalProcessed, int successCount, int failureCount) {
        this.successfulInits = successfulInits;
        this.failedInits = failedInits;
        this.totalProcessed = totalProcessed;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public List<AddMaterialInventoryResponse> getSuccessfulInits() {
        return successfulInits;
    }

    public void setSuccessfulInits(List<AddMaterialInventoryResponse> successfulInits) {
        this.successfulInits = successfulInits;
    }

    public List<BulkMInitError> getFailedInits() {
        return failedInits;
    }

    public void setFailedInits(List<BulkMInitError> failedInits) {
        this.failedInits = failedInits;
    }

    public int getTotalProcessed() {
        return totalProcessed;
    }

    public void setTotalProcessed(int totalProcessed) {
        this.totalProcessed = totalProcessed;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }


    public static class BulkMInitError {
        private String productVariantSku;
        private String error;
        private String reason;

        public BulkMInitError() {}

        public BulkMInitError(String productVariantSku, String error, String reason) {
            this.productVariantSku = productVariantSku;
            this.error = error;
            this.reason = reason;
        }

        public String getProductVariantSku() {
            return productVariantSku;
        }

        public void setProductVariantSku(String productVariantSku) {
            this.productVariantSku = productVariantSku;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

}
