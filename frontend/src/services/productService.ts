// services/productService.ts
import apiClient from '../lib/axios';
import { PageResponse } from '../types/PageResponse';
import { Product, ProductDetailResponse, ProductQueryParams, ProductAutoComplete } from '../types/product';
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

// to fetch the detail product page 
export const getProductById = async (id: number): Promise<ProductDetailResponse> => {
  const res = await apiClient.get<ProductDetailResponse>(`/products/${id}`);
  return res.data;
};