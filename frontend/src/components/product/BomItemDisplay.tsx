'use client';

import React, { useEffect, useState } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Badge,
  Spinner,
  Alert,
  AlertIcon,
  Button,
  useToast,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableContainer,
} from '@chakra-ui/react';
import { BOMItemResponse } from '../../types/product';
import { getBomItem } from '../../services/productService';

interface BomItemDisplayProps {
  productId: number;
  variantId: number;
  variantName?: string;
}

const BomItemDisplay: React.FC<BomItemDisplayProps> = ({ 
  productId, 
  variantId, 
  variantName = 'Product Variant' 
}) => {
  const [bomItems, setBomItems] = useState<BOMItemResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const toast = useToast();

  const fetchBomItems = async () => {
    setLoading(true);
    setError(null);
    try {
      const items = await getBomItem(productId, variantId);
      setBomItems(items);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch BOM items';
      setError(errorMessage);
      toast({
        title: 'Error',
        description: errorMessage,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (productId && variantId) {
      fetchBomItems();
    }
  }, [productId, variantId]);

  if (loading) {
    return (
      <Box p={4}>
        <HStack justify="center" spacing={4}>
          <Spinner size="md" />
          <Text>Loading BOM items...</Text>
        </HStack>
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={4}>
        <Alert status="error">
          <AlertIcon />
          {error}
          <Button size="sm" ml={2} onClick={fetchBomItems}>
            Retry
          </Button>
        </Alert>
      </Box>
    );
  }

  return (
    <Box p={4} border="1px" borderColor="gray.200" borderRadius="md">
      <VStack spacing={4} align="stretch">
        <HStack justify="space-between">
          <Text fontWeight="bold" fontSize="lg">
            BOM Items for {variantName}
          </Text>
          <Badge colorScheme="blue">
            {bomItems.length} items
          </Badge>
        </HStack>

        {bomItems.length === 0 ? (
          <Text color="gray.500" textAlign="center" py={4}>
            No BOM items found for this variant
          </Text>
        ) : (
          <TableContainer>
            <Table variant="simple" size="sm">
              <Thead>
                <Tr>
                  <Th>Material SKU</Th>
                  <Th>Material Name</Th>
                  <Th>Quantity</Th>
                  <Th>UOM</Th>
                </Tr>
              </Thead>
              <Tbody>
                {bomItems.map((item) => (
                  <Tr key={item.id}>
                    <Td>
                      <Text fontWeight="medium" fontSize="sm">
                        {item.materialSku}
                      </Text>
                    </Td>
                    <Td>
                      <Text fontSize="sm">
                        {item.materialName}
                      </Text>
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

        <HStack justify="end" spacing={2}>
          <Text fontSize="xs" color="gray.500">
            Product ID: {productId} | Variant ID: {variantId}
          </Text>
          <Button size="sm" variant="outline" onClick={fetchBomItems}>
            Refresh
          </Button>
        </HStack>
      </VStack>
    </Box>
  );
};

export default BomItemDisplay; 