import api from '@/lib/axios';
import { Supplier, SupplierQueryParams, SupplierAutoComplete } from '@/types/supplier';
import { PageResponse } from '@/types/PageResponse';

// Get suppliers with filters
export const getSuppliers = async (params: SupplierQueryParams): Promise<PageResponse<Supplier>> => {
  const queryParams = new URLSearchParams();
  
  if (params.search) queryParams.append('search', params.search);
  if (params.page !== undefined) queryParams.append('page', params.page.toString());
  if (params.size) queryParams.append('size', params.size.toString());
  if (params.sort) queryParams.append('sort', params.sort);
  
  if (params.country) {
    if (Array.isArray(params.country)) {
      params.country.forEach(country => queryParams.append('country', country.toString()));
    } else {
      queryParams.append('country', params.country.toString());
    }
  }

  console.log('Sending params to /suppliers/filter', Object.fromEntries(queryParams));
  const res = await api.get<PageResponse<Supplier>>(`/suppliers/filter?${queryParams.toString()}`);
  return res.data;
};

// Get supplier by ID
export const getSupplierById = async (id: number): Promise<Supplier> => {
  const res = await api.get<Supplier>(`/suppliers/${id}`);
  return res.data;
};

// Get all suppliers for autocomplete
export const getAllSuppliersForAutocomplete = async (): Promise<SupplierAutoComplete[]> => {
  const res = await api.get<SupplierAutoComplete[]>('/suppliers/all');
  return res.data;
};

// Add new supplier
export const addSupplier = async (supplier: Omit<Supplier, 'id'>): Promise<Supplier> => {
  const res = await api.post<Supplier>('/suppliers/add-supplier', supplier);
  return res.data;
};

// Update supplier
export const updateSupplier = async (id: number, supplier: Partial<Supplier>): Promise<Supplier> => {
  const res = await api.put<Supplier>(`/suppliers/${id}`, supplier);
  return res.data;
};

// Delete supplier
export const deleteSupplier = async (id: number): Promise<void> => {
  await api.delete(`/suppliers/${id}`);
}; 