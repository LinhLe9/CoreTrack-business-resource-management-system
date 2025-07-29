import apiClient from '../lib/axios';
import { toBigDecimalString } from '../lib/utils';

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

export const createMaterialInventory = async (
  request: AddMaterialInventoryRequest
): Promise<AddMaterialInventoryResponse> => {
  const response = await apiClient.post('/material-inventory/add-inventory', request);
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