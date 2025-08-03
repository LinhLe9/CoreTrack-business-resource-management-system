// Sale Types based on backend DTOs

export interface SaleCardResponse {
  id: number;
  sku: string;
  createdAt: string;
  status: string;
  detailNumber: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  customerAddress: string;
}

export interface SaleCreateDetailRequest {
  productVariantSku: string;
  quantity: string; // BigDecimal as string
}

export interface SaleCreateRequest {
  sku: string;
  total: string; // BigDecimal as string
  promotion: string; // BigDecimal as string
  netTotal: string; // BigDecimal as string
  expected_complete_date: string; // LocalDate as string
  details: SaleCreateDetailRequest[];
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  customerAddress: string;
}

export interface UpdateSaleOrderStatusRequest {
  newStatus: OrderStatus;
  note?: string;
}

export enum OrderStatus {
  NEW = 'NEW',
  ALLOCATED = 'ALLOCATED',
  PACKED = 'PACKED',
  SHIPPED = 'SHIPPED',
  DONE = 'DONE',
  CANCELLED = 'CANCELLED'
}

export interface SaleStatusTransitionRule {
  fromStatus: OrderStatus;
  toStatus: OrderStatus;
  allowedRoles: string[];
}

export interface SaleDetailResponse {
  id: number;
  productVariantSku: string;
  productVariantName: string;
  productName: string;
  quantity: string; // BigDecimal as string
  unitPrice: string; // BigDecimal as string
  totalPrice: string; // BigDecimal as string
  status: string;
  allocatedQuantity?: string; // BigDecimal as string
  allocatedAt?: string;
  allocatedBy?: string;
}

export interface SaleOrderStatusLogResponse {
  id: number;
  fromStatus: string;
  toStatus: string;
  note?: string;
  createdAt: string;
  createdBy: string;
}

export interface SaleTicketResponse {
  id: number;
  sku: string;
  total: string; // BigDecimal as string
  promotion: string; // BigDecimal as string
  netTotal: string; // BigDecimal as string
  expected_complete_date: string;
  completed_date?: string;
  status: string;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  customerAddress: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy?: string;
  details: SaleDetailResponse[];
  statusLogs: SaleOrderStatusLogResponse[];
}

export interface SaleQueryParams {
  search?: string;
  ticketStatus?: string[];
  page?: number;
  size?: number;
  sort?: string;
}

export interface SaleResponse {
  content: SaleCardResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// Helper function to convert number to BigDecimal string for backend
export const toBigDecimalString = (value: number): string => {
  return value.toString();
};

// Helper function to create sale create request
export const createSaleCreateRequest = (
  sku: string,
  total: number,
  promotion: number,
  netTotal: number,
  expected_complete_date: string,
  details: SaleCreateDetailRequest[],
  customerName: string,
  customerEmail: string,
  customerPhone: string,
  customerAddress: string
): SaleCreateRequest => {
  return {
    sku,
    total: toBigDecimalString(total),
    promotion: toBigDecimalString(promotion),
    netTotal: toBigDecimalString(netTotal),
    expected_complete_date,
    details,
    customerName,
    customerEmail,
    customerPhone,
    customerAddress,
  };
};

// Helper function to create sale detail request
export const createSaleDetailRequest = (
  productVariantSku: string,
  quantity: number
): SaleCreateDetailRequest => {
  return {
    productVariantSku,
    quantity: toBigDecimalString(quantity),
  };
}; 