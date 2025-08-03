import axios from '@/lib/axios';
import {
  CreatePurchasingTicketRequest,
  CreatePurchasingTicketResponse,
  PurchasingTicketResponse,
  PurchasingTicketCardResponse,
  PurchasingTicketDetailResponse,
  PageResponse,
  PurchasingTicketFilter,
  StatusTransitionRule,
  UpdatePurchasingDetailStatusRequest,
  BulkCreatePurchasingTicketRequest,
  BulkCreatePurchasingTicketResponse,
  PurchasingTicketStatusesResponse,
  PurchasingTicket,
  MaterialVariantRequest
} from '@/types/purchasingTicket';

const API_BASE_URL = '/purchasing-tickets';

export const purchasingTicketService = {
  // Bulk create purchasing tickets
  async bulkCreatePurchasingTicket(request: BulkCreatePurchasingTicketRequest): Promise<BulkCreatePurchasingTicketResponse> {
    const response = await axios.post(`${API_BASE_URL}/bulk-create`, request);
    return response.data;
  },

  // Get purchasing ticket by ID
  async getPurchasingTicketById(id: number): Promise<PurchasingTicketResponse> {
    const response = await axios.get(`${API_BASE_URL}/${id}`);
    return response.data;
  },

  // Get purchasing tickets with filtering and pagination
  async getPurchasingTickets(filter: PurchasingTicketFilter = {}): Promise<PageResponse<PurchasingTicketCardResponse>> {
    const params = new URLSearchParams();
    
    if (filter.search) {
      params.append('search', filter.search);
    }
    
    if (filter.ticketStatus && filter.ticketStatus.length > 0) {
      filter.ticketStatus.forEach(status => {
        params.append('ticketStatus', status);
      });
    }
    
    if (filter.page !== undefined) {
      params.append('page', filter.page.toString());
    }
    
    if (filter.size !== undefined) {
      params.append('size', filter.size.toString());
    }

    const response = await axios.get(`${API_BASE_URL}/filter?${params.toString()}`);
    return response.data;
  },

  // Get autocomplete suggestions
  async getAutoComplete(search: string): Promise<PurchasingTicketCardResponse[]> {
    const response = await axios.get(`${API_BASE_URL}/autocomplete?search=${encodeURIComponent(search)}`);
    return response.data;
  },

  // Get status transition rules
  async getStatusTransitionRules(): Promise<StatusTransitionRule[]> {
    const response = await axios.get(`${API_BASE_URL}/status-rules`);
    return response.data;
  },

  // Get all purchasing ticket statuses
  async getAllPurchasingTicketStatuses(): Promise<PurchasingTicketStatusesResponse> {
    const response = await axios.get(`${API_BASE_URL}/statuses`);
    return response.data;
  },

  // Get specific purchasing ticket detail
  async getPurchasingTicketDetails(ticketId: number, detailId: number): Promise<PurchasingTicketDetailResponse> {
    const response = await axios.get(`${API_BASE_URL}/${ticketId}/details/${detailId}`);
    return response.data;
  },

  // Update purchasing ticket detail status
  async updateDetailStatus(
    ticketId: number, 
    detailId: number, 
    request: UpdatePurchasingDetailStatusRequest
  ): Promise<PurchasingTicketDetailResponse> {
    const response = await axios.put(`${API_BASE_URL}/${ticketId}/details/${detailId}/status`, request);
    return response.data;
  },

  // Cancel entire purchasing ticket
  async cancelPurchasingTicket(ticketId: number, reason: string): Promise<PurchasingTicket> {
    const response = await axios.put(`${API_BASE_URL}/${ticketId}/cancel?reason=${encodeURIComponent(reason)}`);
    return response.data;
  },

  // Cancel specific purchasing ticket detail
  async cancelPurchasingTicketDetail(
    ticketId: number, 
    detailId: number, 
    reason: string
  ): Promise<PurchasingTicketDetailResponse> {
    const response = await axios.put(`${API_BASE_URL}/${ticketId}/details/${detailId}/cancel?reason=${encodeURIComponent(reason)}`);
    return response.data;
  },

  // Utility methods for date formatting
  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  },

  // Get status color for UI
  getStatusColor(status: string): string {
    switch (status?.toUpperCase()) {
      case 'NEW':
        return 'bg-blue-100 text-blue-800';
      case 'PARTIAL_APPROVAL':
        return 'bg-yellow-100 text-yellow-800';
      case 'APPROVAL':
        return 'bg-purple-100 text-purple-800';
      case 'PARTIAL_SUCCESSFUL':
        return 'bg-orange-100 text-orange-800';
      case 'SUCCESSFUL':
        return 'bg-green-100 text-green-800';
      case 'PARTIAL_SHIPPING':
        return 'bg-orange-100 text-orange-800';
      case 'SHIPPING':
        return 'bg-cyan-100 text-cyan-800';
      case 'PARTIAL_READY':
        return 'bg-orange-100 text-orange-800';
      case 'READY':
        return 'bg-green-100 text-green-800';
      case 'CLOSED':
        return 'bg-gray-100 text-gray-800';
      case 'PARTIAL_CANCELLED':
        return 'bg-red-100 text-red-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  },

  // Get status badge text
  getStatusBadgeText(status: string): string {
    switch (status?.toUpperCase()) {
      case 'NEW':
        return 'New';
      case 'PARTIAL_APPROVAL':
        return 'Partial Approval';
      case 'APPROVAL':
        return 'Approval';
      case 'PARTIAL_SUCCESSFUL':
        return 'Partial Successful';
      case 'SUCCESSFUL':
        return 'Successful';
      case 'PARTIAL_SHIPPING':
        return 'Partial Shipping';
      case 'SHIPPING':
        return 'Shipping';
      case 'PARTIAL_READY':
        return 'Partial Ready';
      case 'READY':
        return 'Ready';
      case 'CLOSED':
        return 'Closed';
      case 'PARTIAL_CANCELLED':
        return 'Partial Cancelled';
      case 'CANCELLED':
        return 'Cancelled';
      default:
        return status || 'Unknown';
    }
  },

  // Validation methods
  validateCreateRequest(request: CreatePurchasingTicketRequest): string[] {
    const errors: string[] = [];

    if (!request.materialVariantSku || request.materialVariantSku.trim() === '') {
      errors.push('Material Variant SKU is required');
    }

    if (!request.quantity || request.quantity <= 0) {
      errors.push('Quantity must be greater than 0');
    }

    if (!request.expectedReadyDate) {
      errors.push('Expected ready date is required');
    } else {
      const date = new Date(request.expectedReadyDate);
      if (isNaN(date.getTime())) {
        errors.push('Invalid expected ready date format');
      }
    }

    return errors;
  },

  validateBulkCreateRequest(request: BulkCreatePurchasingTicketRequest): string[] {
    const errors: string[] = [];

    if (!request.name || request.name.trim() === '') {
      errors.push('Ticket name is required');
    }

    if (!request.singleTicket || request.singleTicket.length === 0) {
      errors.push('At least one material variant is required');
    } else {
      request.singleTicket.forEach((ticket, index) => {
        const ticketErrors = this.validateCreateRequest(ticket);
        ticketErrors.forEach(error => {
          errors.push(`Item ${index + 1}: ${error}`);
        });
      });
    }

    return errors;
  },

  // Sample data creation methods for testing
  createSampleCreateRequest(): CreatePurchasingTicketRequest {
    return {
      materialVariantSku: 'MAT-001',
      quantity: 100,
      expectedReadyDate: '2024-12-31'
    };
  },

  createSampleBulkCreateRequest(): BulkCreatePurchasingTicketRequest {
    return {
      name: 'Sample Purchasing Ticket',
      singleTicket: [
        {
          materialVariantSku: 'MAT-001',
          quantity: 100,
          expectedReadyDate: '2024-12-31'
        },
        {
          materialVariantSku: 'MAT-002',
          quantity: 50,
          expectedReadyDate: '2024-12-30'
        }
      ]
    };
  },

  // Test methods for development
  async testBulkCreateFunctionality(): Promise<any> {
    const sampleRequest = this.createSampleBulkCreateRequest();
    return await this.bulkCreatePurchasingTicket(sampleRequest);
  },

  async testSearchFunctionality(): Promise<any> {
    return await this.getPurchasingTickets({ search: 'test', page: 0, size: 10 });
  },

  async testAutocompleteFunctionality(): Promise<any> {
    return await this.getAutoComplete('test');
  },

  async testDetailsFunctionality(): Promise<any> {
    // This would need a valid ticket ID
    return await this.getPurchasingTicketById(1);
  },

  async testStatusManagementFunctionality(): Promise<any> {
    return await this.getStatusTransitionRules();
  },

  async testStatusesFunctionality(): Promise<any> {
    return await this.getAllPurchasingTicketStatuses();
  }
}; 