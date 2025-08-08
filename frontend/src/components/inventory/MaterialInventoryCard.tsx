import React, { useState } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Badge,
  Image,
  IconButton,
  Flex,
  Checkbox,
  useDisclosure,
  Heading,
  Tag,
  TagLabel,
} from '@chakra-ui/react';
import { AddIcon, MinusIcon, SettingsIcon } from '@chakra-ui/icons';
import Link from 'next/link';
import { SearchInventoryResponse } from '../../types/productInventory';
import MaterialStockTransactionModal from './MaterialStockTransactionModal';
import { formatBigDecimal } from '../../lib/utils';
import { useUser } from '../../hooks/useUser';

interface MaterialInventoryCardProps {
  materialInventory: SearchInventoryResponse;
  onStockUpdate: () => void;
  isSelectionMode?: boolean;
  isSelected?: boolean;
  onSelectionChange?: (variantId: number, isSelected: boolean) => void;
}

const MaterialInventoryCard: React.FC<MaterialInventoryCardProps> = ({
  materialInventory,
  onStockUpdate,
  isSelectionMode = false,
  isSelected = false,
  onSelectionChange,
}) => {
  const { isOpen: isAddOpen, onOpen: onAddOpen, onClose: onAddClose } = useDisclosure();
  const { isOpen: isSubtractOpen, onOpen: onSubtractOpen, onClose: onSubtractClose } = useDisclosure();
  const { isOpen: isSetOpen, onOpen: onSetOpen, onClose: onSetClose } = useDisclosure();
  const { isOwner, isWarehouseStaff } = useUser();

  // Check if user has permission for inventory operations
  const hasInventoryPermission = () => isOwner() || isWarehouseStaff();

  const formatStockValue = (value: string | null | undefined): string => {
    if (!value || value === 'null' || value === 'undefined') {
      return 'N/A';
    }
    return value;
  };

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

  const handleSelectionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (onSelectionChange) {
      onSelectionChange(materialInventory.id!, e.target.checked);
    }
  };

  const handleStockSuccess = () => {
    onStockUpdate();
  };

  const handleCardClick = (e: React.MouseEvent) => {
    // Prevent navigation if clicking on action buttons or checkbox
    const target = e.target as HTMLElement;
    if (target.closest('button') || target.closest('input[type="checkbox"]')) {
      e.preventDefault();
      e.stopPropagation();
    }
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
            <Box display="flex" alignItems="center" justifyContent="center" flexShrink={0} position="relative" zIndex={10}>
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
            {materialInventory.imageUrl ? (
              <Image
                src={materialInventory.imageUrl}
                alt={materialInventory.name}
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
              {/* Material Name - Large font */}
              <Heading size="md" noOfLines={2} lineHeight="1.2">
                {materialInventory.name}
              </Heading>
              
              {/* SKU - Small font */}
              <Text fontSize="sm" color="gray.600">
                SKU: {materialInventory.sku}
              </Text>

              {/* Material Group Tag */}
              {materialInventory.group && (
                <Tag 
                  size="sm" 
                  variant="outline" 
                  colorScheme="gray" 
                  borderRadius="full" 
                  alignSelf="flex-start"
                  bg="transparent"
                  borderColor="gray.300"
                >
                  <TagLabel>{materialInventory.group}</TagLabel>
                </Tag>
              )}

              {/* Spacer between basic info and stock info */}
              <Box h={2} />

              {/* Stock Information */}
              <VStack align="stretch" spacing={1}>
                <HStack spacing={2}>
                  <Text fontSize="sm" color="gray.600">Current Stock:</Text>
                  <Text fontSize="sm" fontWeight="bold" color="blue.600">
                    {formatStockValue(materialInventory.currentStock)}
                  </Text>
                </HStack>
                {/* Debug info */}
                <Text fontSize="xs" color="gray.400">
                  Debug: ID={materialInventory.id}, Stock={materialInventory.currentStock}
                </Text>
                <HStack spacing={2}>
                  <Text fontSize="sm" color="gray.600">Min Stock:</Text>
                  <Text fontSize="sm" color="gray.700">
                    {formatStockValue(materialInventory.minAlertStock)}
                  </Text>
                </HStack>
                <HStack spacing={2}>
                  <Text fontSize="sm" color="gray.600">Max Stock:</Text>
                  <Text fontSize="sm" color="gray.700">
                    {formatStockValue(materialInventory.maxStockLevel)}
                  </Text>
                </HStack>
              </VStack>

              {/* Inventory Status Tag */}
              <Tag size="sm" colorScheme={getInventoryStatusColor(materialInventory.inventoryStatus)} borderRadius="full" alignSelf="flex-start">
                <TagLabel>{getInventoryStatusDisplayName(materialInventory.inventoryStatus)}</TagLabel>
              </Tag>
            </VStack>
          </Box>

          {/* Column 4: Action Buttons - Vertical layout */}
          {!isSelectionMode && hasInventoryPermission() && (
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
                  onAddOpen();
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
                  onSubtractOpen();
                }}
                _hover={{ transform: 'scale(1.1)' }}
                transition="all 0.2s"
              />
              <IconButton
                aria-label="Set stock"
                icon={<SettingsIcon />}
                size="sm"
                colorScheme="blue"
                variant="solid"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onSetOpen();
                }}
                _hover={{ transform: 'scale(1.1)' }}
                transition="all 0.2s"
              />
            </Box>
          )}
        </Flex>

        {/* Clickable overlay for navigation - excluding action buttons area and checkbox area in selection mode */}
        <Link href={`/material-inventory/${materialInventory.id}`} passHref>
          <Box
            position="absolute"
            top={0}
            left={isSelectionMode ? "60px" : 0} // Exclude checkbox area in selection mode
            right={!isSelectionMode ? "60px" : 0} // Exclude space for action buttons
            bottom={0}
            cursor="pointer"
            zIndex={1}
            _hover={{ bg: 'blackAlpha.50' }}
            transition="background 0.2s"
            borderRadius="lg"
            onClick={handleCardClick}
          />
        </Link>
      </Box>

      {/* Add Stock Modal */}
      <MaterialStockTransactionModal
        isOpen={isAddOpen}
        onClose={onAddClose}
        variantId={materialInventory.id!}
        variantSku={materialInventory.sku}
        variantName={materialInventory.name}
        transactionType="add"
        onSuccess={handleStockSuccess}
      />

      {/* Subtract Stock Modal */}
      <MaterialStockTransactionModal
        isOpen={isSubtractOpen}
        onClose={onSubtractClose}
        variantId={materialInventory.id!}
        variantSku={materialInventory.sku}
        variantName={materialInventory.name}
        transactionType="subtract"
        onSuccess={handleStockSuccess}
      />

      {/* Set Stock Modal */}
      <MaterialStockTransactionModal
        isOpen={isSetOpen}
        onClose={onSetClose}
        variantId={materialInventory.id!}
        variantSku={materialInventory.sku}
        variantName={materialInventory.name}
        transactionType="set"
        onSuccess={handleStockSuccess}
      />
    </>
  );
};

export default MaterialInventoryCard; 