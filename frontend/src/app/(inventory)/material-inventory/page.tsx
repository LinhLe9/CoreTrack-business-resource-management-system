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
  useDisclosure,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { AddIcon, MinusIcon, CheckIcon, SettingsIcon } from '@chakra-ui/icons';

import InventoryMaterialSearchBar from '../../../components/inventory/InventoryMaterialSearchBar';
import InventoryMaterialFilters from '../../../components/inventory/InventoryMaterialFilters';
import MaterialInventoryCard from '../../../components/inventory/MaterialInventoryCard';
import MaterialBulkStockTransactionModal from '../../../components/inventory/MaterialBulkStockTransactionModal';
import { getMaterialInventoryFilter, getAllMaterialInventoryForAutocomplete } from '../../../services/materialInventoryService';
import { MaterialInventoryQueryParams, MaterialInventoryFilterParams, MaterialInventoryAutoComplete } from '../../../types/materialInventory';
import { SearchInventoryResponse, AllSearchInventoryResponse } from '../../../types/productInventory';
import { PageResponse } from '../../../types/PageResponse';
import { useUser } from '../../../hooks/useUser';

const MaterialInventoryPage: React.FC = () => {
  const [pageData, setPageData] = useState<PageResponse<SearchInventoryResponse> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [queryParams, setQueryParams] = useState<MaterialInventoryQueryParams>({});
  const [allMaterialInventoryForAutocomplete, setAllMaterialInventoryForAutocomplete] = useState<AllSearchInventoryResponse[]>([]);
  const [searchLoading, setSearchLoading] = useState(false);
  
  // Bulk operations state
  const [isSelectionMode, setIsSelectionMode] = useState(false);
  const [selectedItems, setSelectedItems] = useState<Set<number>>(new Set());
  const { isOpen: isBulkAddOpen, onOpen: onBulkAddOpen, onClose: onBulkAddClose } = useDisclosure();
  const { isOpen: isBulkSubtractOpen, onOpen: onBulkSubtractOpen, onClose: onBulkSubtractClose } = useDisclosure();
  const { isOpen: isBulkSetOpen, onOpen: onBulkSetOpen, onClose: onBulkSetClose } = useDisclosure();
  
  const toast = useToast();
  const router = useRouter();
  const { isOwner, isWarehouseStaff } = useUser();

  // Check if user has permission for inventory operations
  const hasInventoryPermission = () => isOwner() || isWarehouseStaff();

  // Fetch autocomplete list only when needed
  const fetchAllMaterialInventory = useCallback(async (search?: string) => {
    if (searchLoading) return;
    
    setSearchLoading(true);
    try {
      const data = await getAllMaterialInventoryForAutocomplete(search);
      setAllMaterialInventoryForAutocomplete(data);
    } catch (err) {
      console.error("Error fetching all material inventory for autocomplete:", err);
    } finally {
      setSearchLoading(false);
    }
  }, [searchLoading]);

  const handleSearchInputChange = useCallback((inputValue: string) => {
    if (inputValue.length >= 2) {
      fetchAllMaterialInventory(inputValue);
    }
  }, [fetchAllMaterialInventory]);

  // Fetch material inventory applied filter/search
  const fetchMaterialInventory = useCallback(async (params: MaterialInventoryQueryParams) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getMaterialInventoryFilter(params);
      setPageData(data);
    } catch (err: any) {
      setError('Failed to fetch material inventory. Please try again.');
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
    fetchMaterialInventory(queryParams);
  }, [fetchMaterialInventory, queryParams]);

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

    setQueryParams((prev: any) => ({
      ...prev,
      search: searchTerm || undefined,
      page: 0,
    }));
  };

  const handleFilter = (filters: MaterialInventoryFilterParams) => {
    setQueryParams((prev: MaterialInventoryQueryParams) => ({
      ...prev,
      groupMaterials: filters.groupMaterials,
      inventoryStatus: filters.inventoryStatus,
      page: 0,
    }));
  };

  const handlePageChange = (newPage: number) => {
    setQueryParams((prev: MaterialInventoryQueryParams) => ({
      ...prev,
      page: newPage,
    }));
  };

  const handleSelectMaterialInventoryFromSearch = (variantId: number) => {
    router.push(`/material-inventory/${variantId}`);
  };

  const handleInitialStock = () => {
    router.push('/material-inventory/initial');
  };

  const handleStockUpdate = () => {
    // Refresh the material inventory data after stock update
    console.log('=== REFRESHING MATERIAL INVENTORY ===');
    console.log('Current queryParams:', queryParams);
    console.log('=====================================');
    fetchMaterialInventory(queryParams);
  };

  // Bulk operations handlers
  const toggleSelectionMode = () => {
    setIsSelectionMode(!isSelectionMode);
    if (isSelectionMode) {
      setSelectedItems(new Set());
    }
  };

  const handleSelectAll = () => {
    if (!pageData) return;
    const allIds = pageData.content
      .filter(item => item.id != null)
      .map(item => item.id!);
    setSelectedItems(new Set(allIds));
  };

  const handleCancelSelection = () => {
    setIsSelectionMode(false);
    setSelectedItems(new Set());
  };

  const handleItemSelection = (variantId: number, isSelected: boolean) => {
    setSelectedItems(prev => {
      const newSet = new Set(prev);
      if (isSelected) {
        newSet.add(variantId);
      } else {
        newSet.delete(variantId);
      }
      return newSet;
    });
  };

  const handleBulkAdd = () => {
    if (selectedItems.size === 0) {
      toast({
        title: 'No Items Selected',
        description: 'Please select at least one item',
        status: 'warning',
        duration: 3000,
        isClosable: true,
      });
      return;
    }
    onBulkAddOpen();
  };

  const handleBulkSubtract = () => {
    if (selectedItems.size === 0) {
      toast({
        title: 'No items selected',
        description: 'Please select at least one item to subtract stock.',
        status: 'warning',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    onBulkSubtractOpen();
  };

  const handleBulkSet = () => {
    if (selectedItems.size === 0) {
      toast({
        title: 'No items selected',
        description: 'Please select at least one item to set stock.',
        status: 'warning',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    onBulkSetOpen();
  };

  const handleBulkSuccess = () => {
    // Reset selection mode and refresh data
    setIsSelectionMode(false);
    setSelectedItems(new Set());
    fetchMaterialInventory(queryParams);
  };

  // Get selected items data for modal
  const getSelectedItemsData = () => {
    if (!pageData) return [];
    
    return pageData.content
      .filter(item => selectedItems.has(item.id!))
      .map(item => ({
        id: item.id!,
        sku: item.sku,
        name: item.name,
      }));
  };

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        {/* Header - Centered */}
        <Center>
          <VStack spacing={4} align="center">
            <Heading size="lg">Material Inventory</Heading>
            
            {/* Search Bar and Initial Stock Button - Side by side */}
            <HStack spacing={4} align="center" w="full" maxW="800px">
              <Box flex={1}>
                <InventoryMaterialSearchBar
                  onSearch={handleSearch}
                  onSelectMaterialInventory={handleSelectMaterialInventoryFromSearch}
                  materialInventoryForAutocomplete={allMaterialInventoryForAutocomplete}
                  onSearchInputChange={handleSearchInputChange}
                />
              </Box>
              
              <Button
                size="sm"
                colorScheme="blue"
                onClick={() => {
                  console.log('=== FORCE REFRESH ===');
                  fetchMaterialInventory(queryParams);
                }}
              >
                Refresh
              </Button>
              
              {hasInventoryPermission() ? (
                <Button
                  leftIcon={<AddIcon />}
                  colorScheme="teal"
                  onClick={handleInitialStock}
                  size="md"
                  flexShrink={0}
                >
                  Initial Stock
                </Button>
              ) : null}
            </HStack>
          </VStack>
        </Center>

        {/* Filters */}
        <InventoryMaterialFilters
          onFilter={handleFilter}
        />

        {/* Bulk Operations Bar */}
        {hasInventoryPermission() ? (
          <HStack spacing={4} justify="center" p={4} bg="gray.50" borderRadius="md">
            <IconButton
              aria-label="Toggle selection mode"
              icon={<CheckIcon />}
              size="sm"
              colorScheme={isSelectionMode ? "blue" : "gray"}
              variant={isSelectionMode ? "solid" : "outline"}
              onClick={toggleSelectionMode}
            />
            <Text fontSize="sm" fontWeight="medium">
              {isSelectionMode ? "Selection Mode" : "Select Items"}
            </Text>
            
            {isSelectionMode && (
              <>
                <IconButton
                  aria-label="Select all"
                  icon={<CheckIcon />}
                  size="sm"
                  colorScheme="blue"
                  variant="outline"
                  onClick={handleSelectAll}
                />
                <Text fontSize="sm">Select All</Text>
                
                <IconButton
                  aria-label="Cancel selection"
                  icon={<MinusIcon />}
                  size="sm"
                  colorScheme="red"
                  variant="outline"
                  onClick={handleCancelSelection}
                />
                <Text fontSize="sm">Cancel</Text>
                
                <IconButton
                  aria-label="Bulk add stock"
                  icon={<AddIcon />}
                  size="sm"
                  colorScheme="green"
                  onClick={handleBulkAdd}
                />
                <Text fontSize="sm">Add</Text>
                
                <IconButton
                  aria-label="Bulk subtract stock"
                  icon={<MinusIcon />}
                  size="sm"
                  colorScheme="red"
                  onClick={handleBulkSubtract}
                />
                <Text fontSize="sm">Subtract</Text>
                
                <IconButton
                  aria-label="Bulk set stock"
                  icon={<SettingsIcon />}
                  size="sm"
                  colorScheme="purple"
                  onClick={handleBulkSet}
                />
                <Text fontSize="sm">Set</Text>
                
                <Text fontSize="sm" color="gray.600">
                  ({selectedItems.size} selected)
                </Text>
              </>
            )}
          </HStack>
        ) : null}

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
          </Alert>
        )}

        {!loading && !error && pageData && (
          <>
            <Text fontSize="sm" color="gray.600" textAlign="center">
              Showing {pageData.content.length} of {pageData.totalElements} results
            </Text>

            <SimpleGrid columns={{ base: 1, lg: 2 }} spacing={6}>
              {pageData.content
                .filter(materialInventory => materialInventory.id != null)
                .map((materialInventory) => {
                  return (
                    <MaterialInventoryCard 
                      key={materialInventory.id} 
                      materialInventory={materialInventory}
                      onStockUpdate={handleStockUpdate}
                      isSelectionMode={isSelectionMode}
                      isSelected={selectedItems.has(materialInventory.id!)}
                      onSelectionChange={handleItemSelection}
                    />
                  );
                })}
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
      </VStack>

      {/* Bulk Add Modal */}
      {hasInventoryPermission() ? (
        <MaterialBulkStockTransactionModal
          isOpen={isBulkAddOpen}
          onClose={onBulkAddClose}
          selectedItems={getSelectedItemsData()}
          transactionType="add"
          onSuccess={handleBulkSuccess}
        />
      ) : null}

      {/* Bulk Subtract Modal */}
      {hasInventoryPermission() ? (
        <MaterialBulkStockTransactionModal
          isOpen={isBulkSubtractOpen}
          onClose={onBulkSubtractClose}
          selectedItems={getSelectedItemsData()}
          transactionType="subtract"
          onSuccess={handleBulkSuccess}
        />
      ) : null}
    </Box>
  );
};

export default MaterialInventoryPage;
