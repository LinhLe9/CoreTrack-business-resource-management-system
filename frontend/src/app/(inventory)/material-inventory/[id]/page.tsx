'use client';

import React, { useState, useEffect } from 'react';
import {
  Box,
  Heading,
  VStack,
  HStack,
  Text,
  Spinner,
  useToast,
  Card,
  CardBody,
  Badge,
  Divider,
  Flex,
  Image,
  Grid,
  GridItem,
  Container,
} from '@chakra-ui/react';
import { useParams, useRouter } from 'next/navigation';
import { getMaterialInventoryById } from '../../../../services/materialInventoryService';
import { formatBigDecimal } from '../../../../lib/utils';
import { MaterialInventoryDetailResponse } from '../../../../types/materialInventory';
import { InventoryTransactionResponse } from '../../../../types/inventory';

const MaterialInventoryDetailPage: React.FC = () => {
  const [materialInventory, setMaterialInventory] = useState<MaterialInventoryDetailResponse | null>(null);
  const [transactions, setTransactions] = useState<InventoryTransactionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const toast = useToast();
  const params = useParams();
  const router = useRouter();
  const variantId = params.id as string;

  useEffect(() => {
    const fetchMaterialInventoryDetail = async () => {
      if (!variantId) return;

      setLoading(true);
      setError(null);

      try {
        // Fetch material inventory detail (includes logs)
        const detailResponse = await getMaterialInventoryById(parseInt(variantId));
        setMaterialInventory(detailResponse);
        setTransactions(detailResponse.transactions || []);
      } catch (err: any) {
        console.error('Error fetching material inventory detail:', err);
        setError('Failed to load material inventory details.');
        
        if (err.response?.status === 404) {
          toast({
            title: 'Not Found',
            description: 'Material inventory not found.',
            status: 'error',
            duration: 5000,
            isClosable: true,
          });
          router.push('/material-inventory');
        } else {
          toast({
            title: 'Error',
            description: err.response?.data?.message || 'Failed to load material inventory details.',
            status: 'error',
            duration: 5000,
            isClosable: true,
          });
        }
      } finally {
        setLoading(false);
      }
    };

    fetchMaterialInventoryDetail();
  }, [variantId, toast, router]);

  const getInventoryStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'in_stock':
        return 'green';
      case 'low_stock':
        return 'orange';
      case 'out_of_stock':
        return 'red';
      case 'over_stock':
        return 'purple';
      default:
        return 'gray';
    }
  };

  const getInventoryStatusDisplayName = (status: string) => {
    switch (status.toLowerCase()) {
      case 'in_stock':
        return 'In Stock';
      case 'low_stock':
        return 'Low Stock';
      case 'out_of_stock':
        return 'Out of Stock';
      case 'over_stock':
        return 'Over Stock';
      default:
        return status;
    }
  };

  const getTransactionTypeColor = (type: string) => {
    switch (type.toLowerCase()) {
      case 'add':
        return 'green';
      case 'subtract':
        return 'red';
      case 'set':
        return 'blue';
      default:
        return 'gray';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <Box p={8} display="flex" justifyContent="center" alignItems="center" minH="400px">
        <Spinner size="xl" color="teal.500" />
      </Box>
    );
  }

  if (error || !materialInventory) {
    return (
      <Box p={8} textAlign="center">
        <Text fontSize="lg" color="red.500">{error || 'Material inventory not found'}</Text>
      </Box>
    );
  }

  return (
    <Container maxW="1200px" p={8}>
      <VStack spacing={6} align="stretch">
        {/* Header - Material Variant Name */}
        <Box textAlign="center">
          <Heading as="h1" size="xl" color="teal.700" mb={2}>
            {materialInventory.materialVariantName || materialInventory.materialName}
          </Heading>
        </Box>

        {/* Material Information and Image */}
        <Card>
          <CardBody>
            <Grid templateColumns={{ base: '1fr', md: '1fr 200px' }} gap={6}>
              {/* Left Side - Information */}
              <GridItem>
                <VStack align="start" spacing={4}>
                  <Box>
                    <Text fontWeight="bold" fontSize="lg" mb={2}>Material Information</Text>
                    <VStack align="start" spacing={2}>
                      <Text><strong>Material Name:</strong> {materialInventory.materialName}</Text>
                      <Text><strong>Material SKU:</strong> {materialInventory.materialSku}</Text>
                      <Text><strong>Variant SKU:</strong> {materialInventory.materialVariantSku}</Text>
                      {materialInventory.materialVariantName && (
                        <Text><strong>Variant Name:</strong> {materialInventory.materialVariantName}</Text>
                      )}
                    </VStack>
                  </Box>

                  <Divider />

                  <Box>
                    <Text fontWeight="bold" fontSize="lg" mb={2}>Stock Information</Text>
                    <VStack align="start" spacing={2}>
                      <HStack>
                        <Text><strong>Current Stock:</strong></Text>
                        <Text fontSize="lg" fontWeight="bold" color="blue.600">
                          {formatBigDecimal(materialInventory.currentStock)}
                        </Text>
                      </HStack>
                      <Text><strong>Allocated Stock:</strong> {formatBigDecimal(materialInventory.allocatedStock)}</Text>
                      <Text><strong>Future Stock:</strong> {formatBigDecimal(materialInventory.futureStock)}</Text>
                      <Text><strong>Min Alert Stock:</strong> {formatBigDecimal(materialInventory.minAlertStock)}</Text>
                      <Text><strong>Max Stock Level:</strong> {formatBigDecimal(materialInventory.maxStockLevel)}</Text>
                      <HStack>
                        <Text><strong>Status:</strong></Text>
                        <Badge colorScheme={getInventoryStatusColor(materialInventory.inventoryStatus)}>
                          {getInventoryStatusDisplayName(materialInventory.inventoryStatus)}
                        </Badge>
                      </HStack>
                      <HStack>
                        <Text><strong>Material Status:</strong></Text>
                        <Badge colorScheme={materialInventory.materialStatus === 'ACTIVE' ? 'green' : 'red'}>
                          {materialInventory.materialStatus}
                        </Badge>
                      </HStack>
                    </VStack>
                  </Box>
                </VStack>
              </GridItem>

              {/* Right Side - Image */}
              <GridItem display="flex" justifyContent="center" alignItems="start">
                <Image
                  src={materialInventory.imageUrl || '/default-product.jpg'}
                  alt={materialInventory.materialName}
                  boxSize="150px"
                  objectFit="contain"
                  borderRadius="md"
                />
              </GridItem>
            </Grid>
          </CardBody>
        </Card>

        {/* Transaction History */}
        <Card>
          <CardBody>
            <Text fontWeight="bold" fontSize="lg" mb={4}>Transaction History</Text>
            
            {transactions.length === 0 ? (
              <Text color="gray.500" textAlign="center" py={8}>
                No transaction history available
              </Text>
            ) : (
              <Box maxH="400px" overflowY="auto">
                <VStack spacing={3} align="stretch">
                  {transactions.map((transaction) => (
                    <Card key={transaction.id} size="sm" variant="outline">
                      <CardBody>
                        <Grid templateColumns={{ base: '1fr', md: '2fr 1fr 1fr 1fr 1fr' }} gap={4}>
                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <HStack>
                                <Badge colorScheme={getTransactionTypeColor(transaction.transactionType)}>
                                  {transaction.transactionType}
                                </Badge>
                                {transaction.stockType && (
                                  <Badge colorScheme="blue" variant="outline">
                                    {transaction.stockType}
                                  </Badge>
                                )}
                                {transaction.referenceDocumentType && (
                                  <Badge colorScheme="purple" variant="outline">
                                    {transaction.referenceDocumentType}
                                  </Badge>
                                )}
                              </HStack>
                              <Text fontSize="sm" color="gray.600">
                                {formatDate(transaction.createdAt)}
                              </Text>
                              {transaction.note && (
                                <Text fontSize="sm" color="gray.700" noOfLines={2}>
                                  {transaction.note}
                                </Text>
                              )}
                            </VStack>
                          </GridItem>

                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <Text fontSize="sm" color="gray.600">Quantity</Text>
                              <Text fontWeight="bold" color={getTransactionTypeColor(transaction.transactionType)}>
                                {formatBigDecimal(transaction.quantity)}
                              </Text>
                            </VStack>
                          </GridItem>

                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <Text fontSize="sm" color="gray.600">Previous Stock</Text>
                              <Text>{formatBigDecimal(transaction.previousStock)}</Text>
                            </VStack>
                          </GridItem>

                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <Text fontSize="sm" color="gray.600">New Stock</Text>
                              <Text fontWeight="bold">{formatBigDecimal(transaction.newStock)}</Text>
                            </VStack>
                          </GridItem>
                        </Grid>

                        {transaction.createdBy && (
                          <Text fontSize="xs" color="gray.500" mt={2}>
                            By: {transaction.createdBy} ({transaction.user_role})
                          </Text>
                        )}
                      </CardBody>
                    </Card>
                  ))}
                </VStack>
              </Box>
            )}
          </CardBody>
        </Card>
      </VStack>
    </Container>
  );
};

export default MaterialInventoryDetailPage; 