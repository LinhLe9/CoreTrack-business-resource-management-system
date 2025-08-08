'use client';

import React, { useState, useEffect } from 'react';
import {
  Box,
  Heading,
  VStack,
  HStack,
  Button,
  Text,
  useToast,
  Card,
  CardBody,
  Badge,
  IconButton,
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
  FormControl,
  FormLabel,
  Divider,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { CloseIcon } from '@chakra-ui/icons';

import ProductVariantSearchBar from '../../../../components/product/ProductVariantSearchBar';
import { getAllProductVariantsForAutocomplete } from '../../../../services/productService';
import { bulkInitInventory, createBulkInitInventoryRequest } from '../../../../services/productInventoryService';
import { ProductVariantAutoComplete } from '../../../../types/product';
import ProtectedRoute from '../../../../components/auth/ProtectedRoute';

interface SelectedProduct {
  variantId: number;
  productName: string;
  productSku: string;
  variantSku: string;
  variantName: string;
  productGroup?: string;
}

const InitialStockPage: React.FC = () => {
  const [allProductVariantsForAutocomplete, setAllProductVariantsForAutocomplete] = useState<ProductVariantAutoComplete[]>([]);
  const [selectedProducts, setSelectedProducts] = useState<SelectedProduct[]>([]);
  const [initialStock, setInitialStock] = useState<number>(0);
  const [minAlertStock, setMinAlertStock] = useState<number>(0);
  const [maxStockLevel, setMaxStockLevel] = useState<number>(100);
  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const router = useRouter();

  // Fetch autocomplete list
  useEffect(() => {
    const fetchAllProductVariants = async () => {
      try {
        const data = await getAllProductVariantsForAutocomplete();
        setAllProductVariantsForAutocomplete(data);
      } catch (err) {
        console.error("Error fetching all product variants for autocomplete:", err);
        toast({
          title: 'Error',
          description: 'Failed to load product variants for search.',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    };
    fetchAllProductVariants();
  }, [toast]);

  const handleSelectProductVariant = (variantId: number) => {
    const variant = allProductVariantsForAutocomplete.find(v => v.variantId === variantId);
    if (!variant) return;

    // Check if variant is already selected
    const isAlreadySelected = selectedProducts.some(p => p.variantId === variantId);
    if (isAlreadySelected) {
      toast({
        title: 'Product Variant Already Selected',
        description: 'This product variant is already in your selection.',
        status: 'warning',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    // Add variant to selection
    const selectedProduct: SelectedProduct = {
      variantId: variant.variantId,
      productName: variant.productName,
      productSku: variant.productSku,
      variantSku: variant.variantSku,
      variantName: variant.variantName,
      productGroup: variant.productGroup,
    };

    setSelectedProducts(prev => [...prev, selectedProduct]);
  };

  const handleRemoveProduct = (variantId: number) => {
    setSelectedProducts(prev => prev.filter(p => p.variantId !== variantId));
  };

  const handleSubmit = async () => {
    if (selectedProducts.length === 0) {
      toast({
        title: 'No Products Selected',
        description: 'Please select at least one product variant.',
        status: 'error', duration: 5000, isClosable: true,
      });
      return;
    }

    if (initialStock < 0 || minAlertStock < 0 || maxStockLevel < 0) {
      toast({
        title: 'Invalid Values',
        description: 'Stock values cannot be negative.',
        status: 'error', duration: 5000, isClosable: true,
      });
      return;
    }

    if (maxStockLevel <= minAlertStock) {
      toast({
        title: 'Invalid Stock Levels',
        description: 'Max stock level must be greater than min alert stock.',
        status: 'error', duration: 5000, isClosable: true,
      });
      return;
    }

    if (initialStock > maxStockLevel) {
      toast({
        title: 'Invalid Initial Stock',
        description: 'Initial stock cannot be greater than max stock level.',
        status: 'error', duration: 5000, isClosable: true,
      });
      return;
    }

    setLoading(true);
    try {
      const productVariantSkus = selectedProducts.map(p => p.variantSku);
      const request = createBulkInitInventoryRequest(
        productVariantSkus,
        initialStock,
        minAlertStock,
        maxStockLevel,
      );
      
      // Debug log
      console.log('=== BULK INIT REQUEST DEBUG ===');
      console.log('Selected Products:', selectedProducts);
      console.log('Request Data:', request);
      console.log('Product Variant SKUs:', productVariantSkus);
      console.log('Initial Stock:', initialStock);
      console.log('Min Alert Stock:', minAlertStock);
      console.log('Max Stock Level:', maxStockLevel);
      console.log('==============================');
      
      const response = await bulkInitInventory(request);
      
      console.log('=== BULK INIT RESPONSE DEBUG ===');
      console.log('Response:', response);
      console.log('===============================');
      
      toast({
        title: 'Success',
        description: `Successfully initialized ${response.successfulInits.length} products. ${response.failedInits.length} failed.`,
        status: 'success', duration: 5000, isClosable: true,
      });
      router.push('/product-inventory');
    } catch (error: any) {
      console.error('=== BULK INIT ERROR DEBUG ===');
      console.error('Error:', error);
      console.error('Error Response:', error.response);
      console.error('Error Data:', error.response?.data);
      console.error('============================');
      
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to initialize product stock.',
        status: 'error', duration: 5000, isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    router.push('/product-inventory');
  };

  return (
    <ProtectedRoute requiredRoles={['OWNER', 'WAREHOUSE_STAFF']}>
      <Box p={8} maxW="800px" mx="auto">
        <Heading as="h1" size="xl" textAlign="center" mb={8} color="teal.700">
          Initialize Product Stock
        </Heading>

        <VStack spacing={6} align="stretch">
          {/* Search Bar */}
          <Box>
            <Text fontSize="lg" fontWeight="bold" mb={3}>
              Search and Select Products
            </Text>
            <ProductVariantSearchBar
              onSearch={() => {}} // Not used for search, only for selection
              onSelectProductVariant={handleSelectProductVariant}
              productVariantsForAutocomplete={allProductVariantsForAutocomplete}
            />
          </Box>

          {/* Selected Products */}
          {selectedProducts.length > 0 && (
            <Box>
              <Text fontSize="lg" fontWeight="bold" mb={3}>
                Selected Products ({selectedProducts.length})
              </Text>
              <VStack spacing={2} align="stretch">
                {selectedProducts.map((product) => (
                  <Card key={product.variantId} size="sm">
                    <CardBody>
                      <HStack justify="space-between">
                        <VStack align="start" spacing={1}>
                          <Text fontWeight="bold">{product.productName}</Text>
                          <Text fontSize="sm" color="gray.600">
                            Product SKU: {product.productSku}
                          </Text>
                          <Text fontSize="sm" color="gray.600">
                            Variant SKU: {product.variantSku}
                          </Text>
                          <Text fontSize="sm" color="gray.600">
                            Variant: {product.variantName}
                          </Text>
                          {product.productGroup && (
                            <Text fontSize="sm" color="gray.600">
                              Group: {product.productGroup}
                            </Text>
                          )}
                        </VStack>
                        <IconButton
                          icon={<CloseIcon />}
                          aria-label="Remove product variant"
                          size="sm"
                          colorScheme="red"
                          variant="ghost"
                          onClick={() => handleRemoveProduct(product.variantId)}
                        />
                      </HStack>
                    </CardBody>
                  </Card>
                ))}
              </VStack>
            </Box>
          )}

          <Divider />

          {/* Stock Level Configuration */}
          <Box>
            <Text fontSize="lg" fontWeight="bold" mb={3}>
              Stock Level Configuration
            </Text>
            <VStack spacing={4} align="stretch">
              <FormControl>
                <FormLabel>Initial Stock</FormLabel>
                <NumberInput
                  value={initialStock}
                  onChange={(_, value) => setInitialStock(value)}
                  min={0}
                  precision={2}
                >
                  <NumberInputField />
                  <NumberInputStepper>
                    <NumberIncrementStepper />
                    <NumberDecrementStepper />
                  </NumberInputStepper>
                </NumberInput>
              </FormControl>

              <FormControl>
                <FormLabel>Min Alert Stock</FormLabel>
                <NumberInput
                  value={minAlertStock}
                  onChange={(_, value) => setMinAlertStock(value)}
                  min={0}
                  precision={2}
                >
                  <NumberInputField />
                  <NumberInputStepper>
                    <NumberIncrementStepper />
                    <NumberDecrementStepper />
                  </NumberInputStepper>
                </NumberInput>
              </FormControl>

              <FormControl>
                <FormLabel>Max Stock Level</FormLabel>
                <NumberInput
                  value={maxStockLevel}
                  onChange={(_, value) => setMaxStockLevel(value)}
                  min={0}
                  precision={2}
                >
                  <NumberInputField />
                  <NumberInputStepper>
                    <NumberIncrementStepper />
                    <NumberDecrementStepper />
                  </NumberInputStepper>
                </NumberInput>
              </FormControl>
            </VStack>
          </Box>

          {/* Action Buttons */}
          <HStack spacing={4} justify="center">
            <Button
              colorScheme="gray"
              onClick={handleCancel}
              isDisabled={loading}
            >
              Cancel
            </Button>
            <Button
              colorScheme="teal"
              onClick={handleSubmit}
              isLoading={loading}
              loadingText="Initializing..."
              isDisabled={selectedProducts.length === 0}
            >
              Initialize Stock
            </Button>
          </HStack>
        </VStack>
      </Box>
    </ProtectedRoute>
  );
};

export default InitialStockPage; 