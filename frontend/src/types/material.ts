export interface Material {
    id: number;
    sku: string;
    name: string;
    shortDes?: string; 
    groupMaterial: string; 
    status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED' | 'DELETED'; 
    uom: string;
    imageUrl?: string; 
}

export interface MaterialVariantResponse {
  id: number;
  sku: string;
  name: string;
  shortDescription: string;
  imageUrl: string;
}

export interface MaterialSupplierResponse {
  supplierId: number;
  supplierName: string;
  price: number;
  currency: string;
  leadTimeDays: number;
  minOrderQuantity: number;
  supplierMaterialCode: string;
}

export interface MaterialVariantInventoryResponse {
  materialVariantResponse: MaterialVariantResponse;
  inventoryResponse: InventoryResponse | null;
}

export interface MaterialDetailResponse {
  id: number;
  sku: string;
  name: string;
  shortDes: string;
  groupMaterial: string;
  status: string;
  uom: string;
  imageUrl: string;
  variants: MaterialVariantInventoryResponse[];
  suppliers: MaterialSupplierResponse[];
}

export interface UpdateMaterialResponse {
  sku: string;
  name: string;
  shortDes: string;
  groupMaterial: string;
  isActive: boolean;
  imageUrl: string;
  createdAt: string;
  createdBy: string;
  variants: MaterialVariantResponse[];
  suppliers: MaterialSupplierResponse[];
  updatedAt: string;
  updatedBy: string;
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
  currentStock?: string;
  minAlertStock?: string;
  maxStockLevel?: string;
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

export type Supplier = {
  supplierId?: number;
  // newSupplierName?: string;
  // newSupplierEmail?: string;
  // newSupplierPhone?: string;
  price: number;
  currency: string;
  leadTime: number;
  minimumOrderQuantity: number;
  supplierMaterialCode: string;
};

export type Variant = {
  name: string;
  shortDescription: string;
  imageUrl: string;
};

export type AddMaterialForm = {
  name: string;
  description: string;
  sku: string;
  uom: string;
  imageUrl: string;
  materialGroupId: string;
  newMaterialGroupName: string;
  variants: Variant[];
  suppliers: Supplier[];
};

export type MaterialGroup = {
  id: number;
  name: string;
}