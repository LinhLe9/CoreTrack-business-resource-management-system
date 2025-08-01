// Production Ticket Status Enums
export enum ProductionTicketStatus {
  NEW = 'NEW',
  IN_PROGRESS = 'IN_PROGRESS',
  PARTIAL_COMPLETE = 'PARTIAL_COMPLETE',
  COMPLETE = 'COMPLETE',
  PARTIAL_CANCELLED = 'PARTIAL_CANCELLED',
  CANCELLED = 'CANCELLED'
}

export enum ProductionTicketDetailStatus {
  NEW = 'NEW',
  APPROVAL = 'APPROVAL',
  COMPLETE = 'COMPLETE',
  READY = 'READY',
  CLOSED = 'CLOSED',
  CANCELLED = 'CANCELLED'
}

// Base Types
export interface User {
  id: number;
  username: string;
  role: string;
}

export interface ProductVariant {
  id: number;
  sku: string;
  name: string;
  isActive: boolean;
}

export interface MaterialVariant {
  id: number;
  sku: string;
  name: string;
  isActive: boolean;
}

// BOM Item Types
export interface BomItemProductionTicketRequest {
  materialVariantSku: string;
  plannedQuantity: number;
  actualQuantity: number;
}

export interface BomItemProductionTicketResponse {
  id: number;
  materialVariantSku: string;
  actualQuantity: number;
  plannedQuantity: number;
}

// Production Ticket Detail Types
export interface ProductionTicketDetail {
  id: number;
  productVariant: ProductVariant;
  quantity: number;
  status: ProductionTicketDetailStatus;
  expected_complete_date: string;
  completed_date?: string;
  createdAt: string;
  updatedAt: string;
  createdBy: User;
  updatedBy: User;
  bomItem: BomItemProductionTicketResponse[];
}

export interface ProductTicketDetailShortResponse {
  id: number;
  productVariantSku: string;
  quantity: number;
  expected_complete_date: string;
  completed_date?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  createdByRole: string;
  updatedBy: string;
  updatedByRole: string;
}

// Production Ticket Types
export interface ProductionTicket {
  id: number;
  name: string;
  status: ProductionTicketStatus;
  isActive: boolean;
  completed_date?: string;
  createdAt: string;
  updatedAt: string;
  createdBy: User;
  updatedBy: User;
  ticketDetail: ProductionTicketDetail[];
}

export interface ProductionTicketResponse {
  id: number;
  name: string;
  completed_date?: string;
  status: string;
  createdAt: string;
  createdBy: string;
  createdByRole: string;
  updatedAt: string;
  updatedBy: string;
  updatedByRole: string;
  detail: ProductTicketDetailShortResponse[];
}

export interface ProductionTicketCardResponse {
  id: number;
  name: string;
  status: string;
  createdAt: string;
  createdBy: string;
  createdByRole: string;
  updatedAt: string;
  updatedBy: string;
  updatedByRole: string;
}

export interface ProductionTicketDetailResponse {
  id: number;
  productVariant: ProductVariant;
  quantity: number;
  status: string;
  expected_complete_date: string;
  completed_date?: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  createdByRole: string;
  updatedBy: string;
  updatedByRole: string;
  bomItem: BomItemProductionTicketResponse[];
}

// Create Production Ticket Types
export interface CreateProductionTicketRequest {
  variantSku: string;
  quantity: number;
  expected_complete_date: string;
  boms?: BomItemProductionTicketRequest[];
  status?: ProductionTicketDetailStatus;
}

export interface CreateProductionTicketResponse {
  id: number;
  name: string;
  productVariantSku: string;
  productVariantId: number;
  quantity: number;
  status: string;
  expected_complete_date: string;
  completed_date?: string;
  createdAt: string;
  createdBy: string;
  createdByRole: string;
}

// Bulk Create Production Ticket Types
export interface ProductVariantBomRequest {
  productVariantSku: string;
  quantity: number;
  expectedCompleteDate: string;
  boms?: BomItemProductionTicketRequest[];
}

export interface BulkCreateProductionTicketRequest {
  name: string;
  productVariants: ProductVariantBomRequest[];
}

export interface BulkCreateProductionTicketResponse {
  success: boolean;
  message: string;
  createdTickets: CreateProductionTicketResponse[];
  errors: string[];
  totalRequested: number;
  totalCreated: number;
  totalFailed: number;
}

// Status Management Types
export interface StatusTransitionRule {
  currentStatus: ProductionTicketDetailStatus;
  allowedTransitions: ProductionTicketDetailStatus[];
}

export interface UpdateDetailStatusRequest {
  newStatus: ProductionTicketDetailStatus;
  note?: string;
}

export interface StatusInfo {
  name: string;
  displayName: string;
  description: string;
}

export interface ProductionTicketStatusesResponse {
  productionTicketStatuses: StatusInfo[];
  productionTicketDetailStatuses: StatusInfo[];
}

// Filter and Search Types
export interface ProductionTicketFilter {
  search?: string;
  ticketStatus?: string[];
  page?: number;
  size?: number;
}

// API Response Types
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

// Error Types
export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
} 