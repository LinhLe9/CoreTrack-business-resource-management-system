// services/materialService.ts
import apiClient from '../lib/axios';
import { PageResponse } from '../types/PageResponse';
import { Material, ProductDetailResponse, MaterialQueryParams, MaterialAutoComplete } from '../types/material';
import qs from 'qs'; 


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

// to fetch the detail product page 
export const getProductById = async (id: number): Promise<ProductDetailResponse> => {
  const res = await apiClient.get<ProductDetailResponse>(`/products/${id}`);
  return res.data;
};