// components/inventory/ProductInventoryCard.tsx
import React, { useState } from 'react';
import { 
  Box, Image, Text, Heading, VStack, Tag, TagLabel, Flex, HStack, 
  IconButton, useDisclosure, Checkbox 
} from '@chakra-ui/react';
import { AddIcon, MinusIcon } from '@chakra-ui/icons';
import { SearchInventoryResponse } from '../../types/productInventory';
import NextLink from 'next/link';
import { formatBigDecimal } from '../../lib/utils';
import StockTransactionModal from './StockTransactionModal';

interface ProductInventoryCardProps {
  productInventory: SearchInventoryResponse;
  onStockUpdate?: () => void;
  isSelectionMode?: boolean;
  isSelected?: boolean;
  onSelectionChange?: (variantId: number, isSelected: boolean) => void;
}

const ProductInventoryCard: React.FC<ProductInventoryCardProps> = React.memo(({ 
  productInventory, 
  onStockUpdate,
  isSelectionMode = false,
  isSelected = false,
  onSelectionChange
}) => {
  const { isOpen: isAddModalOpen, onOpen: onAddModalOpen, onClose: onAddModalClose } = useDisclosure();
  const { isOpen: isSubtractModalOpen, onOpen: onSubtractModalOpen, onClose: onSubtractModalClose } = useDisclosure();

  const getInventoryStatusColor = (status: string) => {
    switch (status) {
      case 'IN_STOCK': return 'green';
      case 'OUT_OF_STOCK': return 'red';
      case 'LOW_STOCK': return 'orange';
      case 'OVER_STOCK': return 'yellow';
      default: return 'gray';
    }
  };

  const getInventoryStatusDisplayName = (status: string) => {
    switch (status) {
      case 'IN_STOCK': return 'In Stock';
      case 'OUT_OF_STOCK': return 'Out of Stock';
      case 'LOW_STOCK': return 'Low Stock';
      case 'OVER_STOCK': return 'Over Stock';
      default: return status;
    }
  };

  const formatStockValue = (value: string | null | undefined): string => {
    if (!value || value === 'null' || value === 'undefined') return 'N/A';
    const parsed = parseFloat(value);
    return isNaN(parsed) ? 'N/A' : parsed.toFixed(2);
  };

  const handleStockUpdate = () => {
    onStockUpdate?.();
  };

  const handleSelectionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    e.stopPropagation();
    onSelectionChange?.(productInventory.id!, e.target.checked);
  };

  return (
    <>
      <Box
        borderRadius="lg"
        overflow="hidden"
        p={4}
        boxShadow="md"
        transition="all 0.2s"
        _hover={{ transform: 'translateY(-2px)', boxShadow: 'lg' }}
        bg="white"
        position="relative"
        borderColor={isSelected ? "blue.500" : "gray.200"}
        borderWidth={isSelected ? "2px" : "1px"}
      >


        <Flex gap={4} align="stretch" direction={{ base: "row", lg: "row" }}>
          {/* Column 1: Selection Checkbox */}
          {isSelectionMode && (
            <Box display="flex" alignItems="center" justifyContent="center" flexShrink={0}>
              <Checkbox
                isChecked={isSelected}
                onChange={handleSelectionChange}
                colorScheme="blue"
                size="lg"
              />
            </Box>
          )}

          {/* Column 2: Image */}
          <Box flexShrink={0} alignSelf={{ base: "center", lg: "flex-start" }}>
            {productInventory.imageUrl ? (
              <Image
                src={productInventory.imageUrl}
                alt={productInventory.name}
                boxSize={{ base: "80px", lg: "100px" }}
                objectFit="cover"
                borderRadius="md"
                fallbackSrc="/default-product.jpg"
                border="1px solid"
                borderColor="gray.200"
              />
            ) : (
              <Box
                boxSize={{ base: "80px", lg: "100px" }}
                bg="gray.100"
                borderRadius="md"
                display="flex"
                alignItems="center"
                justifyContent="center"
                border="1px solid"
                borderColor="gray.200"
              >
                <Text fontSize="xs" color="gray.500">No Image</Text>
              </Box>
            )}
          </Box>

          {/* Column 3: Information */}
          <Box flex={1} minW={0}>
            <VStack align="stretch" spacing={1}>
              {/* Product Name - Large font */}
              <Heading size="md" noOfLines={2} lineHeight="1.2">
                {productInventory.name}
              </Heading>
              
              {/* SKU - Small font */}
              <Text fontSize="sm" color="gray.600">
                SKU: {productInventory.sku}
              </Text>

              {/* Product Group Tag */}
              {productInventory.group && (
                <Tag 
                  size="sm" 
                  variant="outline" 
                  colorScheme="gray" 
                  borderRadius="full" 
                  alignSelf="flex-start"
                  bg="transparent"
                  borderColor="gray.300"
                >
                  <TagLabel>{productInventory.group}</TagLabel>
                </Tag>
              )}

              {/* Spacer between basic info and stock info */}
              <Box h={2} />

              {/* Stock Information */}
              <VStack align="stretch" spacing={1}>
                <HStack spacing={2}>
                  <Text fontSize="sm" color="gray.600">Current Stock:</Text>
                  <Text fontSize="sm" fontWeight="bold" color="blue.600">
                    {formatStockValue(productInventory.currentStock)}
                  </Text>
                </HStack>
                <HStack spacing={2}>
                  <Text fontSize="sm" color="gray.600">Min Stock:</Text>
                  <Text fontSize="sm" color="gray.700">
                    {formatStockValue(productInventory.minAlertStock)}
                  </Text>
                </HStack>
                <HStack spacing={2}>
                  <Text fontSize="sm" color="gray.600">Max Stock:</Text>
                  <Text fontSize="sm" color="gray.700">
                    {formatStockValue(productInventory.maxStockLevel)}
                  </Text>
                </HStack>
              </VStack>

              {/* Inventory Status Tag */}
              <Tag size="sm" colorScheme={getInventoryStatusColor(productInventory.inventoryStatus)} borderRadius="full" alignSelf="flex-start">
                <TagLabel>{getInventoryStatusDisplayName(productInventory.inventoryStatus)}</TagLabel>
              </Tag>
            </VStack>
          </Box>

          {/* Column 4: Action Buttons - Vertical layout */}
          {!isSelectionMode && (
            <Box flexShrink={0} display="flex" flexDirection="column" gap={2} alignSelf={{ base: "center", lg: "flex-start" }} position="relative" zIndex={10}>
              <IconButton
                aria-label="Add stock"
                icon={<AddIcon />}
                size="sm"
                colorScheme="green"
                variant="solid"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onAddModalOpen();
                }}
                _hover={{ transform: 'scale(1.1)' }}
                transition="all 0.2s"
              />
              <IconButton
                aria-label="Subtract stock"
                icon={<MinusIcon />}
                size="sm"
                colorScheme="red"
                variant="solid"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onSubtractModalOpen();
                }}
                _hover={{ transform: 'scale(1.1)' }}
                transition="all 0.2s"
              />
            </Box>
          )}
        </Flex>

        {/* Clickable overlay for navigation - excluding action buttons area */}
        <NextLink href={`/product-inventory/${productInventory.id}`} passHref>
          <Box
            position="absolute"
            top={0}
            left={0}
            right={!isSelectionMode ? "60px" : 0} // Exclude space for action buttons
            bottom={0}
            cursor="pointer"
            zIndex={1}
            _hover={{ bg: 'blackAlpha.50' }}
            transition="background 0.2s"
            borderRadius="lg"
          />
        </NextLink>
      </Box>

      {/* Add Stock Modal */}
      <StockTransactionModal
        isOpen={isAddModalOpen}
        onClose={onAddModalClose}
        variantId={productInventory.id}
        variantSku={productInventory.sku}
        variantName={productInventory.name}
        transactionType="add"
        onSuccess={handleStockUpdate}
      />

      {/* Subtract Stock Modal */}
      <StockTransactionModal
        isOpen={isSubtractModalOpen}
        onClose={onSubtractModalClose}
        variantId={productInventory.id}
        variantSku={productInventory.sku}
        variantName={productInventory.name}
        transactionType="subtract"
        onSuccess={handleStockUpdate}
      />
    </>
  );
});

export default ProductInventoryCard; 