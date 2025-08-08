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
  Button,
  IconButton,
  Center,
  Alert,
  AlertIcon,
} from '@chakra-ui/react';
import { EditIcon } from '@chakra-ui/icons';
import { useParams, useRouter } from 'next/navigation';
import { getMaterialInventoryById } from '../../../../services/materialInventoryService';
import { formatBigDecimal } from '../../../../lib/utils';
import StockTransactionModal from '../../../../components/inventory/StockTransactionModal';
import SetMinMaxModal from '../../../../components/inventory/SetMinMaxModal';
import { MaterialInventoryDetailResponse } from '../../../../types/materialInventory';
import { InventoryTransactionResponse } from '../../../../types/inventory';
import { useUser } from '../../../../hooks/useUser';

const MaterialInventoryDetailPage: React.FC = () => {
  const [materialInventory, setMaterialInventory] = useState<MaterialInventoryDetailResponse | null>(null);
  const [transactions, setTransactions] = useState<InventoryTransactionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSetModalOpen, setIsSetModalOpen] = useState(false);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isSubtractModalOpen, setIsSubtractModalOpen] = useState(false);
  const [isMinModalOpen, setIsMinModalOpen] = useState(false);
  const [isMaxModalOpen, setIsMaxModalOpen] = useState(false);
  const toast = useToast();
  const params = useParams();
  const router = useRouter();
  const variantId = params.id as string;
  const { user, isOwner, isWarehouseStaff } = useUser();

  useEffect(() => {
    const fetchMaterialInventoryDetail = async () => {
      if (!variantId) return;

      setLoading(true);
      setError(null);

      try {
        // Fetch material inventory detail (includes logs)
        const detailResponse = await getMaterialInventoryById(parseInt(variantId));
        console.log('=== FRONTEND DEBUG ===');
        console.log('Detail Response:', detailResponse);
        console.log('Current Stock:', detailResponse.currentStock);
        console.log('Current Stock Type:', typeof detailResponse.currentStock);
        console.log('Current Stock Value:', detailResponse.currentStock);
        console.log('======================');
        setMaterialInventory(detailResponse);
        setTransactions(detailResponse.logs || []);
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
        }
      } finally {
        setLoading(false);
      }
    };

    fetchMaterialInventoryDetail();
  }, [variantId, toast]);

  const getInventoryStatusColor = (status: string) => {
    switch (status) {
      case 'IN_STOCK':
        return 'green';
      case 'LOW_STOCK':
        return 'yellow';
      case 'OUT_OF_STOCK':
        return 'red';
      case 'OVER_STOCK':
        return 'orange';
      default:
        return 'gray';
    }
  };

  const getInventoryStatusDisplayName = (status: string) => {
    switch (status) {
      case 'IN_STOCK':
        return 'In Stock';
      case 'LOW_STOCK':
        return 'Low Stock';
      case 'OUT_OF_STOCK':
        return 'Out of Stock';
      case 'OVER_STOCK':
        return 'Over Stock';
      default:
        return status;
    }
  };

  const getTransactionTypeColor = (type: string) => {
    switch (type) {
      case 'IN':
        return 'green';
      case 'OUT':
        return 'red';
      case 'SET':
        return 'blue';
      default:
        return 'gray';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  const handleStockSuccess = () => {
    // Refresh the data after successful stock operation
    window.location.reload();
  };

  if (loading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  if (error || !materialInventory) {
    return (
      <Center minHeight="300px">
        <Text color="red.500" fontSize="lg">
          {error || 'Material inventory not found.'}
        </Text>
      </Center>
    );
  }

  return (
    <Container maxW="6xl" py={8}>
      <VStack spacing={6} align="stretch">
        <Heading size="lg" mb={4}>Material Inventory Detail</Heading>

        {/* Material Information Card */}
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
                      <HStack>
                        <Text><strong>Min Alert Stock:</strong> {formatBigDecimal(materialInventory.minAlertStock)}</Text>
                        {(isOwner() || isWarehouseStaff()) && (
                          <IconButton
                            aria-label="Edit minimum alert stock"
                            icon={<EditIcon />}
                            size="sm"
                            variant="ghost"
                            colorScheme="blue"
                            onClick={() => setIsMinModalOpen(true)}
                          />
                        )}
                      </HStack>
                      <HStack>
                        <Text><strong>Max Stock Level:</strong> {formatBigDecimal(materialInventory.maxStockLevel)}</Text>
                        {(isOwner() || isWarehouseStaff()) && (
                          <IconButton
                            aria-label="Edit maximum stock level"
                            icon={<EditIcon />}
                            size="sm"
                            variant="ghost"
                            colorScheme="blue"
                            onClick={() => setIsMaxModalOpen(true)}
                          />
                        )}
                      </HStack>
                      <HStack>
                        <Text><strong>Status:</strong></Text>
                        <Badge colorScheme={getInventoryStatusColor(materialInventory.inventoryStatus)}>
                          {getInventoryStatusDisplayName(materialInventory.inventoryStatus)}
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
                              </HStack>
                              <Text fontSize="sm" color="gray.600">
                                {transaction.note || 'No note provided'}
                              </Text>
                              <Text fontSize="xs" color="gray.500">
                                {transaction.createdBy && `By: ${transaction.createdBy}`}
                              </Text>
                            </VStack>
                          </GridItem>
                          
                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <Text fontSize="sm" fontWeight="bold">Quantity</Text>
                              <Text fontSize="sm">{formatBigDecimal(transaction.quantity)}</Text>
                            </VStack>
                          </GridItem>
                          
                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <Text fontSize="sm" fontWeight="bold">Previous</Text>
                              <Text fontSize="sm">{formatBigDecimal(transaction.previousStock)}</Text>
                            </VStack>
                          </GridItem>
                          
                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <Text fontSize="sm" fontWeight="bold">New Stock</Text>
                              <Text fontSize="sm">{formatBigDecimal(transaction.newStock)}</Text>
                            </VStack>
                          </GridItem>
                          
                          <GridItem>
                            <VStack align="start" spacing={1}>
                              <Text fontSize="sm" fontWeight="bold">Date</Text>
                              <Text fontSize="sm">{formatDate(transaction.createdAt)}</Text>
                            </VStack>
                          </GridItem>
                        </Grid>
                      </CardBody>
                    </Card>
                  ))}
                </VStack>
              </Box>
            )}
          </CardBody>
        </Card>

        {/* Stock Operations Card */}
        <Card>
          <CardBody>
            <Text fontWeight="bold" fontSize="lg" mb={4} textAlign="center">Stock Action</Text>
            <VStack spacing={4}>
              
              {/* Role-based access control */}
              {(isOwner() || isWarehouseStaff()) ? (
                <HStack spacing={4} flexWrap="wrap" justify="center">
                  <Button
                    variant="outline"
                    colorScheme="blue"
                    borderWidth="2px"
                    borderColor="blue.600"
                    bg="white"
                    _hover={{ bg: 'blue.50' }}
                    onClick={() => setIsSetModalOpen(true)}
                  >
                    Set Stock
                  </Button>
                  <Button
                    variant="outline"
                    colorScheme="green"
                    borderWidth="2px"
                    borderColor="green.600"
                    bg="white"
                    _hover={{ bg: 'green.50' }}
                    onClick={() => setIsAddModalOpen(true)}
                  >
                    Add Stock
                  </Button>
                  <Button
                    variant="outline"
                    colorScheme="red"
                    borderWidth="2px"
                    borderColor="red.600"
                    bg="white"
                    _hover={{ bg: 'red.50' }}
                    onClick={() => setIsSubtractModalOpen(true)}
                  >
                    Subtract Stock
                  </Button>
                </HStack>
              ) : (
                <Alert status="info">
                  <AlertIcon />
                  Only OWNER and WAREHOUSE_STAFF can perform stock actions.
                </Alert>
              )}
            </VStack>
          </CardBody>
        </Card>
      </VStack>

      {/* Set Stock Modal */}
      <StockTransactionModal
        isOpen={isSetModalOpen}
        onClose={() => setIsSetModalOpen(false)}
        variantId={parseInt(variantId)}
        variantSku={materialInventory.materialVariantSku}
        variantName={materialInventory.materialVariantName || materialInventory.materialName}
        transactionType="set"
        onSuccess={handleStockSuccess}
      />

      {/* Add Stock Modal */}
      <StockTransactionModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        variantId={parseInt(variantId)}
        variantSku={materialInventory.materialVariantSku}
        variantName={materialInventory.materialVariantName || materialInventory.materialName}
        transactionType="add"
        onSuccess={handleStockSuccess}
      />

      {/* Subtract Stock Modal */}
      <StockTransactionModal
        isOpen={isSubtractModalOpen}
        onClose={() => setIsSubtractModalOpen(false)}
        variantId={parseInt(variantId)}
        variantSku={materialInventory.materialVariantSku}
        variantName={materialInventory.materialVariantName || materialInventory.materialName}
        transactionType="subtract"
        onSuccess={handleStockSuccess}
      />

      {/* Set Minimum Alert Stock Modal */}
      <SetMinMaxModal
        isOpen={isMinModalOpen}
        onClose={() => setIsMinModalOpen(false)}
        variantId={parseInt(variantId)}
        variantSku={materialInventory.materialVariantSku}
        variantName={materialInventory.materialVariantName || materialInventory.materialName}
        type="minimum"
        currentValue={materialInventory.minAlertStock}
        onSuccess={handleStockSuccess}
        serviceType="material"
      />

      {/* Set Maximum Stock Level Modal */}
      <SetMinMaxModal
        isOpen={isMaxModalOpen}
        onClose={() => setIsMaxModalOpen(false)}
        variantId={parseInt(variantId)}
        variantSku={materialInventory.materialVariantSku}
        variantName={materialInventory.materialVariantName || materialInventory.materialName}
        type="maximum"
        currentValue={materialInventory.maxStockLevel}
        onSuccess={handleStockSuccess}
        serviceType="material"
      />
    </Container>
  );
};

export default MaterialInventoryDetailPage; 