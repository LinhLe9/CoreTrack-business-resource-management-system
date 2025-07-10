// services/productService.ts
import apiClient from '../lib/axios';
import { PageResponse } from '../types/PageResponse';
import { Product, ProductDetailResponse,ProductQueryParams } from '../types/product';

export const getProducts = async (
  params: ProductQueryParams
): Promise<PageResponse<Product>> => {
  const response = await apiClient.get('/products', { params });
  return response.data;
};

// ✅ Thêm API lấy toàn bộ để dùng cho autocomplete
export const getAllProductsForAutocomplete = async (): Promise<Product[]> => {
  const response = await apiClient.get('/products/all'); 
  return response.data;
};

export const getProductById = async (id: number): Promise<ProductDetailResponse> => {
  const res = await apiClient.get<ProductDetailResponse>(`/product/${id}`);
  return res.data;
};