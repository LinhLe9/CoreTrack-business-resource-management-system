// services/materialService.ts
import apiClient from '../lib/axios';
import { PageResponse } from '../types/PageResponse';
import { Material, ProductDetailResponse, MaterialDetailResponse, UpdateMaterialResponse, MaterialQueryParams, MaterialAutoComplete, MaterialGroup } from '../types/material';
import qs from 'qs'; 
import { MaterialVariantAutoComplete } from '../types/material';
import { MaterialSupplierResponse } from '../types/material';


// to fetch the product after searching + filtering
export const getMaterials = async (
  params:  MaterialQueryParams
  
): Promise<PageResponse<Material>> => {
  const defaultParams = {
    page: 0,      
    size: 10,     
    search: '',   
    groupMaterial: [], 
    status: []        
  };
  const mergedParams = { ...defaultParams, ...params };

  console.log('Sending params to /materials/filter', mergedParams);
  const response = await apiClient.get('/materials/filter', { 
    params: mergedParams,
    paramsSerializer: (params) =>
      qs.stringify(params, { arrayFormat: 'repeat' }) 
      // result: ?groupMaterial=A&groupMaterial=B&status=ACTIVE
  });
  return response.data;
};

// to fetch the material for auto complete while searching
export const getAllMaterialsForAutocomplete = async (): Promise<MaterialAutoComplete[]> => {
  const response = await apiClient.get('/materials/all'); 
  return response.data;
};

// Get material variants for autocomplete
export const getAllMaterialVariantsForAutocomplete = async (search?: string): Promise<MaterialVariantAutoComplete[]> => {
  try {
    const params = search ? `?search=${encodeURIComponent(search)}` : '';
    const response = await apiClient.get(`/materials/variants/autocomplete${params}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching material variants for autocomplete:', error);
    throw error;
  }
};

// to fetch the detail product page 
export const getProductById = async (id: number): Promise<ProductDetailResponse> => {
  const res = await apiClient.get<ProductDetailResponse>(`/products/${id}`);
  return res.data;
};

// to fetch material details by ID
export const getMaterialById = async (id: number): Promise<MaterialDetailResponse> => {
  const res = await apiClient.get<MaterialDetailResponse>(`/materials/${id}`);
  return res.data;
};

// to update material
export const updateMaterial = async (id: number, materialData: any): Promise<UpdateMaterialResponse> => {
  const response = await apiClient.put<UpdateMaterialResponse>(`/materials/${id}`, materialData);
  return response.data;
};

// to add new material
export const addMaterial = async (materialData: any): Promise<any> => {
  const response = await apiClient.post('/materials/add-material', materialData);
  return response.data;
};

// to fetch all group 
export const getAllMaterialGroup = async (): Promise<MaterialGroup[]> => {
  const res = await apiClient.get<MaterialGroup[]>('/materials/material-groups');
  return res.data;
};

// to get available status transitions for a material
export const getAvailableStatusTransitions = async (materialId: number): Promise<any> => {
  const response = await apiClient.get(`/materials/${materialId}/status-transitions`);
  return response.data;
};

// to change material status
export const changeMaterialStatus = async (materialId: number, newStatus: string, reason?: string): Promise<any> => {
  const response = await apiClient.put(`/materials/${materialId}/status`, {
    newStatus,
    reason
  });
  return response.data;
};

// Get suppliers by material variant SKU
export const getSuppliersByMaterialVariantSku = async (materialVariantSku: string): Promise<MaterialSupplierResponse[]> => {
  const response = await apiClient.get(`/materials/variants/${materialVariantSku}/suppliers`);
  return response.data;
};

// to delete a material (soft delete)
export const deleteMaterial = async (id: number): Promise<any> => {
  try {
    const response = await apiClient.delete(`/materials/${id}`);
    return response.data;
  } catch (error) {
    throw error;
  }
};