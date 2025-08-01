import { useState, useCallback } from 'react';
import { productionTicketService, productionTicketUtils } from '@/services/productionTicketService';
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
  ProductionTicketStatusesResponse
} from '@/types/productionTicket';

export const useProductionTicket = () => {
  // State for production tickets
  const [productionTickets, setProductionTickets] = useState<PageResponse<ProductionTicketCardResponse> | null>(null);
  const [currentTicket, setCurrentTicket] = useState<ProductionTicketResponse | null>(null);
  const [currentDetail, setCurrentDetail] = useState<ProductionTicketDetailResponse | null>(null);
  const [statusRules, setStatusRules] = useState<StatusTransitionRule[]>([]);
  const [statuses, setStatuses] = useState<ProductionTicketStatusesResponse | null>(null);
  const [autocompleteResults, setAutocompleteResults] = useState<ProductionTicketCardResponse[]>([]);

  // Loading states
  const [loading, setLoading] = useState(false);
  const [creating, setCreating] = useState(false);
  const [bulkCreating, setBulkCreating] = useState(false);
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const [cancelling, setCancelling] = useState(false);

  // Error states
  const [error, setError] = useState<string | null>(null);

  // Clear error
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  // Get production tickets with filtering
  const getProductionTickets = useCallback(async (filter: ProductionTicketFilter = {}) => {
    setLoading(true);
    setError(null);
    try {
      const result = await productionTicketService.getProductionTickets(filter);
      setProductionTickets(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch production tickets';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get production ticket by ID
  const getProductionTicketById = useCallback(async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await productionTicketService.getProductionTicketById(id);
      setCurrentTicket(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch production ticket';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get production ticket detail
  const getProductionTicketDetails = useCallback(async (ticketId: number, detailId: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await productionTicketService.getProductionTicketDetails(ticketId, detailId);
      setCurrentDetail(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch production ticket detail';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Create production ticket
  const createProductionTicket = useCallback(async (request: CreateProductionTicketRequest) => {
    setCreating(true);
    setError(null);
    try {
      const result = await productionTicketService.createProductionTicket(request);
      // Refresh the list after creation
      await getProductionTickets();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to create production ticket';
      setError(errorMessage);
      throw err;
    } finally {
      setCreating(false);
    }
  }, [getProductionTickets]);

  // Bulk create production tickets
  const bulkCreateProductionTicket = useCallback(async (request: BulkCreateProductionTicketRequest) => {
    setBulkCreating(true);
    setError(null);
    try {
      const result = await productionTicketService.bulkCreateProductionTicket(request);
      // Refresh the list after creation
      await getProductionTickets();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to create production tickets';
      setError(errorMessage);
      throw err;
    } finally {
      setBulkCreating(false);
    }
  }, [getProductionTickets]);

  // Update detail status
  const updateDetailStatus = useCallback(async (
    ticketId: number, 
    detailId: number, 
    request: UpdateDetailStatusRequest
  ) => {
    setUpdatingStatus(true);
    setError(null);
    try {
      const result = await productionTicketService.updateDetailStatus(ticketId, detailId, request);
      // Refresh current ticket and detail
      await getProductionTicketById(ticketId);
      await getProductionTicketDetails(ticketId, detailId);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update status';
      setError(errorMessage);
      throw err;
    } finally {
      setUpdatingStatus(false);
    }
  }, [getProductionTicketById, getProductionTicketDetails]);

  // Cancel production ticket
  const cancelProductionTicket = useCallback(async (ticketId: number, reason: string) => {
    setCancelling(true);
    setError(null);
    try {
      const result = await productionTicketService.cancelProductionTicket(ticketId, reason);
      // Refresh the list after cancellation
      await getProductionTickets();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to cancel production ticket';
      setError(errorMessage);
      throw err;
    } finally {
      setCancelling(false);
    }
  }, [getProductionTickets]);

  // Cancel production ticket detail
  const cancelProductionTicketDetail = useCallback(async (ticketId: number, detailId: number, reason: string) => {
    setCancelling(true);
    setError(null);
    try {
      const result = await productionTicketService.cancelProductionTicketDetail(ticketId, detailId, reason);
      // Refresh current ticket and detail
      await getProductionTicketById(ticketId);
      await getProductionTicketDetails(ticketId, detailId);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to cancel production ticket detail';
      setError(errorMessage);
      throw err;
    } finally {
      setCancelling(false);
    }
  }, [getProductionTicketById, getProductionTicketDetails]);

  // Get status transition rules
  const getStatusTransitionRules = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await productionTicketService.getStatusTransitionRules();
      setStatusRules(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch status rules';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get all statuses
  const getAllProductionTicketStatuses = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await productionTicketService.getAllProductionTicketStatuses();
      setStatuses(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch statuses';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get autocomplete suggestions
  const getAutoComplete = useCallback(async (search: string) => {
    if (!search || search.trim() === '') {
      setAutocompleteResults([]);
      return [];
    }
    
    setLoading(true);
    setError(null);
    try {
      const result = await productionTicketService.getAutoComplete(search);
      setAutocompleteResults(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch autocomplete';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Test functions for development
  const testCascadeRelationships = useCallback(async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await productionTicketService.testCascadeRelationships(id);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to test cascade relationships';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Utility functions
  const formatDate = useCallback((dateString: string) => {
    return productionTicketUtils.formatDate(dateString);
  }, []);

  const getStatusColor = useCallback((status: string) => {
    return productionTicketUtils.getStatusColor(status);
  }, []);

  const getStatusBadgeText = useCallback((status: string) => {
    return productionTicketUtils.getStatusBadgeText(status);
  }, []);

  const validateCreateRequest = useCallback((request: CreateProductionTicketRequest) => {
    return productionTicketUtils.validateCreateRequest(request);
  }, []);

  const validateBulkCreateRequest = useCallback((request: BulkCreateProductionTicketRequest) => {
    return productionTicketUtils.validateBulkCreateRequest(request);
  }, []);

  return {
    // State
    productionTickets,
    currentTicket,
    currentDetail,
    statusRules,
    statuses,
    autocompleteResults,
    loading,
    creating,
    bulkCreating,
    updatingStatus,
    cancelling,
    error,

    // Actions
    getProductionTickets,
    getProductionTicketById,
    getProductionTicketDetails,
    createProductionTicket,
    bulkCreateProductionTicket,
    updateDetailStatus,
    cancelProductionTicket,
    cancelProductionTicketDetail,
    getStatusTransitionRules,
    getAllProductionTicketStatuses,
    getAutoComplete,
    testCascadeRelationships,
    clearError,

    // Utilities
    formatDate,
    getStatusColor,
    getStatusBadgeText,
    validateCreateRequest,
    validateBulkCreateRequest
  };
}; 