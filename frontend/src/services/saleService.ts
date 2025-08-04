import apiClient from '../lib/axios';
import {
  SaleCardResponse,
  SaleCreateRequest,
  SaleTicketResponse,
  SaleStatusTransitionRule,
  UpdateSaleOrderStatusRequest,
  SaleQueryParams,
  SaleResponse,
  OrderStatus,
} from '../types/sale';

// Get all sale tickets with filtering and pagination
export const getSaleTickets = async (params: SaleQueryParams = {}): Promise<SaleResponse> => {
  const queryParams = new URLSearchParams();
  
  if (params.search) queryParams.append('search', params.search);
  if (params.ticketStatus) {
    params.ticketStatus.forEach((status: string) => {
      queryParams.append('ticketStatus', status);
    });
  }
  if (params.page !== undefined) queryParams.append('page', params.page.toString());
  if (params.size !== undefined) queryParams.append('size', params.size.toString());
  if (params.sort) queryParams.append('sort', params.sort);

  const response = await apiClient.get(`/sale/filter?${queryParams.toString()}`);
  return response.data;
};

// Get sale ticket by ID
export const getSaleTicketById = async (id: number): Promise<SaleTicketResponse> => {
  const response = await apiClient.get(`/sale/${id}`);
  return response.data;
};

// Create a new sale ticket
export const createSaleTicket = async (request: SaleCreateRequest): Promise<SaleCardResponse> => {
  const response = await apiClient.post('/sale/bulk-create', request);
  return response.data;
};

// Get autocomplete suggestions for sale tickets
export const getSaleAutoComplete = async (search?: string): Promise<SaleCardResponse[]> => {
  const params = search ? `?search=${encodeURIComponent(search)}` : '';
  const response = await apiClient.get(`/sale/autocomplete${params}`);
  return response.data;
};

// Get status transition rules
export const getStatusTransitionRules = async (): Promise<SaleStatusTransitionRule[]> => {
  const response = await apiClient.get('/sale/status-rules');
  return response.data;
};

// Update sale order status
export const updateSaleOrderStatus = async (
  id: number,
  request: UpdateSaleOrderStatusRequest
): Promise<SaleTicketResponse> => {
  const response = await apiClient.put(`/sale/${id}/status`, request);
  return response.data;
};

// Cancel sale ticket
export const cancelSaleTicket = async (ticketId: number, reason: string): Promise<any> => {
  const response = await apiClient.put(`/sale/${ticketId}/cancel?reason=${encodeURIComponent(reason)}`);
  return response.data;
};

// Allocate order detail
export const allocateOrderDetail = async (ticketId: number, detailId: number): Promise<SaleTicketResponse> => {
  const response = await apiClient.put(`/sale/${ticketId}/detail/${detailId}/allocate`);
  return response.data;
};



// Helper function to create a simple sale ticket
export const createSimpleSaleTicket = async (
  sku: string,
  total: number,
  promotion: number,
  netTotal: number,
  expected_complete_date: string,
  details: Array<{ productVariantSku: string; quantity: number }>,
  customerName: string,
  customerEmail: string,
  customerPhone: string,
  customerAddress: string
): Promise<SaleCardResponse> => {
  const request: SaleCreateRequest = {
    sku,
    total: total.toString(),
    promotion: promotion.toString(),
    netTotal: netTotal.toString(),
    expected_complete_date,
    details: details.map(detail => ({
      productVariantSku: detail.productVariantSku,
      quantity: detail.quantity.toString(),
    })),
    customerName,
    customerEmail,
    customerPhone,
    customerAddress,
  };

  return createSaleTicket(request);
};

// Helper function to update status with simple parameters
export const updateSaleStatus = async (
  id: number,
  newStatus: OrderStatus,
  note?: string
): Promise<SaleTicketResponse> => {
  const request: UpdateSaleOrderStatusRequest = {
    newStatus,
    note,
  };

  return updateSaleOrderStatus(id, request);
}; 