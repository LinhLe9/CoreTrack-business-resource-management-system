export interface Product {
    id: number;
    sku: string;
    name: string;
    shortDescription?: string; 
    group: string; 
    status: 'Active' | 'Inactive' | 'Discontinued' | 'Deleted'; 
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
  currency: string;
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
  bomItems?: BOMItemResponse[];
}

export interface BOMItemResponse {
  id: number;
  materialSku: string; // Changed from materialId to materialSku to match backend
  materialName: string;
  quantity: string; // BigDecimal from backend
  uom: string;
  notes?: string;
}

export interface InventoryResponse {
  currentStock?: string;
  minAlertStock?: string;
  maxStockLevel?: string;
}

export interface ProductQueryParams{
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
  groupProducts?: (string | number)[] | string | number; // Changed from groupProduct to groupProducts
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

export interface ProductVariantAutoComplete {
  variantId: number;
  productName: string;
  productSku: string;
  variantSku: string;
  variantName: string;
  productGroup?: string;
}

export type BOMItem = {
  materialSku: string;
  materialId?: number;
  materialName?: string;
  quantity: string; // BigDecimal from backend
  uom?: string;
  notes?: string;
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