import apiClient from '../lib/axios';
import { toBigDecimalString } from '../lib/utils';
import {
  AddProductInventoryRequest,
  AddProductInventoryResponse,
  InventoryTransactionResponse,
  BulkStockModifyRequest,
  BulkStockSetRequest,
  BulkTransactionError,
  BulkInventoryTransactionResponse,
  BulkInitInventoryRequest,
  BulkInitError,
  BulkInitInventoryResponse,
  EnumValue,
  InventoryEnumsResponse,
  TransactionEnumsResponse,
  AllSearchInventoryResponse
} from '../types/productInventory';

export const createProductInventory = async (
  request: AddProductInventoryRequest
): Promise<AddProductInventoryResponse> => {
  const response = await apiClient.post('/product-inventory/add-inventory', request);
  return response.data;
};

// Helper function to convert number to BigDecimal string for backend
export const createInventoryRequest = (
  productVariantSku: string,
  currentStock: number,
  minAlertStock?: number,
  maxStockLevel?: number
): AddProductInventoryRequest => {
  return {
    productVariantSku,
    currentStock: toBigDecimalString(currentStock),
    minAlertStock: minAlertStock ? toBigDecimalString(minAlertStock) : undefined,
    maxStockLevel: maxStockLevel ? toBigDecimalString(maxStockLevel) : undefined,
  };
};

// Add stock to a specific variant
export const addStock = async (
  variantId: number,
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): Promise<InventoryTransactionResponse> => {
  const request = {
    newQuantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
  const response = await apiClient.put(`/product-inventory/${variantId}/add`, request);
  return response.data;
};

// Subtract stock from a specific variant
export const subtractStock = async (
  variantId: number,
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): Promise<InventoryTransactionResponse> => {
  const request = {
    newQuantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
  const response = await apiClient.put(`/product-inventory/${variantId}/subtract`, request);
  return response.data;
};

// Bulk operations functions
export const bulkSetStock = async (request: BulkStockSetRequest): Promise<BulkInventoryTransactionResponse> => {
  const response = await apiClient.put('/product-inventory/bulk/set', request);
  return response.data;
};

export const bulkAddStock = async (request: BulkStockModifyRequest): Promise<BulkInventoryTransactionResponse> => {
  const response = await apiClient.put('/product-inventory/bulk/add', request);
  return response.data;
};

export const bulkSubtractStock = async (request: BulkStockModifyRequest): Promise<BulkInventoryTransactionResponse> => {
  const response = await apiClient.put('/product-inventory/bulk/subtract', request);
  return response.data;
};

export const bulkInitInventory = async (request: BulkInitInventoryRequest): Promise<BulkInitInventoryResponse> => {
  const response = await apiClient.post('/product-inventory/bulk/init', request);
  return response.data;
};

export const getInventoryEnums = async (): Promise<InventoryEnumsResponse> => {
  const response = await apiClient.get('/product-inventory/enums');
  return response.data;
};

export const getInventoryStatuses = async (): Promise<EnumValue[]> => {
  const response = await apiClient.get('/product-inventory/enums/inventory-statuses');
  return response.data;
};

export const getTransactionEnums = async (): Promise<TransactionEnumsResponse> => {
  const response = await apiClient.get('/product-inventory/enums/transaction-enums');
  return response.data;
};

// Helper functions for bulk operations
export const createBulkStockSetRequest = (
  variantIds: number[],
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number
): BulkStockSetRequest => {
  return {
    variantIds,
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
  };
};

export const createBulkStockModifyRequest = (
  variantIds: number[],
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): BulkStockModifyRequest => {
  return {
    variantIds,
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
};

export const createBulkInitInventoryRequest = (
  productVariantSkus: string[],
  currentStock: number,
  minAlertStock?: number,
  maxStockLevel?: number
): BulkInitInventoryRequest => {
  return {
    productVariantSkus,
    currentStock: toBigDecimalString(currentStock),
    minAlertStock: minAlertStock ? toBigDecimalString(minAlertStock) : undefined,
    maxStockLevel: maxStockLevel ? toBigDecimalString(maxStockLevel) : undefined,
  };
};

// Product Inventory Search and Filter
export const getProductInventoryFilter = async (params: any): Promise<any> => {
  const queryParams = new URLSearchParams();
  
  if (params.search) queryParams.append('search', params.search);
  if (params.groupProducts) {
    params.groupProducts.forEach((group: string) => {
      queryParams.append('groupProducts', group);
    });
  }
  if (params.inventoryStatus) {
    params.inventoryStatus.forEach((status: string) => {
      queryParams.append('inventoryStatus', status);
    });
  }
  if (params.page !== undefined) queryParams.append('page', params.page.toString());
  if (params.size !== undefined) queryParams.append('size', params.size.toString());
  if (params.sort) queryParams.append('sort', params.sort);

  const response = await apiClient.get(`/product-inventory/filter?${queryParams.toString()}`);
  return response.data;
};

// Product Inventory Alarm Filter
export const getProductAlarmFilter = async (params: any): Promise<any> => {
  const queryParams = new URLSearchParams();
  
  if (params.search) queryParams.append('search', params.search);
  if (params.groupProducts) {
    params.groupProducts.forEach((group: string) => {
      queryParams.append('groupProducts', group);
    });
  }
  if (params.status) {
    params.status.forEach((status: string) => {
      queryParams.append('status', status);
    });
  }
  if (params.sortByOldest !== undefined) queryParams.append('sortByOldest', params.sortByOldest.toString());
  if (params.page !== undefined) queryParams.append('page', params.page.toString());
  if (params.size !== undefined) queryParams.append('size', params.size.toString());

  const response = await apiClient.get(`/product-inventory/alarm/filter?${queryParams.toString()}`);
  return response.data;
};

export const getAllProductInventoryForAutocomplete = async (search?: string): Promise<AllSearchInventoryResponse[]> => {
  const params = search ? `?search=${encodeURIComponent(search)}` : '';
  const response = await apiClient.get(`/product-inventory/autocomplete${params}`);
  return response.data;
};

export const getProductInventoryById = async (variantId: number): Promise<any> => {
  const response = await apiClient.get(`/product-inventory/${variantId}`);
  return response.data;
};

 