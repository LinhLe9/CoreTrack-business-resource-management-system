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
import {
  MaterialInventoryQueryParams,
  MaterialInventoryFilterParams,
  MaterialInventoryAutoComplete,
  AddMaterialInventoryRequest,
  AddMaterialInventoryResponse,
  MaterialInventoryDetailResponse,
  StockSetRequest,
  StockModifyRequest,
  BulkStockSetRequest as MaterialBulkStockSetRequest,
  BulkStockModifyRequest as MaterialBulkStockModifyRequest,
  MaterialBulkInventoryTransactionResponse,
  BulkMInitInventoryRequest,
  BulkMInitInventoryResponse
} from '../types/materialInventory';

// Material Inventory Autocomplete
export const getAllMaterialInventoryForAutocomplete = async (search?: string): Promise<AllSearchInventoryResponse[]> => {
  const params = search ? `?search=${encodeURIComponent(search)}` : '';
  const response = await apiClient.get(`/material-inventory/autocomplete${params}`);
  return response.data;
};

// Material Inventory Search and Filter
export const getMaterialInventoryFilter = async (params: any): Promise<any> => {
  const queryParams = new URLSearchParams();
  
  if (params.search) queryParams.append('search', params.search);
  if (params.groupMaterials) {
    params.groupMaterials.forEach((group: string) => {
      queryParams.append('groupMaterials', group);
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

  const response = await apiClient.get(`/material-inventory/filter?${queryParams.toString()}`);
  return response.data;
};

export const getMaterialInventoryById = async (variantId: number): Promise<MaterialInventoryDetailResponse> => {
  const response = await apiClient.get(`/material-inventory/${variantId}`);
  return response.data;
};

// Material Transaction Enums
export const getMaterialTransactionEnums = async (): Promise<TransactionEnumsResponse> => {
  const response = await apiClient.get('/material-inventory/enums/transaction-enums');
  return response.data;
};

// Material Stock Operations
export const addMaterialStock = async (
  variantId: number,
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): Promise<InventoryTransactionResponse> => {
  const payload: StockModifyRequest = {
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
  
  const response = await apiClient.post(`/material-inventory/${variantId}/add`, payload);
  return response.data;
};

export const subtractMaterialStock = async (
  variantId: number,
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): Promise<InventoryTransactionResponse> => {
  const payload: StockModifyRequest = {
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
  
  const response = await apiClient.post(`/material-inventory/${variantId}/subtract`, payload);
  return response.data;
};

export const setMaterialStock = async (
  variantId: number,
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): Promise<InventoryTransactionResponse> => {
  const payload: StockSetRequest = {
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
  
  const response = await apiClient.put(`/material-inventory/${variantId}/set`, payload);
  return response.data;
};

// Material Inventory Creation
export const createMaterialInventory = async (
  request: AddMaterialInventoryRequest
): Promise<AddMaterialInventoryResponse> => {
  const response = await apiClient.post('/material-inventory/init', request);
  return response.data;
};

// Helper function to convert number to BigDecimal string for backend
export const createMaterialInventoryRequest = (
  materialVariantSku: string,
  currentStock: number,
  minAlertStock?: number,
  maxStockLevel?: number
): AddMaterialInventoryRequest => {
  return {
    materialVariantSku,
    currentStock: toBigDecimalString(currentStock),
    minAlertStock: minAlertStock ? toBigDecimalString(minAlertStock) : undefined,
    maxStockLevel: maxStockLevel ? toBigDecimalString(maxStockLevel) : undefined,
  };
};

// Bulk Material Stock Operations
export const bulkSetMaterialStock = async (
  variantIds: number[],
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number
): Promise<MaterialBulkInventoryTransactionResponse> => {
  const payload: MaterialBulkStockSetRequest = {
    variantIds,
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
  };
  
  const response = await apiClient.put('/material-inventory/bulk/set', payload);
  return response.data;
};

export const bulkAddMaterialStock = async (
  variantIds: number[],
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): Promise<MaterialBulkInventoryTransactionResponse> => {
  const payload: MaterialBulkStockModifyRequest = {
    variantIds,
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
  
  const response = await apiClient.put('/material-inventory/bulk/add', payload);
  return response.data;
};

export const bulkSubtractMaterialStock = async (
  variantIds: number[],
  quantity: number,
  note?: string,
  referenceDocumentType?: string,
  referenceDocumentId?: number,
  transactionSource?: string
): Promise<MaterialBulkInventoryTransactionResponse> => {
  const payload: MaterialBulkStockModifyRequest = {
    variantIds,
    quantity: toBigDecimalString(quantity),
    note,
    referenceDocumentType,
    referenceDocumentId,
    transactionSource,
  };
  
  const response = await apiClient.put('/material-inventory/bulk/subtract', payload);
  return response.data;
};

export const bulkInitMaterialInventory = async (
  materialVariantSkus: string[],
  currentStock: number,
  minAlertStock?: number,
  maxStockLevel?: number
): Promise<BulkMInitInventoryResponse> => {
  const payload: BulkMInitInventoryRequest = {
    materialVariantSkus,
    currentStock: toBigDecimalString(currentStock),
    minAlertStock: minAlertStock ? toBigDecimalString(minAlertStock) : undefined,
    maxStockLevel: maxStockLevel ? toBigDecimalString(maxStockLevel) : undefined,
  };
  
  console.log('=== SENDING BULK INIT MATERIAL REQUEST ===');
  console.log('Endpoint: /material-inventory/bulk/init');
  console.log('Payload:', payload);
  console.log('==========================================');
  
  const response = await apiClient.post('/material-inventory/bulk/init', payload);
  
  console.log('=== BULK INIT MATERIAL RESPONSE RECEIVED ===');
  console.log('Response:', response.data);
  console.log('============================================');
  
  return response.data;
};

// Helper function to create bulk init inventory request
export const createBulkMInitInventoryRequest = (
  materialVariantSkus: string[],
  currentStock: number,
  minAlertStock?: number,
  maxStockLevel?: number
): BulkMInitInventoryRequest => {
  return {
    materialVariantSkus,
    currentStock: toBigDecimalString(currentStock),
    minAlertStock: minAlertStock ? toBigDecimalString(minAlertStock) : undefined,
    maxStockLevel: maxStockLevel ? toBigDecimalString(maxStockLevel) : undefined,
  };
}; 