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
  groupProduct?: (string | number)[] | string | number;
  status?: (string | number)[] | string | number;
}

export interface ProductAutoComplete{
  id: number;
  sku: string;
  name: string;
  status: string;
  imageUrl: string;
  shortDescription?: string;
}

export type BOMItem = {
  materialSku: string;
  quantity: number;
};

export type Variant = {
  name: string;
  shortDescription: string;
  imageUrl: string;
  bomItems: BOMItem[];
};

export type AddProductForm = {
  name: string;
  description: string;
  sku: string;
  price: number;
  currency: string;
  imageUrl: string;
  productGroupId: string;
  newProductGroupName: string;
  variants: Variant[];
  bomItems : BOMItem[];
};

export type Group = {
  id: number;
  name: string;
};