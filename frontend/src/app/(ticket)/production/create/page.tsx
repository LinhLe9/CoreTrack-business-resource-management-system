'use client';

import React, { useState, useCallback } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Input,
  Button,
  FormControl,
  FormLabel,
  FormErrorMessage,
  useToast,
  Alert,
  AlertIcon,
  Spinner,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableContainer,
  Badge,
  IconButton,
  useDisclosure,
  Collapse,
  Divider,
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { ChevronDownIcon, ChevronUpIcon, AddIcon, CloseIcon } from '@chakra-ui/icons';
import { useProductionTicket } from '../../../../hooks/useProductionTicket';
import { useProduct } from '../../../../hooks/useProduct';
import { 
  ProductVariantAutoComplete, 
  BOMItemResponse
} from '../../../../types/product';
import { 
  BulkCreateProductionTicketRequest, 
  ProductVariantBomRequest,
  BomItemProductionTicketRequest
} from '../../../../types/productionTicket';
import { MaterialVariantAutoComplete } from '../../../../types/material';
import ProductVariantSearchBar from '../../../../components/product/ProductVariantSearchBar';
import MaterialVariantSearchBar from '../../../../components/material/MaterialVariantSearchBar';
import { getAllMaterialVariantsForAutocomplete } from '../../../../services/materialService';

interface ProductVariantData {
  variant: ProductVariantAutoComplete;
  quantity: number;
  expectedCompleteDate: string;
  bomItems: BOMItemResponse[];
  customBomItems: BomItemProductionTicketRequest[];
}

const CreateProductionTicketPage: React.FC = () => {
  const [title, setTitle] = useState('');
  const [productVariants, setProductVariants] = useState<ProductVariantData[]>([]);
  const [productVariantSearchResults, setProductVariantSearchResults] = useState<ProductVariantAutoComplete[]>([]);
  const [materialVariantSearchResults, setMaterialVariantSearchResults] = useState<MaterialVariantAutoComplete[]>([]);
  const [currentVariantIndex, setCurrentVariantIndex] = useState<number>(-1);
  const [currentMaterialIndex, setCurrentMaterialIndex] = useState<number>(-1);
  
  const { bulkCreateProductionTicket, bulkCreating, error, clearError } = useProductionTicket();
  const { fetchVariantAutocomplete, fetchBomItems } = useProduct();
  
  const toast = useToast();
  const router = useRouter();

  // Handle product variant search
  const handleProductVariantSearch = useCallback(async (search: string) => {
    try {
      const results = await fetchVariantAutocomplete(search);
      setProductVariantSearchResults(results || []);
    } catch (err) {
      console.error('Error searching product variants:', err);
    }
  }, [fetchVariantAutocomplete]);

  // Handle material variant search
  const handleMaterialVariantSearch = useCallback(async (search: string) => {
    try {
      const results = await getAllMaterialVariantsForAutocomplete(search);
      setMaterialVariantSearchResults(results);
    } catch (err) {
      console.error('Error searching material variants:', err);
    }
  }, []);

  // Handle product variant selection
  const handleProductVariantSelect = useCallback(async (variantId: number) => {
    try {
      // Find the selected variant from search results
      const selectedVariant = productVariantSearchResults.find(v => v.variantId === variantId);
      if (!selectedVariant) {
        toast({
          title: 'Error',
          description: 'Selected variant not found',
          status: 'error',
          duration: 3000,
          isClosable: true,
        });
        return;
      }

      // Check if variant already exists
      const existingIndex = productVariants.findIndex(pv => pv.variant.variantId === selectedVariant.variantId);
      if (existingIndex !== -1) {
        toast({
          title: 'Variant already added',
          description: 'This product variant has already been added to the production ticket.',
          status: 'warning',
          duration: 3000,
          isClosable: true,
        });
        return;
      }

      // Get BOM items for the variant
      const bomItems = await fetchBomItems(selectedVariant.variantId, selectedVariant.variantId);
      
      const newProductVariant: ProductVariantData = {
        variant: selectedVariant,
        quantity: 1,
        expectedCompleteDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        bomItems,
        customBomItems: [],
      };

      setProductVariants(prev => [...prev, newProductVariant]);
      setProductVariantSearchResults([]);
    } catch (err: any) {
      toast({
        title: 'Error',
        description: err.message || 'Failed to add product variant',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  }, [productVariants, productVariantSearchResults, fetchBomItems, toast]);

  // Handle material variant selection for custom BOM
  const handleMaterialVariantSelect = useCallback((materialVariantId: number) => {
    if (currentVariantIndex === -1 || currentMaterialIndex === -1) return;

    // Find the selected material variant from search results
    const selectedMaterialVariant = materialVariantSearchResults.find(m => m.variantId === materialVariantId);
    if (!selectedMaterialVariant) {
      toast({
        title: 'Error',
        description: 'Selected material variant not found',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    const newBomItem: BomItemProductionTicketRequest = {
      materialVariantSku: selectedMaterialVariant.variantSku,
      plannedQuantity: 1,
      actualQuantity: 0,
    };

    setProductVariants(prev => {
      const updated = [...prev];
      updated[currentVariantIndex].customBomItems[currentMaterialIndex] = newBomItem;
      return updated;
    });

    setMaterialVariantSearchResults([]);
    setCurrentMaterialIndex(-1);
  }, [currentVariantIndex, currentMaterialIndex, materialVariantSearchResults, toast]);

  // Remove product variant
  const removeProductVariant = useCallback((index: number) => {
    setProductVariants(prev => prev.filter((_, i) => i !== index));
  }, []);

  // Add custom BOM item
  const addCustomBomItem = useCallback((variantIndex: number) => {
    setProductVariants(prev => {
      const updated = [...prev];
      updated[variantIndex].customBomItems.push({
        materialVariantSku: '',
        plannedQuantity: 1,
        actualQuantity: 0,
      });
      return updated;
    });
  }, []);

  // Remove custom BOM item
  const removeCustomBomItem = useCallback((variantIndex: number, bomIndex: number) => {
    setProductVariants(prev => {
      const updated = [...prev];
      updated[variantIndex].customBomItems.splice(bomIndex, 1);
      return updated;
    });
  }, []);

  // Handle form submission
  const handleSubmit = useCallback(async () => {
    if (!title.trim()) {
      toast({
        title: 'Title required',
        description: 'Please enter a title for the production ticket.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    if (productVariants.length === 0) {
      toast({
        title: 'No product variants',
        description: 'Please add at least one product variant.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    try {
      const request: BulkCreateProductionTicketRequest = {
        name: title,
        productVariants: productVariants.map(pv => ({
          productVariantSku: pv.variant.variantSku,
          quantity: pv.quantity,
          expectedCompleteDate: pv.expectedCompleteDate,
          boms: pv.customBomItems,
        })),
      };

      const response = await bulkCreateProductionTicket(request);
      
      toast({
        title: 'Success',
        description: response.message,
        status: 'success',
        duration: 5000,
        isClosable: true,
      });

      router.push('/ticket/production');
    } catch (err: any) {
      toast({
        title: 'Error',
        description: err.message || 'Failed to create production ticket',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  }, [title, productVariants, bulkCreateProductionTicket, toast, router]);

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        <Text fontSize="2xl" fontWeight="bold">
          Create Production Ticket
        </Text>

        {error && (
          <Alert status="error">
            <AlertIcon />
            {error}
            <Button size="sm" ml={2} onClick={clearError}>
              Dismiss
            </Button>
          </Alert>
        )}

        {/* Title Input */}
        <FormControl isInvalid={!title.trim()}>
          <FormLabel>Production Ticket Title</FormLabel>
          <Input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Enter production ticket title"
          />
          <FormErrorMessage>Title is required</FormErrorMessage>
        </FormControl>

        {/* Product Variant Search */}
        <Box>
          <FormLabel>Search Product Variant</FormLabel>
          <ProductVariantSearchBar
            onSearch={handleProductVariantSearch}
            onSelectProductVariant={handleProductVariantSelect}
            productVariantsForAutocomplete={productVariantSearchResults}
          />
        </Box>

        {/* Product Variants List */}
        {productVariants.map((productVariant, variantIndex) => (
          <Box key={productVariant.variant.variantId} p={4} border="1px" borderColor="gray.200" borderRadius="md">
            <VStack spacing={4} align="stretch">
              {/* Variant Header */}
              <HStack justify="space-between">
                <VStack align="start" spacing={1}>
                  <Text fontWeight="bold">{productVariant.variant.variantName}</Text>
                  <Text fontSize="sm" color="gray.600">
                    Product: {productVariant.variant.productName} ({productVariant.variant.productSku})
                  </Text>
                  <Text fontSize="sm" color="gray.500">
                    Variant SKU: {productVariant.variant.variantSku}
                  </Text>
                </VStack>
                <IconButton
                  aria-label="Remove variant"
                  icon={<CloseIcon />}
                  size="sm"
                  colorScheme="red"
                  variant="outline"
                  onClick={() => removeProductVariant(variantIndex)}
                />
              </HStack>

              {/* Quantity and Date Inputs */}
              <HStack spacing={4}>
                <FormControl>
                  <FormLabel>Quantity</FormLabel>
                  <NumberInput
                    min={1}
                    value={productVariant.quantity}
                    onChange={(_, value) => {
                      setProductVariants(prev => {
                        const updated = [...prev];
                        updated[variantIndex].quantity = value;
                        return updated;
                      });
                    }}
                  >
                    <NumberInputField />
                    <NumberInputStepper>
                      <NumberIncrementStepper />
                      <NumberDecrementStepper />
                    </NumberInputStepper>
                  </NumberInput>
                </FormControl>

                <FormControl>
                  <FormLabel>Expected Complete Date</FormLabel>
                  <Input
                    type="date"
                    value={productVariant.expectedCompleteDate}
                    onChange={(e) => {
                      setProductVariants(prev => {
                        const updated = [...prev];
                        updated[variantIndex].expectedCompleteDate = e.target.value;
                        return updated;
                      });
                    }}
                  />
                </FormControl>
              </HStack>

              {/* BOM Items Section */}
              <Box>
                <HStack justify="space-between" mb={2}>
                  <Text fontWeight="medium">BOM Items</Text>
                  <Badge colorScheme="blue">
                    {productVariant.bomItems.length + productVariant.customBomItems.length} items
                  </Badge>
                </HStack>

                {/* Existing BOM Items */}
                {productVariant.bomItems.length > 0 && (
                  <TableContainer>
                    <Table variant="simple" size="sm">
                      <Thead>
                        <Tr>
                          <Th>Material Variant</Th>
                          <Th>Quantity</Th>
                          <Th>UOM</Th>
                        </Tr>
                      </Thead>
                      <Tbody>
                        {productVariant.bomItems.map((item) => (
                          <Tr key={item.id}>
                            <Td>
                              <VStack align="start" spacing={0}>
                                <Text fontWeight="medium" fontSize="sm">
                                  {item.materialName}
                                </Text>
                                <Text fontSize="xs" color="gray.500">
                                  {item.materialSku}
                                </Text>
                              </VStack>
                            </Td>
                            <Td>
                              <Badge colorScheme="green" variant="subtle">
                                {item.quantity}
                              </Badge>
                            </Td>
                            <Td>
                              <Text fontSize="sm" color="gray.600">
                                {item.uom}
                              </Text>
                            </Td>
                          </Tr>
                        ))}
                      </Tbody>
                    </Table>
                  </TableContainer>
                )}

                {/* Warning if no BOM items */}
                {productVariant.bomItems.length === 0 && (
                  <Alert status="warning" mb={4}>
                    <AlertIcon />
                    No BOM items found for this variant. Please add custom BOM items below.
                  </Alert>
                )}

                {/* Custom BOM Items */}
                <VStack spacing={3} align="stretch">
                  <HStack justify="space-between">
                    <Text fontSize="sm" fontWeight="medium">
                      Custom BOM Items
                    </Text>
                    <Button
                      size="sm"
                      leftIcon={<AddIcon />}
                      onClick={() => addCustomBomItem(variantIndex)}
                    >
                      Add BOM Item
                    </Button>
                  </HStack>

                  {productVariant.customBomItems.map((bomItem, bomIndex) => (
                    <Box key={bomIndex} p={3} border="1px" borderColor="gray.200" borderRadius="md">
                      <VStack spacing={3} align="stretch">
                        <HStack justify="space-between">
                          <Text fontSize="sm" fontWeight="medium">
                            BOM Item {bomIndex + 1}
                          </Text>
                          <IconButton
                            aria-label="Remove BOM item"
                            icon={<CloseIcon />}
                            size="xs"
                            colorScheme="red"
                            variant="outline"
                            onClick={() => removeCustomBomItem(variantIndex, bomIndex)}
                          />
                        </HStack>

                        <MaterialVariantSearchBar
                          onSearch={handleMaterialVariantSearch}
                          onSelectMaterialVariant={(materialVariantId) => {
                            setCurrentVariantIndex(variantIndex);
                            setCurrentMaterialIndex(bomIndex);
                            handleMaterialVariantSelect(materialVariantId);
                          }}
                          materialVariantsForAutocomplete={materialVariantSearchResults}
                        />

                        <HStack spacing={3}>
                          <FormControl>
                            <FormLabel fontSize="sm">Planned Quantity</FormLabel>
                            <NumberInput
                              min={0}
                              value={bomItem.plannedQuantity}
                              onChange={(_, value) => {
                                setProductVariants(prev => {
                                  const updated = [...prev];
                                  updated[variantIndex].customBomItems[bomIndex].plannedQuantity = value;
                                  return updated;
                                });
                              }}
                            >
                              <NumberInputField />
                              <NumberInputStepper>
                                <NumberIncrementStepper />
                                <NumberDecrementStepper />
                              </NumberInputStepper>
                            </NumberInput>
                          </FormControl>

                          <FormControl>
                            <FormLabel fontSize="sm">Actual Quantity</FormLabel>
                            <NumberInput
                              min={0}
                              value={bomItem.actualQuantity}
                              onChange={(_, value) => {
                                setProductVariants(prev => {
                                  const updated = [...prev];
                                  updated[variantIndex].customBomItems[bomIndex].actualQuantity = value;
                                  return updated;
                                });
                              }}
                            >
                              <NumberInputField />
                              <NumberInputStepper>
                                <NumberIncrementStepper />
                                <NumberDecrementStepper />
                              </NumberInputStepper>
                            </NumberInput>
                          </FormControl>
                        </HStack>
                      </VStack>
                    </Box>
                  ))}
                </VStack>
              </Box>
            </VStack>
          </Box>
        ))}

        {/* Submit Button */}
        <HStack spacing={4} justify="center">
          <Button
            colorScheme="blue"
            onClick={handleSubmit}
            isLoading={bulkCreating}
            loadingText="Creating..."
            size="lg"
          >
            Create Production Ticket
          </Button>
          <Button
            variant="outline"
            onClick={() => router.push('/ticket/production')}
          >
            Cancel
          </Button>
        </HStack>
      </VStack>
    </Box>
  );
};

export default CreateProductionTicketPage; 