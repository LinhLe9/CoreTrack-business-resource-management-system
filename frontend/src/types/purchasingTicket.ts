// Purchasing Ticket Status Enums
export enum PurchasingTicketStatus {
  NEW = 'NEW',
  PARTIAL_APPROVAL = 'PARTIAL_APPROVAL',
  APPROVAL = 'APPROVAL',
  PARTIAL_SUCCESSFUL = 'PARTIAL_SUCCESSFUL',
  SUCCESSFUL = 'SUCCESSFUL',
  PARTIAL_SHIPPING = 'PARTIAL_SHIPPING',
  SHIPPING = 'SHIPPING',
  PARTIAL_READY = 'PARTIAL_READY',
  READY = 'READY',
  CLOSED = 'CLOSED',
  PARTIAL_CANCELLED = 'PARTIAL_CANCELLED',
  CANCELLED = 'CANCELLED'
}

export enum PurchasingTicketDetailStatus {
  NEW = 'NEW',
  APPROVAL = 'APPROVAL',
  SUCCESSFUL = 'SUCCESSFUL',
  SHIPPING = 'SHIPPING',
  READY = 'READY',
  CLOSED = 'CLOSED',
  CANCELLED = 'CANCELLED'
}

// Import MaterialSupplierResponse from material types
import { MaterialSupplierResponse } from './material';

// Base Types
export interface User {
  id: number;
  username: string;
  role: string;
}

export interface MaterialVariant {
  id: number;
  sku: string;
  name: string;
  isActive: boolean;
}

// Purchasing Ticket Detail Types
export interface PurchasingTicketDetail {
  id: number;
  materialVariant: MaterialVariant;
  quantity: number;
  status: PurchasingTicketDetailStatus;
  expected_ready_date: string;
  ready_date?: string;
  createdAt: string;
  updatedAt: string;
  createdBy: User;
  updatedBy: User;
}

export interface PurchasingTicketDetailShortResponse {
  id: number;
  materialVariantSku: string;
  quantity: number;
  expected_ready_date: string;
  ready_date?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  createdByRole: string;
  updatedBy: string;
  updatedByRole: string;
}

// Purchasing Ticket Types
export interface PurchasingTicket {
  id: number;
  name: string;
  status: PurchasingTicketStatus;
  isActive: boolean;
  completed_date?: string;
  createdAt: string;
  updatedAt: string;
  createdBy: User;
  updatedBy: User;
  ticketDetail: PurchasingTicketDetail[];
}

export interface PurchasingTicketStatusLogResponse {
  id: number;
  purchasingTicketId: number;
  purchasingTicketName: string;
  new_status: string;
  old_status: string;
  note: string;
  updatedAt: string;
  updatedByName: string;
  updatedByRole: string;
}

export interface PurchasingTicketDetailStatusLogResponse {
  id: number;
  purchasingTicketDetailId: number;
  new_status: string;
  old_status: string;
  note: string;
  updatedAt: string;
  updatedByName: string;
  updatedByRole: string;
}

export interface PurchasingTicketResponse {
  id: number;
  name: string;
  completed_date?: string;
  status: string;
  createdAt: string;
  createdBy: string;
  createdByRole: string;
  lastUpdatedAt: string;
  lastUpdateBy: string;
  lastUpdateByRole: string;
  detail: PurchasingTicketDetailShortResponse[];
  logs: PurchasingTicketStatusLogResponse[];
}

export interface PurchasingTicketCardResponse {
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
}

export interface PurchasingTicketDetailResponse {
  id: number;
  materialVariantSku: string;
  quantity: number;
  status: string;
  expected_ready_date: string;
  ready_date?: string;
  createdAt: string;
  lastUpdatedAt: string;
  createdBy_name: string;
  createdBy_role: string;
  lastUpdatedAt_name: string;
  lastUpdatedAt_role: string;
  logs: PurchasingTicketDetailStatusLogResponse[];
  materialSuppliers: MaterialSupplierResponse[];
}

// Request Types
export interface CreatePurchasingTicketRequest {
  materialVariantSku: string;
  quantity: number;
  expectedReadyDate: string; // YYYY-MM-DD date string
}

export interface CreatePurchasingTicketResponse {
  id: number;
  name: string;
  materialVariantSku: string;
  materialVariantId: number;
  quantity: number;
  status: string;
  expected_ready_date: string;
  ready_date?: string;
  createdAt: string;
  createdBy: string;
  createdByRole: string;
}

export interface MaterialVariantRequest {
  materialVariantSku: string;
  quantity: number;
  expectedReadyDate: string; // YYYY-MM-DD date string
}

export interface BulkCreatePurchasingTicketRequest {
  name: string;
  singleTicket: CreatePurchasingTicketRequest[];
}

export interface BulkCreatePurchasingTicketResponse {
  success: boolean;
  message: string;
  createdTickets: CreatePurchasingTicketResponse[];
  errors: string[];
  totalRequested: number;
  totalCreated: number;
  totalFailed: number;
}

export interface StatusTransitionRule {
  currentStatus: PurchasingTicketDetailStatus;
  allowedTransitions: PurchasingTicketDetailStatus[];
  description: string;
}

export interface UpdatePurchasingDetailStatusRequest {
  newStatus: PurchasingTicketDetailStatus;
  note?: string;
}

export interface StatusInfo {
  name: string;
  displayName: string;
  description: string;
}

export interface PurchasingTicketStatusesResponse {
  purchasingTicketStatuses: StatusInfo[];
  purchasingTicketDetailStatuses: StatusInfo[];
}

// Filter Types
export interface PurchasingTicketFilter {
  search?: string;
  ticketStatus?: string[];
  page?: number;
  size?: number;
}

// Re-export PageResponse from existing types
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