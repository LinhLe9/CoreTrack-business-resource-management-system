export interface Supplier {
    id: number;
    name: string;
    contactPerson?: string; 
    email?: string;
    phone?: string;
    address?: string; 
    country?: string;
    description?: string;
    status: 'Active' | 'Inactive';
}

export interface SupplierAutoComplete{
  id: number;
  name: string;
  contactPerson?: string;
  email?: string;
  phone?: string;
  address?: string; 
  country?: string;
}

export interface SupplierQueryParams{
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
  country?: (string | number)[] | string | number;
}