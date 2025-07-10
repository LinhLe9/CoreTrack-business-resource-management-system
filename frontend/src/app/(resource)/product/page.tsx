// pages/materials/index.tsx
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
} from '@chakra-ui/react';
import { useRouter } from 'next/router';

import ProductCard from '../../../components/ProductCard';
import SearchBar from '../../../components/SearchBar';
import FilterPanel from '../../../components/FilterPanel';
import { getProducts,getAllProductsForAutocomplete } from '../../../services/productService'; 
import { Product, ProductQueryParams } from '../../../types/product';
import { PageResponse } from '../../../types/PageResponse';

const ProductCatalogPage: React.FC = () => {
  const [pageData, setPageData] = useState<PageResponse<Product> | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [queryParams, setQueryParams] = useState<ProductQueryParams>({});
  const [allProductsForAutocomplete, setAllProductsForAutocomplete] = useState<Product[]>([]);
  const toast = useToast();
  const router = useRouter();

  // Fetch all product for autocomplete
  useEffect(() => {
    const fetchAllProducts = async () => {
      try {
        const data = await getAllProductsForAutocomplete();
        setAllProductsForAutocomplete(data);
      } catch (err) {
        console.error("Error fetching all products for autocomplete:", err);
      }
    };
    fetchAllProducts();
  }, []);

  // Fetch page data theo filter/search/page
  const fetchProducts = useCallback(async (params: ProductQueryParams) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getProducts(params);
      setPageData(data);
    } catch (err: any) {
      setError('Failed to fetch products. Please try again.');
      if (err.response && err.response.status === 400) {
        toast({
          title: 'Invalid Input',
          description: err.response.data.message || "Please check your search/filter criteria.",
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => {
    fetchProducts(queryParams);
  }, [fetchProducts, queryParams]);

  const handleSearch = (searchTerm: string) => {
    if (searchTerm.length > 255) {
      toast({
        title: 'Invalid Search Input',
        description: 'Search keyword is too long. Max 255 characters allowed.',
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

  const handleFilter = (filters: Omit<ProductQueryParams, 'search' | 'page' | 'size' | 'sort'>) => {
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

  const handleSelectProductFromSearch = (ProductId: number) => {
    router.push(`/product/${ProductId}`);
  };

  return (
    <Box p={8} maxWidth="1400px" mx="auto">
      <Heading as="h1" size="xl" textAlign="center" mb={8} color="teal.700">
        Material Catalog
      </Heading>

      <Flex direction={{ base: 'column', md: 'row' }} justify="space-between" align="flex-start" gap={6} mb={8}>
        <Box flex={1} minWidth="300px">
          <SearchBar
            onSearch={handleSearch}
            onSelectProduct={handleSelectProductFromSearch}
            initialSearchTerm={queryParams.search}
            productsForAutocomplete={allProductsForAutocomplete}
          />
        </Box>
        <Box>
          <FilterPanel onFilter={handleFilter} initialFilters={queryParams} />
        </Box>
      </Flex>

      {loading && (
        <Center minHeight="200px">
          <Spinner size="xl" color="blue.500" />
          <Text ml={4}>Loading materials...</Text>
        </Center>
      )}

      {error && !loading && (
        <Center minHeight="200px">
          <Text color="red.500" fontSize="lg">
            {error}
          </Text>
        </Center>
      )}

      {!loading && !error && pageData?.content.length === 0 && (
        <Center minHeight="200px">
          <Text fontSize="lg" color="gray.600">
            No matching materials found.
          </Text>
        </Center>
      )}

      {!loading && !error && pageData && pageData.content.length > 0 && (
        <>
          <SimpleGrid columns={{ base: 1, sm: 2, md: 3, lg: 4 }} spacing={6}>
            {pageData.content.map((product) => (
              <ProductCard key={product.id} product={product} />
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

export default ProductCatalogPage;