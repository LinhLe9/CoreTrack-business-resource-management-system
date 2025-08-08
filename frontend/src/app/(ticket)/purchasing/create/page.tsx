'use client';

import React, { useState, useCallback, useEffect } from 'react';
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
  Card,
  CardBody,
  SimpleGrid,
} from '@chakra-ui/react';
import { useRouter, useSearchParams } from 'next/navigation';
import { ChevronDownIcon, ChevronUpIcon, AddIcon, CloseIcon } from '@chakra-ui/icons';
import { usePurchasingTicket } from '../../../../hooks/usePurchasingTicket';
import { useUser } from '../../../../hooks/useUser';
import { 
  MaterialVariantAutoComplete 
} from '../../../../types/material';
import { 
  BulkCreatePurchasingTicketRequest, 
  CreatePurchasingTicketRequest
} from '../../../../types/purchasingTicket';
import MaterialVariantSearchBar from '../../../../components/material/MaterialVariantSearchBar';
import { getAllMaterialVariantsForAutocomplete, getSuppliersByMaterialVariantSku } from '../../../../services/materialService';
import { MaterialSupplierResponse } from '../../../../types/material';

interface MaterialVariantData {
  variant: MaterialVariantAutoComplete;
  quantity: number;
  expectedReadyDate: string; // YYYY-MM-DD date string
  suppliers: MaterialSupplierResponse[];
}

interface LowStockMaterial {
  id: number;
  name: string;
  sku: string;
  currentStock: string;
  minAlertStock: string;
  maxStockLevel: string;
  group?: string;
}

const CreatePurchasingTicketPage: React.FC = () => {
  const [title, setTitle] = useState('');
  const [materialVariants, setMaterialVariants] = useState<MaterialVariantData[]>([]);
  const [materialVariantSearchResults, setMaterialVariantSearchResults] = useState<MaterialVariantAutoComplete[]>([]);

  const router = useRouter();
  const searchParams = useSearchParams();
  const toast = useToast();
  const { isOwner, user } = useUser();

  // Check if user has permission to access this page
  useEffect(() => {
    console.log('=== Purchasing Create Page Debug ===');
    console.log('User from useUser:', user);
    console.log('isOwner():', isOwner());
    console.log('=====================================');
    
    if (user && !isOwner()) {
      console.log('Access denied - redirecting to /purchasing');
      toast({
        title: 'Access Denied',
        description: 'Only owners can create purchasing tickets.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      router.push('/purchasing');
    }
  }, [user, isOwner, router, toast]);
  
  // Load low stock data from URL parameters
  useEffect(() => {
    const lowStockData = searchParams.get('lowStockData');
    if (lowStockData) {
      try {
        const parsedData: LowStockMaterial[] = JSON.parse(decodeURIComponent(lowStockData));
        
        // Auto-generate title
        const materialNames = parsedData.map(m => m.name).join(', ');
        setTitle(`Purchasing for Low Stock Items: ${materialNames}`);
        
        // Convert low stock materials to material variants
        const convertLowStockToMaterialVariants = async () => {
          const variants: MaterialVariantData[] = [];
          
          for (const lowStockMaterial of parsedData) {
            try {
              // Search for the material variant by SKU
              const searchResults = await getAllMaterialVariantsForAutocomplete(lowStockMaterial.sku);
              const matchingVariant = searchResults?.find(v => v.variantSku === lowStockMaterial.sku);
              
              if (matchingVariant) {
                // Get suppliers for the variant
                const suppliers = await getSuppliersByMaterialVariantSku(matchingVariant.variantSku);
                
                // Calculate required quantity
                const currentStock = parseFloat(lowStockMaterial.currentStock || '0');
                const minAlert = parseFloat(lowStockMaterial.minAlertStock || '0');
                const maxLevel = parseFloat(lowStockMaterial.maxStockLevel || '100');
                const targetStock = maxLevel * 0.8;
                const requiredQuantity = Math.max(targetStock - currentStock, minAlert - currentStock);
                
                const variantData: MaterialVariantData = {
                  variant: matchingVariant,
                  quantity: Math.ceil(requiredQuantity),
                  expectedReadyDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
                  suppliers,
                };
                
                variants.push(variantData);
              }
            } catch (error) {
              console.error(`Error processing low stock material ${lowStockMaterial.sku}:`, error);
            }
          }
          
          setMaterialVariants(variants);
          
          if (variants.length > 0) {
            toast({
              title: 'Low Stock Items Loaded',
              description: `Pre-filled ${variants.length} material variants from low stock items.`,
              status: 'success',
              duration: 5000,
              isClosable: true,
            });
          }
        };
        
        convertLowStockToMaterialVariants();
      } catch (error) {
        console.error('Error parsing low stock data:', error);
        toast({
          title: 'Error',
          description: 'Failed to load low stock data',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    }
  }, [searchParams, toast]);
  
  // Helper function to convert date string to YYYY-MM-DD format
  const convertToDateString = (dateString: string): string => {
    try {
      const date = new Date(dateString);
      if (date instanceof Date && !isNaN(date.getTime())) {
        return date.toISOString().split('T')[0]; // Return YYYY-MM-DD format
      }
      throw new Error('Invalid date');
    } catch (error) {
      console.error('Error converting date:', dateString, error);
      // Fallback to current date + 7 days
      return new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    }
  };
  
  const { bulkCreatePurchasingTicket, bulkCreating, error, clearError } = usePurchasingTicket();
  
  // Handle material variant search
  const handleMaterialVariantSearch = useCallback(async (search: string) => {
    try {
      const results = await getAllMaterialVariantsForAutocomplete(search);
      setMaterialVariantSearchResults(results);
    } catch (err) {
      console.error('Error searching material variants:', err);
    }
  }, []);

  // Handle material variant selection
  const handleMaterialVariantSelect = useCallback(async (variantId: number) => {
    try {
      // Find the selected variant from search results
      const selectedVariant = materialVariantSearchResults.find(v => v.variantId === variantId);
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
      const existingIndex = materialVariants.findIndex(mv => mv.variant.variantId === selectedVariant.variantId);
      if (existingIndex !== -1) {
        toast({
          title: 'Variant already added',
          description: 'This material variant has already been added to the purchasing ticket.',
          status: 'warning',
          duration: 3000,
          isClosable: true,
        });
        return;
      }

      // Get suppliers for the material variant
      let suppliers: MaterialSupplierResponse[] = [];
      try {
        suppliers = await getSuppliersByMaterialVariantSku(selectedVariant.variantSku);
      } catch (err) {
        console.warn('Failed to fetch suppliers for material variant:', selectedVariant.variantSku, err);
        // Continue without suppliers - this is not a critical error
      }
      
      const newMaterialVariant: MaterialVariantData = {
        variant: selectedVariant,
        quantity: 1,
        expectedReadyDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        suppliers,
      };

      setMaterialVariants(prev => [...prev, newMaterialVariant]);
      setMaterialVariantSearchResults([]);
    } catch (err: any) {
      toast({
        title: 'Error',
        description: err.message || 'Failed to add material variant',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  }, [materialVariants, materialVariantSearchResults, toast]);

  // Handle quantity change
  const handleQuantityChange = useCallback((index: number, value: number) => {
    setMaterialVariants(prev => 
      prev.map((mv, i) => 
        i === index ? { ...mv, quantity: value } : mv
      )
    );
  }, []);

  // Handle date change
  const handleDateChange = useCallback((index: number, date: string) => {
    setMaterialVariants(prev => 
      prev.map((mv, i) => 
        i === index ? { ...mv, expectedReadyDate: convertToDateString(date) } : mv
      )
    );
  }, []);

  // Remove material variant
  const removeMaterialVariant = useCallback((index: number) => {
    setMaterialVariants(prev => prev.filter((_, i) => i !== index));
  }, []);

  // Handle form submission
  const handleSubmit = useCallback(async () => {
    if (!title.trim()) {
      toast({
        title: 'Title Required',
        description: 'Please enter a title for the purchasing ticket.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    if (materialVariants.length === 0) {
      toast({
        title: 'No Material Variants',
        description: 'Please add at least one material variant to the purchasing ticket.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    // Validate all material variants
    const invalidVariants = materialVariants.filter(mv => 
      mv.quantity <= 0 || !mv.expectedReadyDate
    );

    if (invalidVariants.length > 0) {
      toast({
        title: 'Invalid Data',
        description: 'Please ensure all material variants have valid quantity and expected ready date.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    try {
      const request: BulkCreatePurchasingTicketRequest = {
        name: title,
        singleTicket: materialVariants.map(mv => ({
          materialVariantSku: mv.variant.variantSku,
          quantity: mv.quantity,
          expectedReadyDate: mv.expectedReadyDate,
        })),
      };

      const response = await bulkCreatePurchasingTicket(request);
      
      if (response.success) {
        toast({
          title: 'Success',
          description: response.message,
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
        router.push('/purchasing');
      } else {
        toast({
          title: 'Error',
          description: response.message,
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (err: any) {
      toast({
        title: 'Error',
        description: err.message || 'Failed to create purchasing ticket',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  }, [title, materialVariants, bulkCreatePurchasingTicket, toast, router]);

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        <Text fontSize="2xl" fontWeight="bold">Create Purchasing Ticket</Text>

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
        <FormControl isRequired>
          <FormLabel>Purchasing Ticket Title</FormLabel>
          <Input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Enter purchasing ticket title"
          />
        </FormControl>

        {/* Material Variant Search */}
        <Box>
          <Text fontSize="lg" fontWeight="semibold" mb={4}>
            Add Material Variants
          </Text>
          <MaterialVariantSearchBar
            onSearch={handleMaterialVariantSearch}
            onSelectMaterialVariant={handleMaterialVariantSelect}
            materialVariantsForAutocomplete={materialVariantSearchResults}
          />
        </Box>

        {/* Material Variants List */}
        {materialVariants.length > 0 && (
          <Box>
            <Text fontSize="lg" fontWeight="semibold" mb={4}>
              Material Variants ({materialVariants.length})
            </Text>
            
            <VStack spacing={4} align="stretch">
              {materialVariants.map((mv, index) => (
                <Card key={index} variant="outline">
                  <CardBody>
                    <VStack spacing={4} align="stretch">
                      {/* Material Variant Info */}
                      <HStack justify="space-between">
                        <VStack align="start" spacing={1}>
                          <Text fontWeight="bold">{mv.variant.variantName}</Text>
                          <Text fontSize="sm" color="gray.600">
                            SKU: {mv.variant.variantSku}
                          </Text>
                          <Text fontSize="sm" color="gray.600">
                            Material: {mv.variant.materialName} ({mv.variant.materialSku})
                          </Text>
                          {mv.variant.materialGroup && (
                            <Text fontSize="sm" color="gray.600">
                              Group: {mv.variant.materialGroup}
                            </Text>
                          )}
                        </VStack>
                        <IconButton
                          aria-label="Remove material variant"
                          icon={<CloseIcon />}
                          size="sm"
                          colorScheme="red"
                          variant="ghost"
                          onClick={() => removeMaterialVariant(index)}
                        />
                      </HStack>

                      <Divider />

                      {/* Quantity and Date Inputs */}
                      <HStack spacing={4}>
                        <FormControl isRequired>
                          <FormLabel>Quantity</FormLabel>
                          <NumberInput
                            value={mv.quantity}
                            onChange={(_, value) => handleQuantityChange(index, value)}
                            min={1}
                          >
                            <NumberInputField />
                            <NumberInputStepper>
                              <NumberIncrementStepper />
                              <NumberDecrementStepper />
                            </NumberInputStepper>
                          </NumberInput>
                        </FormControl>

                        <FormControl isRequired>
                          <FormLabel>Expected Ready Date</FormLabel>
                          <Input
                            type="date"
                            value={mv.expectedReadyDate}
                            onChange={(e) => handleDateChange(index, e.target.value)}
                          />
                        </FormControl>
                      </HStack>

                      {/* Suppliers Section */}
                      {mv.suppliers.length > 0 && (
                        <Box>
                          <Text fontSize="md" fontWeight="semibold" mb={2}>
                            Available Suppliers ({mv.suppliers.length})
                          </Text>
                          <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} spacing={3}>
                            {mv.suppliers.map((supplier, supplierIndex) => (
                              <Card key={supplierIndex} size="sm" variant="outline">
                                <CardBody p={3}>
                                  <VStack spacing={1} align="start">
                                    <Text fontWeight="semibold" fontSize="sm">
                                      {supplier.supplierName}
                                    </Text>
                                    <Text fontSize="xs" color="gray.600">
                                      Price: {supplier.price} {supplier.currency}
                                    </Text>
                                    <Text fontSize="xs" color="gray.600">
                                      Lead Time: {supplier.leadTimeDays} days
                                    </Text>
                                    <Text fontSize="xs" color="gray.600">
                                      Min Order: {supplier.minOrderQuantity}
                                    </Text>
                                    {supplier.supplierMaterialCode && (
                                      <Text fontSize="xs" color="gray.600">
                                        Code: {supplier.supplierMaterialCode}
                                      </Text>
                                    )}
                                  </VStack>
                                </CardBody>
                              </Card>
                            ))}
                          </SimpleGrid>
                        </Box>
                      )}

                      {mv.suppliers.length === 0 && (
                        <Text fontSize="sm" color="gray.500" fontStyle="italic">
                          No suppliers found for this material variant
                        </Text>
                      )}
                    </VStack>
                  </CardBody>
                </Card>
              ))}
            </VStack>
          </Box>
        )}

        {/* Submit Button */}
        <HStack spacing={4} justify="center">
          <Button
            onClick={() => router.push('/purchasing')}
            variant="outline"
            size="lg"
          >
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            colorScheme="teal"
            size="lg"
            isLoading={bulkCreating}
            loadingText="Creating..."
            isDisabled={materialVariants.length === 0}
          >
            Create Purchasing Ticket
          </Button>
        </HStack>
      </VStack>
    </Box>
  );
};

export default CreatePurchasingTicketPage; 