export interface SearchInventoryResponse {
  id: number;
  sku: string;
  name: string;
  group: string;
  inventoryStatus: string; // Will be enum value from backend
  currentStock: string; // BigDecimal as string
  minAlertStock: string; // BigDecimal as string
  maxStockLevel: string; // BigDecimal as string
  imageUrl?: string;
  updatedAt?: string; // ISO date string
}

export interface ProductInventoryAutoComplete {
  variantId: number;
  productName: string;
  productSku: string;
  variantSku: string;
  variantName: string;
}

export interface AllSearchInventoryResponse {
  id: number;
  sku: string;
  name: string;
  inventoryStatus: string;
  currentStock: string;
  imageUrl?: string;
}

export interface ProductInventoryQueryParams {
  search?: string;
  groupProducts?: string[];
  inventoryStatus?: string[];
  page?: number;
  size?: number;
  sort?: string;
}

export interface ProductInventoryFilterParams {
  groupProducts?: string[];
  inventoryStatus?: string[];
}

// Bulk operations interfaces
export interface BulkStockModifyRequest {
  variantIds: number[];
  quantity: string; // BigDecimal as string
  note?: string;
  referenceDocumentType?: string;
  referenceDocumentId?: number;
  transactionSource?: string;
}

export interface BulkStockSetRequest {
  variantIds: number[];
  quantity: string; // BigDecimal as string
  note?: string;
  referenceDocumentType?: string;
  referenceDocumentId?: number;
}

export interface BulkTransactionError {
  variantId: number;
  error: string;
  reason: string;
}

import { InventoryTransactionResponse } from './inventory';

export interface BulkInventoryTransactionResponse {
  successfulTransactions: InventoryTransactionResponse[];
  failedTransactions: BulkTransactionError[];
  totalProcessed: number;
  successCount: number;
  failureCount: number;
}

export interface BulkInitInventoryRequest {
  productVariantSkus: string[];
  currentStock: string; // BigDecimal as string
  minAlertStock?: string; // BigDecimal as string
  maxStockLevel?: string; // BigDecimal as string
}

export interface BulkInitError {
  productVariantSku: string;
  error: string;
  reason: string;
}

export interface BulkInitInventoryResponse {
  successfulInits: AddProductInventoryResponse[];
  failedInits: BulkInitError[];
  totalProcessed: number;
  successCount: number;
  failureCount: number;
}

export interface EnumValue {
  value: string;
  displayName: string;
  description: string;
}

export interface InventoryEnumsResponse {
  inventoryStatuses: EnumValue[];
  inventoryTransactionTypes: EnumValue[];
  inventoryReferenceDocumentTypes: EnumValue[];
  inventoryTransactionSourceTypes: EnumValue[];
}

export interface TransactionEnumsResponse {
  inventoryTransactionTypes: EnumValue[];
  inventoryReferenceDocumentTypes: EnumValue[];
  inventoryTransactionSourceTypes: EnumValue[];
}

export interface AddProductInventoryRequest {
  productVariantSku: string;
  currentStock: string; // BigDecimal as string
  minAlertStock?: string; // BigDecimal as string
  maxStockLevel?: string; // BigDecimal as string
}

export interface AddProductInventoryResponse {
  productVariantSku: string;
  productVariantName: string;
  currentStock: string;
  minAlertStock?: string;
  maxStockLevel?: string;
  productStatus: string;
  inventoryStatus: string;
}

export type { InventoryTransactionResponse } from './inventory'; 