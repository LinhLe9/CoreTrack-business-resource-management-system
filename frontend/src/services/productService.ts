// services/productService.ts
import apiClient from '../lib/axios';
import { PageResponse } from '../types/PageResponse';
import { Product, ProductDetailResponse, ProductQueryParams, ProductAutoComplete, ProductVariantAutoComplete, BOMItemResponse } from '../types/product';
import qs from 'qs'; 


// to fetch the product after searching + filtering
export const getProducts = async (
  params: ProductQueryParams
  
): Promise<PageResponse<Product>> => {
  const defaultParams = {
    page: 0,      
    size: 10,     
    search: '',   
    groupProduct: [], 
    status: []        
  };
  const mergedParams = { ...defaultParams, ...params };

  console.log('Sending params to /products/filter', mergedParams);
  const response = await apiClient.get('/products/filter', { 
    params: mergedParams,
    paramsSerializer: (params) =>
      qs.stringify(params, { arrayFormat: 'repeat' }) 
      // result: ?groupProduct=A&groupProduct=B&status=ACTIVE
  });
  return response.data;
};

// to fetch the product for auto complete while searching
export const getAllProductsForAutocomplete = async (): Promise<ProductAutoComplete[]> => {
  const response = await apiClient.get('/products/all');
  return response.data;
};

export const getAllProductVariantsForAutocomplete = async (search?: string): Promise<ProductVariantAutoComplete[]> => {
  const params = search ? `?search=${encodeURIComponent(search)}` : '';
  const response = await apiClient.get(`/products/variants/autocomplete${params}`);
  return response.data;
};

// to fetch the detail product page 
export const getProductById = async (id: number): Promise<ProductDetailResponse> => {
  const res = await apiClient.get<ProductDetailResponse>(`/products/${id}`);
  return res.data;
};

// to update product
export const updateProduct = async (id: number, data: any): Promise<any> => {
  try {
    const response = await apiClient.put(`/products/${id}`, data);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// to get all product groups
export const getAllProductGroups = async (): Promise<any[]> => {
  try {
    const response = await apiClient.get('/products/product-groups');
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const getAvailableStatusTransitions = async (productId: number): Promise<any> => {
  try {
    const response = await apiClient.get(`/products/${productId}/status-transitions`);
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const changeProductStatus = async (productId: number, newStatus: string, reason?: string): Promise<any> => {
  try {
    const response = await apiClient.put(`/products/${productId}/status`, {
      newStatus,
      reason
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// to get BOM items for a specific product variant
export const getBomItem = async (productId: number, variantId: number): Promise<BOMItemResponse[]> => {
  try {
    const response = await apiClient.get<BOMItemResponse[]>(`/products/${productId}/variant/${variantId}`);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// to delete a product (soft delete)
export const deleteProduct = async (id: number): Promise<any> => {
  try {
    const response = await apiClient.delete(`/products/${id}`);
    return response.data;
  } catch (error) {
    throw error;
  }
};