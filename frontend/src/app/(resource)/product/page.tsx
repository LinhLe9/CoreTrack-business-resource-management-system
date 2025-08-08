//product/page.tsx

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

import ProductCard from '../../../components/product/ProductCard';
import SearchBar from '../../../components/product/ProductSearchBar';
import ProductFilters from '../../../components/product/ProductFilters';
import { getProducts, getAllProductsForAutocomplete } from '../../../services/productService';
import { Product, ProductQueryParams, ProductAutoComplete } from '../../../types/product';
import { PageResponse } from '../../../types/PageResponse';
import { AddIcon } from '@chakra-ui/icons';
import { useUser } from '../../../hooks/useUser';

const ProductCatalogPage: React.FC = () => {
  const [pageData, setPageData] = useState<PageResponse<Product> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [queryParams, setQueryParams] = useState<ProductQueryParams>({});
  const [allProductsForAutocomplete, setAllProductsForAutocomplete] = useState<ProductAutoComplete[]>([]);
  const toast = useToast();
  const router = useRouter();
  const { isOwner } = useUser();

  // Fetch autocomplete list
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

  // Fetch products applied filter/search
  const fetchProducts = useCallback(async (params: ProductQueryParams) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getProducts(params);
      setPageData(data);
    } catch (err: any) {
      setError('Failed to fetch products. Please try again.');
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
    fetchProducts(queryParams);
  }, [fetchProducts, queryParams]);

  const handleSearch = (searchTerm: string) => {
    console.log('Product page handleSearch called with:', searchTerm); // Debug log
    
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

    console.log('Setting query params with search:', searchTerm); // Debug log
    setQueryParams((prev) => ({
      ...prev,
      search: searchTerm,
      page: 0,
    }));
  };

  const handleFilter = (filters: Omit<ProductQueryParams, 'search' | 'page' | 'size' | 'sort'>) => {
    console.log('Applying filters:', filters); // Debug log
    setQueryParams((prev) => ({
      ...prev,
      groupProducts: filters.groupProducts,
      status: filters.status,
      page: 0,
    }));
  };

  const handlePageChange = (newPage: number) => {
    setQueryParams((prev) => ({
      ...prev,
      page: newPage,
    }));
  };

  const handleSelectProductFromSearch = (productId: number) => {
    router.push(`/product/${productId}`);
  };

  const handleDelete = () => {
    // Refresh the product list after deletion
    fetchProducts(queryParams);
  };

  return (
    <Box p={8} maxW="1400px" mx="auto">
      <Heading as="h1" size="xl" textAlign="center" mb={8} color="teal.700">
        Product Catalog
      </Heading>

      <Flex direction="column" gap={4} mb={8}>
        {/* Line 1: Search bar and Add button */}
        <Flex justify="center" gap={2} align="center">
          <SearchBar
            onSearch={handleSearch}
            onSelectProduct={handleSelectProductFromSearch}
            initialSearchTerm={queryParams.search}
            productsForAutocomplete={allProductsForAutocomplete}
          />
          {isOwner() && (
            <IconButton
              icon={<AddIcon />}
              aria-label="Add new product"
              colorScheme="teal"
              onClick={() => router.push('/product/add')}
              title="Add Product"
            />
          )}
        </Flex>

        {/* Line 2: Filters */}
        <ProductFilters onFilter={handleFilter} initialFilters={queryParams} />
      </Flex>

      {loading && (
        <Center minH="200px">
          <Spinner size="xl" color="blue.500" />
          <Text ml={4}>Loading products...</Text>
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
            No matching products found.
          </Text>
        </Center>
      )}

      {!loading && !error && pageData && pageData.content.length > 0 && (
        <>
          <SimpleGrid columns={{ base: 1, sm: 2, md: 3, lg: 4 }} spacing={6}>
            {pageData.content
              .filter(product => product.id != null)
              .map((product) => {
                console.log('ProductCard id:', product.id, product);
                return <ProductCard key={product.id} product={product} onDelete={handleDelete} showDeleteButton={isOwner()} />;
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

export default ProductCatalogPage;