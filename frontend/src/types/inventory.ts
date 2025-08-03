export interface InventoryTransactionResponse {
  id: number;
  transactionType: string;
  quantity: string; // BigDecimal as string
  previousStock: string; // BigDecimal as string
  newStock: string; // BigDecimal as string
  note?: string;
  referenceDocumentType?: string;
  referenceDocumentId?: number;
  transactionSource: string;
  stockType?: string;
  createdAt: string; // LocalDateTime as string
  createdBy: string;
  user_role: string;
}

export interface BulkTransactionError {
  variantId: number;
  error: string;
  reason: string;
}

export interface BulkInventoryTransactionResponse {
  successfulTransactions: InventoryTransactionResponse[];
  failedTransactions: BulkTransactionError[];
  totalProcessed: number;
  successCount: number;
  failureCount: number;
} 