package org.example.coretrack.dto.product.inventory;

import java.util.List;

public class BulkInventoryTransactionResponse {
    private List<InventoryTransactionResponse> successfulTransactions;
    private List<BulkTransactionError> failedTransactions;
    private int totalProcessed;
    private int successCount;
    private int failureCount;

    public BulkInventoryTransactionResponse() {}

    public BulkInventoryTransactionResponse(List<InventoryTransactionResponse> successfulTransactions,
            List<BulkTransactionError> failedTransactions, int totalProcessed, int successCount, int failureCount) {
        this.successfulTransactions = successfulTransactions;
        this.failedTransactions = failedTransactions;
        this.totalProcessed = totalProcessed;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public List<InventoryTransactionResponse> getSuccessfulTransactions() {
        return successfulTransactions;
    }

    public void setSuccessfulTransactions(List<InventoryTransactionResponse> successfulTransactions) {
        this.successfulTransactions = successfulTransactions;
    }

    public List<BulkTransactionError> getFailedTransactions() {
        return failedTransactions;
    }

    public void setFailedTransactions(List<BulkTransactionError> failedTransactions) {
        this.failedTransactions = failedTransactions;
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

    public static class BulkTransactionError {
        private Long variantId;
        private String error;
        private String reason;

        public BulkTransactionError() {}

        public BulkTransactionError(Long variantId, String error, String reason) {
            this.variantId = variantId;
            this.error = error;
            this.reason = reason;
        }

        public Long getVariantId() {
            return variantId;
        }

        public void setVariantId(Long variantId) {
            this.variantId = variantId;
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