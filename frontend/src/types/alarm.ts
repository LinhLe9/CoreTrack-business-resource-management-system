export interface Alarm {
  id: number;
  type: 'LOW_STOCK' | 'OUT_OF_STOCK' | 'OVER_STOCK';
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  message: string;
  createdAt: string;
  isResolved: boolean;
  resolvedAt?: string;
  resolvedBy?: string;
}

export interface ProductAlarm extends Alarm {
  productId: number;
  productName: string;
  productSku: string;
  variantId?: number;
  variantName?: string;
  variantSku?: string;
  currentStock: number;
  minAlertStock: number;
  maxStockLevel: number;
}

export interface MaterialAlarm extends Alarm {
  materialId: number;
  materialName: string;
  materialSku: string;
  variantId?: number;
  variantName?: string;
  variantSku?: string;
  currentStock: number;
  minAlertStock: number;
  maxStockLevel: number;
}

export interface AlarmQueryParams {
  type?: string[];
  severity?: string[];
  isResolved?: boolean;
  page?: number;
  size?: number;
  sort?: string;
}

export interface AlarmResponse {
  content: Alarm[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
} 