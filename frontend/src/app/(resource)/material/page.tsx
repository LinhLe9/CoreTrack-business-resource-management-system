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

import MaterialCard from '../../../components/material/MaterialCard';
import SearchBar from '../../../components/material/MaterialSearchBar';
import MaterialFilters from '../../../components/material/MaterialFilters';
import { getMaterials, getAllMaterialsForAutocomplete } from '../../../services/materialService';
import { Material, MaterialQueryParams, MaterialAutoComplete } from '../../../types/material';
import { PageResponse } from '../../../types/PageResponse';
import { AddIcon } from '@chakra-ui/icons';

const MaterialCatalogPage: React.FC = () => {
  const [pageData, setPageData] = useState<PageResponse<Material> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [queryParams, setQueryParams] = useState<MaterialQueryParams>({});
  const [allMaterialsForAutocomplete, setAllMaterialsForAutocomplete] = useState<MaterialAutoComplete[]>([]);
  const toast = useToast();
  const router = useRouter();

  // Fetch autocomplete list
  useEffect(() => {
    const fetchAllMaterials = async () => {
      try {
        const data = await getAllMaterialsForAutocomplete();
        setAllMaterialsForAutocomplete(data);
      } catch (err) {
        console.error("Error fetching all materials for autocomplete:", err);
      }
    };
    fetchAllMaterials();
  }, []);

  // Fetch materials applied filter/search
  const fetchMaterials = useCallback(async (params: MaterialQueryParams) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getMaterials(params);
      setPageData(data);
    } catch (err: any) {
      setError('Failed to fetch materials. Please try again.');
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
    fetchMaterials(queryParams);
  }, [fetchMaterials, queryParams]);

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

  const handleFilter = (filters: Omit<MaterialQueryParams, 'search' | 'page' | 'size' | 'sort'>) => {
    setQueryParams((prev) => ({
      ...prev,
      ...filters,
      page: 0,
    }));
  };

  const handlePageChange = (newPage: number) => {
    setQueryParams((prev) => ({
      ...prev,
      page: newPage,
    }));
  };

  const handleSelectMaterialFromSearch = (materialId: number) => {
    router.push(`/material/${materialId}`);
  };

  return (
    <Box p={8} maxW="1400px" mx="auto">
      <Heading as="h1" size="xl" textAlign="center" mb={8} color="teal.700">
        Product Catalog
      </Heading>

      <Flex direction={{ base: 'column', md: 'row' }} gap={6} mb={8} align="flex-start">
        <Box flex="1" minW="300px">
          <Flex gap={2} align="center">
            <SearchBar
              onSearch={handleSearch}
              onSelectMaterial={handleSelectMaterialFromSearch}
              initialSearchTerm={queryParams.search}
              materialsForAutocomplete={allMaterialsForAutocomplete}
            />
            <IconButton
              icon={<AddIcon />}
              aria-label="Add new material"
              colorScheme="teal"
              onClick={() => router.push('/material/add')}
              title="Add Material"
            />
          </Flex>
        </Box>

        <Box>
          <MaterialFilters onFilter={handleFilter} initialFilters={queryParams} />
        </Box>
      </Flex>

      {loading && (
        <Center minH="200px">
          <Spinner size="xl" color="blue.500" />
          <Text ml={4}>Loading materials...</Text>
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
            No matching materials found.
          </Text>
        </Center>
      )}

      {!loading && !error && pageData && pageData.content.length > 0 && (
        <>
          <SimpleGrid columns={{ base: 1, sm: 2, md: 3, lg: 4 }} spacing={6}>
            {pageData.content.map((material) => (
              <MaterialCard key={material.id} material={material} />
            ))}
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

export default MaterialCatalogPage;