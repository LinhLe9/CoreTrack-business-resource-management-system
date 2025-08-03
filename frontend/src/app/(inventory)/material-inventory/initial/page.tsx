'use client';

import React, { useState, useEffect, useCallback } from 'react';
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

import MaterialVariantSearchBar from '../../../../components/material/MaterialVariantSearchBar';
import { getAllMaterialVariantsForAutocomplete } from '../../../../services/materialService';
import { bulkInitMaterialInventory } from '../../../../services/materialInventoryService';
import { MaterialVariantAutoComplete } from '../../../../types/material';

interface SelectedMaterial {
  variantId: number;
  materialName: string;
  materialSku: string;
  variantSku: string;
  variantName: string;
  materialGroup?: string;
}

const InitialStockPage: React.FC = () => {
  const [allMaterialVariantsForAutocomplete, setAllMaterialVariantsForAutocomplete] = useState<MaterialVariantAutoComplete[]>([]);
  const [selectedMaterials, setSelectedMaterials] = useState<SelectedMaterial[]>([]);
  const [initialStock, setInitialStock] = useState<number>(0);
  const [minAlertStock, setMinAlertStock] = useState<number>(0);
  const [maxStockLevel, setMaxStockLevel] = useState<number>(100);
  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const router = useRouter();

  // Handle material variant search
  const handleMaterialVariantSearch = useCallback(async (search: string) => {
    try {
      const results = await getAllMaterialVariantsForAutocomplete(search);
      setAllMaterialVariantsForAutocomplete(results);
    } catch (err) {
      console.error('Error searching material variants:', err);
    }
  }, []);

  const handleSelectMaterialVariant = (variantId: number) => {
    const variant = allMaterialVariantsForAutocomplete.find(v => v.variantId === variantId);
    if (!variant) return;

    // Check if variant is already selected
    const isAlreadySelected = selectedMaterials.some(m => m.variantId === variantId);
    if (isAlreadySelected) {
      toast({
        title: 'Material Variant Already Selected',
        description: 'This material variant is already in your selection.',
        status: 'warning',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    // Add variant to selection
    const selectedMaterial: SelectedMaterial = {
      variantId: variant.variantId,
      materialName: variant.materialName,
      materialSku: variant.materialSku,
      variantSku: variant.variantSku,
      variantName: variant.variantName,
      materialGroup: variant.materialGroup,
    };

    setSelectedMaterials(prev => [...prev, selectedMaterial]);
  };

  const handleRemoveMaterial = (variantId: number) => {
    setSelectedMaterials(prev => prev.filter(m => m.variantId !== variantId));
  };

  const handleSubmit = async () => {
    if (selectedMaterials.length === 0) {
      toast({
        title: 'No Materials Selected',
        description: 'Please select at least one material variant.',
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
      const materialVariantSkus = selectedMaterials.map(m => m.variantSku);
      
      // Debug log
      console.log('=== BULK INIT MATERIAL REQUEST DEBUG ===');
      console.log('Selected Materials:', selectedMaterials);
      console.log('Material Variant SKUs:', materialVariantSkus);
      console.log('Initial Stock:', initialStock);
      console.log('Min Alert Stock:', minAlertStock);
      console.log('Max Stock Level:', maxStockLevel);
      console.log('========================================');
      
      const response = await bulkInitMaterialInventory(
        materialVariantSkus,
        initialStock,
        minAlertStock,
        maxStockLevel
      );
      
      console.log('=== BULK INIT MATERIAL RESPONSE DEBUG ===');
      console.log('Response:', response);
      console.log('=========================================');
      
      toast({
        title: 'Success',
        description: `Successfully initialized ${response.successfulInits.length} materials. ${response.failedInits.length} failed.`,
        status: 'success', duration: 5000, isClosable: true,
      });
      router.push('/material-inventory');
    } catch (error: any) {
      console.error('=== BULK INIT MATERIAL ERROR DEBUG ===');
      console.error('Error:', error);
      console.error('Error Response:', error.response);
      console.error('Error Data:', error.response?.data);
      console.error('=====================================');
      
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to initialize material stock.',
        status: 'error', duration: 5000, isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    router.push('/material-inventory');
  };

  return (
    <Box p={8} maxW="800px" mx="auto">
      <Heading as="h1" size="xl" textAlign="center" mb={8} color="teal.700">
        Initialize Material Stock
      </Heading>

      <VStack spacing={6} align="stretch">
        {/* Search Bar */}
        <Box>
          <Text fontSize="lg" fontWeight="bold" mb={3}>
            Search and Select Materials
          </Text>
          <MaterialVariantSearchBar
            onSearch={handleMaterialVariantSearch}
            onSelectMaterialVariant={handleSelectMaterialVariant}
            materialVariantsForAutocomplete={allMaterialVariantsForAutocomplete}
          />
        </Box>

        {/* Selected Materials */}
        {selectedMaterials.length > 0 && (
          <Box>
            <Text fontSize="lg" fontWeight="bold" mb={3}>
              Selected Materials ({selectedMaterials.length})
            </Text>
            <VStack spacing={2} align="stretch">
              {selectedMaterials.map((material) => (
                <Card key={material.variantId} size="sm">
                  <CardBody>
                    <HStack justify="space-between">
                      <VStack align="start" spacing={1}>
                        <Text fontWeight="bold">{material.materialName}</Text>
                        <Text fontSize="sm" color="gray.600">
                          Material SKU: {material.materialSku}
                        </Text>
                        <Text fontSize="sm" color="gray.600">
                          Variant SKU: {material.variantSku}
                        </Text>
                        <Text fontSize="sm" color="gray.600">
                          Variant: {material.variantName}
                        </Text>
                        {material.materialGroup && (
                          <Text fontSize="sm" color="gray.600">
                            Group: {material.materialGroup}
                          </Text>
                        )}
                      </VStack>
                      <IconButton
                        icon={<CloseIcon />}
                        aria-label="Remove material variant"
                        size="sm"
                        colorScheme="red"
                        variant="ghost"
                        onClick={() => handleRemoveMaterial(material.variantId)}
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
            isDisabled={selectedMaterials.length === 0}
          >
            Initialize Stock
          </Button>
        </HStack>
      </VStack>
    </Box>
  );
};

export default InitialStockPage;
