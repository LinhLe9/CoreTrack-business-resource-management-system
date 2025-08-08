import { useState, useCallback } from 'react';
import { purchasingTicketService } from '@/services/purchasingTicketService';
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
  PurchasingTicketStatusesResponse
} from '@/types/purchasingTicket';

export const usePurchasingTicket = () => {
  // State for purchasing tickets
  const [purchasingTickets, setPurchasingTickets] = useState<PageResponse<PurchasingTicketCardResponse> | null>(null);
  const [currentTicket, setCurrentTicket] = useState<PurchasingTicketResponse | null>(null);
  const [currentDetail, setCurrentDetail] = useState<PurchasingTicketDetailResponse | null>(null);
  const [statusRules, setStatusRules] = useState<StatusTransitionRule[]>([]);
  const [statuses, setStatuses] = useState<PurchasingTicketStatusesResponse | null>(null);
  const [autocompleteResults, setAutocompleteResults] = useState<PurchasingTicketCardResponse[]>([]);

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

  // Get purchasing tickets with filtering
  const getPurchasingTickets = useCallback(async (filter: PurchasingTicketFilter = {}) => {
    setLoading(true);
    setError(null);
    try {
      const result = await purchasingTicketService.getPurchasingTickets(filter);
      setPurchasingTickets(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch purchasing tickets';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get purchasing ticket by ID
  const getPurchasingTicketById = useCallback(async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await purchasingTicketService.getPurchasingTicketById(id);
      setCurrentTicket(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch purchasing ticket';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get purchasing ticket detail
  const getPurchasingTicketDetails = useCallback(async (ticketId: number, detailId: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await purchasingTicketService.getPurchasingTicketDetails(ticketId, detailId);
      setCurrentDetail(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch purchasing ticket detail';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Bulk create purchasing tickets
  const bulkCreatePurchasingTicket = useCallback(async (request: BulkCreatePurchasingTicketRequest) => {
    setBulkCreating(true);
    setError(null);
    try {
      const result = await purchasingTicketService.bulkCreatePurchasingTicket(request);
      // Refresh the list after creation
      await getPurchasingTickets();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to create purchasing tickets';
      setError(errorMessage);
      throw err;
    } finally {
      setBulkCreating(false);
    }
  }, [getPurchasingTickets]);

  // Update detail status
  const updateDetailStatus = useCallback(async (
    ticketId: number, 
    detailId: number, 
    request: UpdatePurchasingDetailStatusRequest
  ) => {
    setUpdatingStatus(true);
    setError(null);
    try {
      const result = await purchasingTicketService.updateDetailStatus(ticketId, detailId, request);
      // Refresh current ticket and detail
      await getPurchasingTicketById(ticketId);
      await getPurchasingTicketDetails(ticketId, detailId);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update status';
      setError(errorMessage);
      throw err;
    } finally {
      setUpdatingStatus(false);
    }
  }, [getPurchasingTicketById, getPurchasingTicketDetails]);

  // Cancel purchasing ticket
  const cancelPurchasingTicket = useCallback(async (ticketId: number, reason: string) => {
    setCancelling(true);
    setError(null);
    try {
      const result = await purchasingTicketService.cancelPurchasingTicket(ticketId, reason);
      // Refresh the list after cancellation
      await getPurchasingTickets();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to cancel purchasing ticket';
      setError(errorMessage);
      throw err;
    } finally {
      setCancelling(false);
    }
  }, [getPurchasingTickets]);

  // Cancel purchasing ticket detail
  const cancelPurchasingTicketDetail = useCallback(async (ticketId: number, detailId: number, reason: string) => {
    setCancelling(true);
    setError(null);
    try {
      const result = await purchasingTicketService.cancelPurchasingTicketDetail(ticketId, detailId, reason);
      // Refresh current ticket and detail
      await getPurchasingTicketById(ticketId);
      await getPurchasingTicketDetails(ticketId, detailId);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to cancel purchasing ticket detail';
      setError(errorMessage);
      throw err;
    } finally {
      setCancelling(false);
    }
  }, [getPurchasingTicketById, getPurchasingTicketDetails]);

  // Delete purchasing ticket from catalog
  const deletePurchasingTicket = useCallback(async (ticketId: number, reason: string) => {
    setCancelling(true);
    setError(null);
    try {
      const result = await purchasingTicketService.deletePurchasingTicket(ticketId, reason);
      // Refresh the list after deletion
      await getPurchasingTickets();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to delete purchasing ticket';
      setError(errorMessage);
      throw err;
    } finally {
      setCancelling(false);
    }
  }, [getPurchasingTickets]);

  // Get status transition rules
  const getStatusTransitionRules = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await purchasingTicketService.getStatusTransitionRules();
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
  const getAllPurchasingTicketStatuses = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await purchasingTicketService.getAllPurchasingTicketStatuses();
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
      const result = await purchasingTicketService.getAutoComplete(search);
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

  // Utility functions
  const formatDate = useCallback((dateString: string) => {
    return purchasingTicketService.formatDate(dateString);
  }, []);

  const getStatusColor = useCallback((status: string) => {
    return purchasingTicketService.getStatusColor(status);
  }, []);

  const getStatusBadgeText = useCallback((status: string) => {
    return purchasingTicketService.getStatusBadgeText(status);
  }, []);

  const validateCreateRequest = useCallback((request: CreatePurchasingTicketRequest) => {
    return purchasingTicketService.validateCreateRequest(request);
  }, []);

  const validateBulkCreateRequest = useCallback((request: BulkCreatePurchasingTicketRequest) => {
    return purchasingTicketService.validateBulkCreateRequest(request);
  }, []);

  return {
    // State
    purchasingTickets,
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
    getPurchasingTickets,
    getPurchasingTicketById,
    getPurchasingTicketDetails,
    bulkCreatePurchasingTicket,
    updateDetailStatus,
    cancelPurchasingTicket,
    cancelPurchasingTicketDetail,
    deletePurchasingTicket,
    getStatusTransitionRules,
    getAllPurchasingTicketStatuses,
    getAutoComplete,
    clearError,

    // Utilities
    formatDate,
    getStatusColor,
    getStatusBadgeText,
    validateCreateRequest,
    validateBulkCreateRequest
  };
}; 