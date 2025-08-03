'use client';

import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Heading,
  VStack,
  HStack,
  SimpleGrid,
  Spinner,
  Text,
  Flex,
  IconButton,
  Alert,
  AlertIcon,
  Button,
  useToast,
  Center,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { AddIcon } from '@chakra-ui/icons';

import { usePurchasingTicket } from '../../../hooks/usePurchasingTicket';
import { PurchasingTicketFilter, PurchasingTicketCardResponse } from '../../../types/purchasingTicket';
import { PageResponse } from '../../../types/PageResponse';
import PurchasingTicketSearchBar from '../../../components/ticket/PurchasingTicketSearchBar';
import PurchasingTicketFilters from '../../../components/ticket/PurchasingTicketFilters';
import PurchasingTicketCard from '../../../components/ticket/PurchasingTicketCard';

const PurchasingTicketPage: React.FC = () => {
  const [pageData, setPageData] = useState<PageResponse<PurchasingTicketCardResponse> | null>(null);
  const [queryParams, setQueryParams] = useState<PurchasingTicketFilter>({});
  const [autocompleteResults, setAutocompleteResults] = useState<PurchasingTicketCardResponse[]>([]);
  
  const {
    loading,
    error,
    getPurchasingTickets,
    getAutoComplete,
    clearError
  } = usePurchasingTicket();
  
  const toast = useToast();
  const router = useRouter();

  // Fetch autocomplete results
  const fetchAutocomplete = useCallback(async (search: string) => {
    if (!search || search.trim() === '') {
      setAutocompleteResults([]);
      return;
    }
    
    try {
      const results = await getAutoComplete(search);
      setAutocompleteResults(results);
    } catch (err) {
      console.error("Error fetching autocomplete:", err);
    }
  }, [getAutoComplete]);

  // Fetch purchasing tickets with filter/search
  const fetchPurchasingTickets = useCallback(async (params: PurchasingTicketFilter) => {
    try {
      const data = await getPurchasingTickets(params);
      setPageData(data);
    } catch (err: any) {
      if (err.response?.status === 400) {
        toast({
          title: 'Invalid Input',
          description: err.response.data.message || 'Check your search/filter.',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    }
  }, [getPurchasingTickets, toast]);

  useEffect(() => {
    fetchPurchasingTickets(queryParams);
  }, [fetchPurchasingTickets, queryParams]);

  const handleSearch = (searchTerm: string) => {
    if (searchTerm.length > 255) {
      toast({
        title: 'Search Too Long',
        description: 'Search term must be 255 characters or less.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
      return;
    }
    
    setQueryParams(prev => ({
      ...prev,
      search: searchTerm || undefined,
      page: 0 // Reset to first page when searching
    }));
  };

  const handleFilter = (filters: any) => {
    setQueryParams(prev => ({
      ...prev,
      ...filters,
      page: 0 // Reset to first page when filtering
    }));
  };

  const handlePageChange = (newPage: number) => {
    setQueryParams(prev => ({
      ...prev,
      page: newPage
    }));
  };

  const handleCreateTicket = () => {
    router.push('/purchasing/create');
  };

  const handleViewDetails = (id: number) => {
    router.push(`/purchasing/${id}`);
  };

  const handleSelectFromAutocomplete = (ticketId: number) => {
    // Navigate to the selected ticket
    router.push(`/purchasing/${ticketId}`);
  };

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        {/* Header - Centered */}
        <Center>
          <VStack spacing={4} align="center">
            <Heading size="lg">Purchasing Tickets</Heading>
            
            {/* Search Bar and Create Button - Side by side */}
            <HStack spacing={4} align="center" w="full" maxW="800px">
              <Box flex={1}>
                <PurchasingTicketSearchBar
                  onSearch={handleSearch}
                  onSelectFromAutocomplete={handleSelectFromAutocomplete}
                  autocompleteResults={autocompleteResults}
                  onSearchInputChange={fetchAutocomplete}
                />
              </Box>
              
              <Button
                leftIcon={<AddIcon />}
                colorScheme="teal"
                onClick={handleCreateTicket}
                size="md"
                flexShrink={0}
              >
                Create Ticket
              </Button>
            </HStack>
          </VStack>
        </Center>

        {/* Filters */}
        <PurchasingTicketFilters
          onFilter={handleFilter}
          initialFilters={queryParams}
        />

        {/* Results */}
        {loading && (
          <Flex justify="center" p={8}>
            <Spinner size="xl" />
          </Flex>
        )}

        {error && (
          <Alert status="error">
            <AlertIcon />
            {error}
            <Button size="sm" ml={2} onClick={clearError}>
              Dismiss
            </Button>
          </Alert>
        )}

        {!loading && !error && pageData && (
          <>
            <Text fontSize="sm" color="gray.600" textAlign="center">
              Showing {pageData.content.length} of {pageData.totalElements} results
            </Text>

            <SimpleGrid columns={{ base: 1, lg: 2 }} spacing={6}>
              {pageData.content.map((purchasingTicket) => (
                <PurchasingTicketCard 
                  key={purchasingTicket.id} 
                  purchasingTicket={purchasingTicket}
                  onViewDetails={handleViewDetails}
                />
              ))}
            </SimpleGrid>

            {/* Pagination */}
            {pageData.totalPages > 1 && (
              <Flex justify="center" mt={6} gap={4}>
                <Button
                  onClick={() => handlePageChange((pageData.number ?? 0) - 1)}
                  disabled={pageData.number === 0}
                >
                  Previous
                </Button>
                <Text alignSelf="center">
                  Page {pageData.number + 1} of {pageData.totalPages}
                </Text>
                <Button
                  onClick={() => handlePageChange((pageData.number ?? 0) + 1)}
                  disabled={pageData.number + 1 >= pageData.totalPages}
                >
                  Next
                </Button>
              </Flex>
            )}
          </>
        )}

        {!loading && !error && pageData && pageData.content.length === 0 && (
          <Center p={8}>
            <VStack spacing={4}>
              <Text fontSize="lg" color="gray.500">
                No purchasing tickets found
              </Text>
              <Text fontSize="sm" color="gray.400">
                Try adjusting your search or filter criteria
              </Text>
            </VStack>
          </Center>
        )}
      </VStack>
    </Box>
  );
};

export default PurchasingTicketPage;
