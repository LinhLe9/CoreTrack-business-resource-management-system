//supplier/page.tsx

'use client';

import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Heading,
  SimpleGrid,
  Spinner,
  Text,
  Center,
  Flex,
  Button,
  useToast,
  IconButton,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';

import SupplierCard from '../../../components/supplier/SupplierCard';
import SearchBar from '../../../components/supplier/SupplierSearchBar';
import SupplierFilters from '../../../components/supplier/SupplierFilters';
import { getSuppliers, getAllSuppliersForAutocomplete } from '../../../services/supplierService';
import { Supplier, SupplierQueryParams, SupplierAutoComplete } from '../../../types/supplier';
import { PageResponse } from '../../../types/PageResponse';
import { AddIcon } from '@chakra-ui/icons';
import { useUser } from '../../../hooks/useUser';

const SupplierCatalogPage: React.FC = () => {
  const [pageData, setPageData] = useState<PageResponse<Supplier> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [queryParams, setQueryParams] = useState<SupplierQueryParams>({});
  const [allSuppliersForAutocomplete, setAllSuppliersForAutocomplete] = useState<SupplierAutoComplete[]>([]);
  const toast = useToast();
  const router = useRouter();
  const { isOwner, isWarehouseStaff } = useUser();

  // Fetch autocomplete list
  useEffect(() => {
    const fetchAllSuppliers = async () => {
      try {
        const data = await getAllSuppliersForAutocomplete();
        setAllSuppliersForAutocomplete(data);
      } catch (err) {
        console.error("Error fetching all suppliers for autocomplete:", err);
      }
    };
    fetchAllSuppliers();
  }, []);

  // Fetch suppliers applied filter/search
  const fetchSuppliers = useCallback(async (params: SupplierQueryParams) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getSuppliers(params);
      setPageData(data);
    } catch (err: any) {
      setError('Failed to fetch supplier. Please try again.');
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
    fetchSuppliers(queryParams);
  }, [fetchSuppliers, queryParams]);

  const handleSearch = (searchTerm: string) => {
    if (searchTerm.length > 255) {
      toast({
        title: 'Search Too Long',
        description: 'Max 255 characters allowed.',
        status: 'warning',
        duration: 5000,
        isClosable: true,
      });
      return;
    }

    setQueryParams((prev) => ({
      ...prev,
      search: searchTerm || undefined,
      page: 0,
    }));
  };

  const handleFilter = (filters: Omit<SupplierQueryParams, 'search' | 'page' | 'size' | 'sort'>) => {
    setQueryParams((prev) => ({
      ...prev,
      country: filters.country,
      page: 0,
    }));
  };

  const handlePageChange = (newPage: number) => {
    setQueryParams((prev) => ({
      ...prev,
      page: newPage,
    }));
  };

  const handleSelectSupplierFromSearch = (supplierId: number) => {
    router.push(`/supplier/${supplierId}`);
  };

  return (
    <Box p={8} maxW="1400px" mx="auto">
      <Heading as="h1" size="xl" textAlign="center" mb={8} color="teal.700">
        Supplier Catalog
      </Heading>

      <Flex direction="column" gap={4} mb={8}>
        {/* Line 1: Search bar and Add button */}
        <Flex justify="center" gap={2} align="center" direction={{ base: 'column', md: 'row' }}>
          <SearchBar
            onSearch={handleSearch}
            onSelectSupplier={handleSelectSupplierFromSearch}
            initialSearchTerm={queryParams.search}
            SuppliersForAutocomplete={allSuppliersForAutocomplete}
          />
          {(isOwner() || isWarehouseStaff()) && (
            <IconButton
              icon={<AddIcon />}
              aria-label="Add new supplier"
              colorScheme="teal"
              onClick={() => router.push('/supplier/add')}
              title="Add Supplier"
            />
          )}
        </Flex>

        {/* Line 2: Filters */}
        <SupplierFilters onFilter={handleFilter} initialFilters={queryParams} />
      </Flex>

      {loading && (
        <Center minH="200px">
          <Spinner size="xl" color="blue.500" />
          <Text ml={4}>Loading suppliers...</Text>
        </Center>
      )}

      {error && !loading && (
        <Center minH="200px">
          <Text color="red.500" fontSize="lg">{error}</Text>
        </Center>
      )}

      {!loading && !error && pageData?.content.length === 0 && (
        <Center minH="200px">
          <Text fontSize="lg" color="gray.600">
            No matching suppliers found.
          </Text>
        </Center>
      )}

      {!loading && !error && pageData && pageData.content.length > 0 && (
        <>
          <SimpleGrid columns={{ base: 1, sm: 2, md: 3, lg: 4 }} spacing={6}>
            {pageData.content
              .filter(supplier => supplier.id != null)
              .map((supplier) => {
                console.log('SupplierCard id:', supplier.id, supplier);
                return <SupplierCard key={supplier.id} supplier={supplier} />;
              })}
          </SimpleGrid>

          <Flex justify="center" align="center" mt={8} gap={4}>
            <Button
              onClick={() => handlePageChange((pageData.number ?? 0) - 1)}
              disabled={pageData.number === 0}
            >
              Previous
            </Button>
            <Text>
              Page {pageData.number + 1} of {pageData.totalPages}
            </Text>
            <Button
              onClick={() => handlePageChange((pageData.number ?? 0) + 1)}
              disabled={pageData.number + 1 >= pageData.totalPages}
            >
              Next
            </Button>
          </Flex>
        </>
      )}
    </Box>
  );
};

export default SupplierCatalogPage;