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
  AlertTitle,
  AlertDescription,
  Button,
  useToast,
  Center,
  useDisclosure,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { AddIcon } from '@chakra-ui/icons';

import SaleSearchBar from '../../../components/sale/SaleSearchBar';
import SaleFilters from '../../../components/sale/SaleFilters';
import SaleCard from '../../../components/sale/SaleCard';
import { getSaleTickets, getSaleAutoComplete } from '../../../services/saleService';
import { SaleCardResponse, SaleQueryParams, SaleResponse } from '../../../types/sale';

const SalePage: React.FC = () => {
  const [pageData, setPageData] = useState<SaleResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [queryParams, setQueryParams] = useState<SaleQueryParams>({});
  const [saleForAutocomplete, setSaleForAutocomplete] = useState<SaleCardResponse[]>([]);
  const [searchLoading, setSearchLoading] = useState(false);
  
  const toast = useToast();
  const router = useRouter();

  // Fetch autocomplete list only when needed
  const fetchSaleAutocomplete = useCallback(async (search?: string) => {
    if (searchLoading) return;
    
    setSearchLoading(true);
    try {
      const data = await getSaleAutoComplete(search);
      setSaleForAutocomplete(data);
    } catch (err) {
      console.error("Error fetching sale autocomplete:", err);
    } finally {
      setSearchLoading(false);
    }
  }, [searchLoading]);

  const handleSearchInputChange = useCallback((inputValue: string) => {
    if (inputValue.length >= 2) {
      fetchSaleAutocomplete(inputValue);
    }
  }, [fetchSaleAutocomplete]);

  // Fetch sale tickets applied filter/search
  const fetchSaleTickets = useCallback(async (params: SaleQueryParams) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getSaleTickets(params);
      setPageData(data);
    } catch (err: any) {
      setError('Failed to fetch sale tickets. Please try again.');
      if (err.response?.status === 400) {
        toast({
          title: 'Invalid Input',
          description: err.response.data.message || 'Check your search/filter.',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => {
    fetchSaleTickets(queryParams);
  }, [fetchSaleTickets, queryParams]);

  const handleSearch = (searchTerm: string) => {
    if (searchTerm.length > 255) {
      toast({
        title: 'Search Too Long',
        description: 'Search term cannot exceed 255 characters.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }
    setQueryParams(prev => ({ ...prev, search: searchTerm, page: 0 }));
  };

  const handleFilter = (filters: { ticketStatus?: string[] }) => {
    setQueryParams(prev => ({ ...prev, ...filters, page: 0 }));
  };

  const handlePageChange = (newPage: number) => {
    setQueryParams(prev => ({ ...prev, page: newPage }));
  };

  const handleSelectSaleFromSearch = (saleId: number) => {
    router.push(`/sale/${saleId}`);
  };

  const handleCreateSale = () => {
    router.push('/sale/create');
  };

  const handleViewSale = (saleId: number) => {
    router.push(`/sale/${saleId}`);
  };

  const handleEditSale = (saleId: number) => {
    router.push(`/sale/${saleId}/edit`);
  };

  return (
    <Box maxW="1200px" mx="auto" p={6}>
      <VStack spacing={6} align="center" mb={6}>
        <Heading as="h1" size="xl" textAlign="center">
          Sale Management
        </Heading>
        <HStack spacing={4} align="center">
          <SaleSearchBar
            onSearch={handleSearch}
            onSelectSale={handleSelectSaleFromSearch}
            saleForAutocomplete={saleForAutocomplete}
            onSearchInputChange={handleSearchInputChange}
          />
          <IconButton
            aria-label="Create new sale"
            icon={<AddIcon />}
            colorScheme="blue"
            size="lg"
            onClick={handleCreateSale}
          />
        </HStack>
      </VStack>

              <VStack spacing={6} align="stretch">
          {/* Filter Section */}
          <Box>
            <SaleFilters
              onFilter={handleFilter}
              initialFilters={queryParams}
            />
          </Box>

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
            <Button size="sm" ml={2} onClick={() => setError(null)}>
              Dismiss
            </Button>
          </Alert>
        )}

        {!loading && !error && pageData && (
          <>
            <Text fontSize="sm" color="gray.600" textAlign="center">
              Showing {pageData.content.length} of {pageData.totalElements} results
            </Text>

            <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} spacing={6}>
              {pageData.content.map((sale) => (
                <SaleCard
                  key={sale.id}
                  sale={sale}
                  onView={handleViewSale}
                  onEdit={handleEditSale}
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
                No sale tickets found
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

export default SalePage;
