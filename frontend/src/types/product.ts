export interface Product {
    id: number;
    sku: string;
    name: string;
    shortDescription?: string; 
    groupProduct: string; 
    status: 'Active' | 'Inactive' | 'Discontinued' | 'Pending' | 'Delete'; 
    price: number;
    imageUrl?: string; 
}

export interface ProductDetailResponse {
  id: number;
  sku: string;
  name: string;
  description: string;
  group: string;
  status: string;
  price: number;
  imageUrl: string;
  variants: ProductVariantInventoryResponse[];
}

export interface ProductVariantInventoryResponse {
  variant: ProductVariantInfoResponse;
  inventory: InventoryResponse | null;
}

export interface ProductVariantInfoResponse {
  id: number;
  sku: string;
  name: string;
  description: string;
  imageUrl: string;
}

export interface InventoryResponse {
  currentStock?: number;
  minAlertStock?: number;
  maxStockLevel?: number;
}

export interface ProductQueryParams {
  search?: string;
  page?: number;
  size?: number;
  sort?: string;

  groupProduct?: string[]; 
  status?: string[];      
}
