import axios from '@/lib/axios';
import {
  CreateProductionTicketRequest,
  CreateProductionTicketResponse,
  ProductionTicketResponse,
  ProductionTicketCardResponse,
  ProductionTicketDetailResponse,
  PageResponse,
  ProductionTicketFilter,
  StatusTransitionRule,
  UpdateDetailStatusRequest,
  BulkCreateProductionTicketRequest,
  BulkCreateProductionTicketResponse,
  ProductionTicketStatusesResponse,
  ProductionTicket,
  BomItemProductionTicketRequest
} from '@/types/productionTicket';

const API_BASE_URL = '/production-tickets';

export const productionTicketService = {
  // Create a single production ticket
  async createProductionTicket(request: CreateProductionTicketRequest): Promise<CreateProductionTicketResponse> {
    const response = await axios.post(`${API_BASE_URL}/create`, request);
    return response.data;
  },

  // Bulk create production tickets
  async bulkCreateProductionTicket(request: BulkCreateProductionTicketRequest): Promise<BulkCreateProductionTicketResponse> {
    const response = await axios.post(`${API_BASE_URL}/bulk-create`, request);
    return response.data;
  },

  // Get production ticket by ID
  async getProductionTicketById(id: number): Promise<ProductionTicketResponse> {
    const response = await axios.get(`${API_BASE_URL}/${id}`);
    return response.data;
  },

  // Get production tickets with filtering and pagination
  async getProductionTickets(filter: ProductionTicketFilter = {}): Promise<PageResponse<ProductionTicketCardResponse>> {
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
  async getAutoComplete(search: string): Promise<ProductionTicketCardResponse[]> {
    const response = await axios.get(`${API_BASE_URL}/autocomplete?search=${encodeURIComponent(search)}`);
    return response.data;
  },

  // Get status transition rules
  async getStatusTransitionRules(): Promise<StatusTransitionRule[]> {
    const response = await axios.get(`${API_BASE_URL}/status-rules`);
    return response.data;
  },

  // Get all production ticket statuses
  async getAllProductionTicketStatuses(): Promise<ProductionTicketStatusesResponse> {
    const response = await axios.get(`${API_BASE_URL}/statuses`);
    return response.data;
  },

  // Get specific production ticket detail
  async getProductionTicketDetails(ticketId: number, detailId: number): Promise<ProductionTicketDetailResponse> {
    const response = await axios.get(`${API_BASE_URL}/${ticketId}/details/${detailId}`);
    return response.data;
  },

  // Update production ticket detail status
  async updateDetailStatus(
    ticketId: number, 
    detailId: number, 
    request: UpdateDetailStatusRequest
  ): Promise<ProductionTicketDetailResponse> {
    const response = await axios.put(`${API_BASE_URL}/${ticketId}/details/${detailId}/status`, request);
    return response.data;
  },

  // Cancel entire production ticket
  async cancelProductionTicket(ticketId: number, reason: string): Promise<ProductionTicket> {
    const response = await axios.put(`${API_BASE_URL}/${ticketId}/cancel?reason=${encodeURIComponent(reason)}`);
    return response.data;
  },

  // Cancel specific production ticket detail
  async cancelProductionTicketDetail(
    ticketId: number, 
    detailId: number, 
    reason: string
  ): Promise<ProductionTicketDetailResponse> {
    const response = await axios.put(`${API_BASE_URL}/${ticketId}/details/${detailId}/cancel?reason=${encodeURIComponent(reason)}`);
    return response.data;
  },

  // Delete production ticket from catalog (soft delete)
  async deleteProductionTicket(ticketId: number, reason: string): Promise<ProductionTicket> {
    const response = await axios.delete(`${API_BASE_URL}/${ticketId}/delete?reason=${encodeURIComponent(reason)}`);
    return response.data;
  },

  // Test endpoints for development
  async testCascadeRelationships(id: number): Promise<boolean> {
    const response = await axios.get(`${API_BASE_URL}/test-cascade/${id}`);
    return response.data.success;
  },

  async testSampleData(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-sample`);
    return response.data;
  },

  async testSearchFunctionality(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-search`);
    return response.data;
  },

  async testAutocompleteFunctionality(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-autocomplete`);
    return response.data;
  },

  async testDetailsFunctionality(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-details`);
    return response.data;
  },

  async testStatusManagementFunctionality(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-status-management`);
    return response.data;
  },

  async testBulkCreateFunctionality(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-bulk-create`);
    return response.data;
  },

  async testStatusesFunctionality(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-statuses`);
    return response.data;
  },

  async testInventoryIntegration(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-inventory-integration`);
    return response.data;
  },

  async testLoggingFunctionality(): Promise<any> {
    const response = await axios.get(`${API_BASE_URL}/test-logging`);
    return response.data;
  }
};

// Utility functions for common operations
export const productionTicketUtils = {
  // Format date for display
  formatDate(dateString: string): string {
    if (!dateString || dateString.trim() === '') {
      return 'N/A';
    }
    
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return 'Invalid Date';
      }
      
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      console.error('Error formatting date:', dateString, error);
      return 'Invalid Date';
    }
  },

  // Get status color for UI
  getStatusColor(status: string): string {
    const statusColors: Record<string, string> = {
      'New': 'bg-blue-100 text-blue-800',
      'In Progress': 'bg-yellow-100 text-yellow-800',
      'Partial Complete': 'bg-orange-100 text-orange-800',
      'Complete': 'bg-green-100 text-green-800',
      'Partial Cancelled': 'bg-red-100 text-red-800',
      'Cancelled': 'bg-gray-100 text-gray-800',
      'Approved': 'bg-purple-100 text-purple-800',
      'Ready': 'bg-green-100 text-green-800',
      'Closed': 'bg-gray-100 text-gray-800'
    };
    return statusColors[status] || 'bg-gray-100 text-gray-800';
  },

  // Get status badge text
  getStatusBadgeText(status: string): string {
    const statusDisplayNames: Record<string, string> = {
      'New': 'New',
      'In Progress': 'In Progress',
      'Partial Complete': 'Partial Complete',
      'Complete': 'Complete',
      'Partial Cancelled': 'Partial Cancelled',
      'Cancelled': 'Cancelled',
      'Approved': 'Approved',
      'Ready': 'Ready',
      'Closed': 'Closed'
    };
    return statusDisplayNames[status] || status;
  },

  // Validate production ticket request
  validateCreateRequest(request: CreateProductionTicketRequest): string[] {
    const errors: string[] = [];
    
    if (!request.variantSku || request.variantSku.trim() === '') {
      errors.push('Product variant SKU is required');
    }
    
    if (!request.quantity || request.quantity <= 0) {
      errors.push('Quantity must be greater than 0');
    }
    
    if (!request.expected_complete_date) {
      errors.push('Expected complete date is required');
    }
    
    return errors;
  },

  // Validate bulk create request
  validateBulkCreateRequest(request: BulkCreateProductionTicketRequest): string[] {
    const errors: string[] = [];
    
    if (!request.name || request.name.trim() === '') {
      errors.push('Production ticket name is required');
    }
    
    if (!request.productVariants || request.productVariants.length === 0) {
      errors.push('At least one product variant is required');
    }
    
    request.productVariants?.forEach((variant, index) => {
      if (!variant.productVariantSku || variant.productVariantSku.trim() === '') {
        errors.push(`Product variant SKU is required for item ${index + 1}`);
      }
      
      if (!variant.quantity || variant.quantity <= 0) {
        errors.push(`Quantity must be greater than 0 for item ${index + 1}`);
      }
      
      if (!variant.expectedCompleteDate) {
        errors.push(`Expected complete date is required for item ${index + 1}`);
      }
    });
    
    return errors;
  },

  // Create sample data for testing
  createSampleCreateRequest(): CreateProductionTicketRequest {
    return {
      variantSku: 'PROD-001',
      quantity: 10,
      expected_complete_date: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
      boms: [
        {
          materialVariantSku: 'MAT-001',
          plannedQuantity: 5,
          actualQuantity: 0
        }
      ]
    };
  },

  createSampleBulkCreateRequest(): BulkCreateProductionTicketRequest {
    return {
      name: 'Sample Bulk Production Order',
      productVariants: [
        {
          productVariantSku: 'PROD-001',
          quantity: 10,
          expectedCompleteDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
          boms: [
            {
              materialVariantSku: 'MAT-001',
              plannedQuantity: 5,
              actualQuantity: 0
            }
          ]
        },
        {
          productVariantSku: 'PROD-002',
          quantity: 15,
          expectedCompleteDate: new Date(Date.now() + 10 * 24 * 60 * 60 * 1000).toISOString(),
          boms: [
            {
              materialVariantSku: 'MAT-002',
              plannedQuantity: 8,
              actualQuantity: 0
            }
          ]
        }
      ]
    };
  }
}; 