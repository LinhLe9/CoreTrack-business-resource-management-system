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
} from '@chakra-ui/react';
import { AddIcon, MinusIcon } from '@chakra-ui/icons';
import { SearchInventoryResponse } from '../../types/productInventory';
import MaterialStockTransactionModal from './MaterialStockTransactionModal';

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

  const formatStockValue = (value: string | null | undefined): string => {
    if (!value || value === 'null' || value === 'undefined') {
      return 'N/A';
    }
    return value;
  };

  const handleSelectionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (onSelectionChange) {
      onSelectionChange(materialInventory.id!, e.target.checked);
    }
  };

  const handleStockSuccess = () => {
    onStockUpdate();
  };

  return (
    <>
      <Box
        border="1px"
        borderColor={isSelected ? "blue.300" : "gray.200"}
        borderRadius="lg"
        p={4}
        bg="white"
        shadow="sm"
        _hover={{ shadow: "md" }}
        transition="all 0.2s"
      >
        <Flex direction={{ base: "row", lg: "row" }} gap={4}>
          {/* Column 1: Checkbox for selection mode */}
          {isSelectionMode && (
            <Checkbox
              isChecked={isSelected}
              onChange={handleSelectionChange}
              alignSelf="center"
              size="lg"
            />
          )}

          {/* Column 2: Image */}
          <Box flexShrink={0}>
            <Image
              src={materialInventory.imageUrl || '/default-product.jpg'}
              alt={materialInventory.name}
              boxSize={{ base: "80px", lg: "100px" }}
              objectFit="cover"
              borderRadius="md"
              fallbackSrc="/default-product.jpg"
            />
          </Box>

          {/* Column 3: Content */}
          <Box flex={1} minW={0}>
            <VStack align="start" spacing={2}>
              {/* Name, SKU, Group */}
              <VStack align="start" spacing={1} w="full">
                <Text fontSize="lg" fontWeight="bold" noOfLines={2}>
                  {materialInventory.name}
                </Text>
                <Text fontSize="sm" color="gray.600">
                  SKU: {materialInventory.sku}
                </Text>
                <Badge
                  colorScheme="gray"
                  variant="outline"
                  fontSize="xs"
                  px={2}
                  py={1}
                >
                  {materialInventory.group}
                </Badge>
              </VStack>

              {/* Stock Information */}
              <VStack align="start" spacing={1} w="full">
                <HStack justify="space-between" w="full">
                  <Text fontSize="sm" color="gray.600">Current Stock:</Text>
                  <Text fontSize="sm" fontWeight="bold">
                    {formatStockValue(materialInventory.currentStock)}
                  </Text>
                </HStack>
                <HStack justify="space-between" w="full">
                  <Text fontSize="sm" color="gray.600">Min Stock:</Text>
                  <Text fontSize="sm">
                    {formatStockValue(materialInventory.minAlertStock)}
                  </Text>
                </HStack>
                <HStack justify="space-between" w="full">
                  <Text fontSize="sm" color="gray.600">Max Stock:</Text>
                  <Text fontSize="sm">
                    {formatStockValue(materialInventory.maxStockLevel)}
                  </Text>
                </HStack>
                <HStack justify="space-between" w="full">
                  <Text fontSize="sm" color="gray.600">Status:</Text>
                  <Badge
                    colorScheme={
                      materialInventory.inventoryStatus === 'IN_STOCK' ? 'green' :
                      materialInventory.inventoryStatus === 'OUT_OF_STOCK' ? 'red' :
                      materialInventory.inventoryStatus === 'LOW_STOCK' ? 'orange' :
                      materialInventory.inventoryStatus === 'OVER_STOCK' ? 'purple' : 'gray'
                    }
                    fontSize="xs"
                  >
                    {materialInventory.inventoryStatus}
                  </Badge>
                </HStack>
              </VStack>
            </VStack>
          </Box>

          {/* Column 4: Action Buttons - Vertical layout */}
          {!isSelectionMode && (
            <VStack spacing={2} alignSelf="center">
              <IconButton
                aria-label="Add stock"
                icon={<AddIcon />}
                size="sm"
                colorScheme="green"
                onClick={onAddOpen}
              />
              <IconButton
                aria-label="Subtract stock"
                icon={<MinusIcon />}
                size="sm"
                colorScheme="red"
                onClick={onSubtractOpen}
              />
            </VStack>
          )}
        </Flex>
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
    </>
  );
};

export default MaterialInventoryCard; 