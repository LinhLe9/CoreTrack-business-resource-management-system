export interface MaterialInventoryQueryParams {
  search?: string;
  groupMaterials?: string[];
  inventoryStatus?: string[];
  page?: number;
  size?: number;
  sort?: string;
}

export interface MaterialInventoryFilterParams {
  groupMaterials?: string[];
  inventoryStatus?: string[];
}

export interface MaterialInventoryAutoComplete {
  variantId: number;
  materialName: string;
  materialSku: string;
  variantSku: string;
  variantName: string;
}

// Material Inventory Request/Response Types
export interface AddMaterialInventoryRequest {
  materialVariantSku: string;
  currentStock: string; // BigDecimal as string
  minAlertStock?: string; // BigDecimal as string
  maxStockLevel?: string; // BigDecimal as string
}

export interface AddMaterialInventoryResponse {
  materialVariantSku: string;
  materialVariantName: string;
  currentStock: string;
  minAlertStock?: string;
  maxStockLevel?: string;
  materialStatus: string;
  inventoryStatus: string;
}

export interface MaterialInventoryDetailResponse {
  id: number;
  materialVariantSku: string;
  materialVariantName: string;
  materialName: string;
  materialSku: string;
  currentStock: string;
  minAlertStock: string;
  maxStockLevel: string;
  allocatedStock: string;
  futureStock: string;
  inventoryStatus: string;
  materialStatus: string;
  imageUrl?: string;
  transactions: any[]; // InventoryTransactionResponse[]
}

// Stock Operation Types
export interface StockSetRequest {
  quantity: string; // BigDecimal as string
  note?: string;
  referenceDocumentType?: string;
  referenceDocumentId?: number;
  transactionSource?: string;
}

export interface StockModifyRequest {
  quantity: string; // BigDecimal as string
  note?: string;
  referenceDocumentType?: string;
  referenceDocumentId?: number;
  transactionSource?: string;
}

// Bulk Operation Types
export interface BulkStockSetRequest {
  variantIds: number[];
  quantity: string; // BigDecimal as string
  note?: string;
  referenceDocumentType?: string;
  referenceDocumentId?: number;
}

export interface BulkStockModifyRequest {
  variantIds: number[];
  quantity: string; // BigDecimal as string
  note?: string;
  referenceDocumentType?: string;
  referenceDocumentId?: number;
  transactionSource?: string;
}

export interface BulkTransactionError {
  variantId: number;
  error: string;
  reason: string;
}

export interface BulkInventoryTransactionResponse {
  successfulTransactions: any[]; // InventoryTransactionResponse[]
  failedTransactions: BulkTransactionError[];
  totalProcessed: number;
  successCount: number;
  failureCount: number;
}

// Bulk Init Types
export interface BulkMInitInventoryRequest {
  materialVariantSkus: string[];
  currentStock: string; // BigDecimal as string
  minAlertStock?: string; // BigDecimal as string
  maxStockLevel?: string; // BigDecimal as string
}

export interface BulkMInitError {
  materialVariantSku: string;
  error: string;
  reason: string;
}

export interface BulkMInitInventoryResponse {
  successfulInits: AddMaterialInventoryResponse[];
  failedInits: BulkMInitError[];
  totalProcessed: number;
  successCount: number;
  failureCount: number;
} 