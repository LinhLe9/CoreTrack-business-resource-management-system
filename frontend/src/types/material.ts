export interface Material {
    id: number;
    sku: string;
    name: string;
    shortDes?: string; 
    groupMaterial: string; 
    status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED' | 'PENDING' | 'DELETE'; 
    uom: string;
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

export interface MaterialQueryParams {
  search?: string;
  page?: number;
  size?: number;
  sort?: string;

  groupMaterial?:  (string | number)[] | string | number; 
  status?:  (string | number)[] | string | number;      
}

export interface MaterialAutoComplete{
  id: number;
  sku: string;
  name: string;
  status: string;
  imageUrl: string;
  shortDes?: string;
}

export type BOMItem = {
  materialId: number;
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
  price: string;
  imageUrl: string;
  productGroupId: string;
  newProductGroupName: string;
  variants: Variant[];
};

export type Group = {
  id: number;
  name: string;
}