'use client';

import React, { useState, useEffect } from 'react';
import {
  Box,
  Heading,
  VStack,
  Text,
  Center,
  Spinner,
  Badge,
  HStack,
  Button,
  useToast,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
  Card,
  CardBody,
  CardHeader,
  Flex,
  Divider,
  Grid,
  GridItem,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Link,
} from '@chakra-ui/react';
import { getProductInventoryFilter } from '@/services/productInventoryService';
import { getMaterialInventoryFilter } from '@/services/materialInventoryService';
import { useRouter } from 'next/navigation';

const AlarmPage: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [outOfStockProducts, setOutOfStockProducts] = useState<any[]>([]);
  const [lowStockProducts, setLowStockProducts] = useState<any[]>([]);
  const [overStockProducts, setOverStockProducts] = useState<any[]>([]);
  const [outOfStockMaterials, setOutOfStockMaterials] = useState<any[]>([]);
  const [lowStockMaterials, setLowStockMaterials] = useState<any[]>([]);
  const [overStockMaterials, setOverStockMaterials] = useState<any[]>([]);
  const toast = useToast();
  const router = useRouter();

  useEffect(() => {
    fetchAllAlarmData();
  }, []);

  const fetchAllAlarmData = async () => {
    try {
      setLoading(true);
      
      // Fetch all three types of product alarms in parallel
      const [outOfStockProductResponse, lowStockProductResponse, overStockProductResponse] = await Promise.all([
        getProductInventoryFilter({
          inventoryStatus: ['OUT_OF_STOCK'],
          sort: 'updatedAt,desc',
          page: 0,
          size: 50
        }),
        getProductInventoryFilter({
          inventoryStatus: ['LOW_STOCK'],
          sort: 'updatedAt,desc',
          page: 0,
          size: 50
        }),
        getProductInventoryFilter({
          inventoryStatus: ['OVER_STOCK'],
          sort: 'updatedAt,desc',
          page: 0,
          size: 50
        })
      ]);

      // Fetch all three types of material alarms in parallel
      const [outOfStockMaterialResponse, lowStockMaterialResponse, overStockMaterialResponse] = await Promise.all([
        getMaterialInventoryFilter({
          inventoryStatus: ['OUT_OF_STOCK'],
          sort: 'updatedAt,desc',
          page: 0,
          size: 50
        }),
        getMaterialInventoryFilter({
          inventoryStatus: ['LOW_STOCK'],
          sort: 'updatedAt,desc',
          page: 0,
          size: 50
        }),
        getMaterialInventoryFilter({
          inventoryStatus: ['OVER_STOCK'],
          sort: 'updatedAt,desc',
          page: 0,
          size: 50
        })
      ]);

      setOutOfStockProducts(outOfStockProductResponse.content || []);
      setLowStockProducts(lowStockProductResponse.content || []);
      setOverStockProducts(overStockProductResponse.content || []);
      setOutOfStockMaterials(outOfStockMaterialResponse.content || []);
      setLowStockMaterials(lowStockMaterialResponse.content || []);
      setOverStockMaterials(overStockMaterialResponse.content || []);
    } catch (error) {
      console.error('Error fetching alarm data:', error);
      toast({
        title: 'Error',
        description: 'Failed to load alarm data',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const renderProductCard = (product: any, borderColor: string) => (
    <Box
      key={product.id}
      p={4}
      border="1px"
      borderColor={borderColor}
      borderRadius="md"
      bg="white"
      _hover={{ shadow: "md" }}
    >
      <VStack align="start" spacing={3}>
        <HStack justify="space-between" w="100%">
          <VStack align="start" spacing={1}>
            <Text fontWeight="bold" fontSize="lg">
              {product.name}
            </Text>
            <Text fontSize="sm" color="gray.600" mb={2}>
              SKU: {product.sku}
            </Text>
          </VStack>
          <VStack align="end" spacing={1}>
            <Badge colorScheme={borderColor === 'red.200' ? 'red' : borderColor === 'orange.200' ? 'orange' : 'blue'}>
              {product.inventoryStatus}
            </Badge>
            <Text fontSize="sm" color="gray.500">
              {formatDate(product.updatedAt)}
            </Text>
          </VStack>
        </HStack>

        <HStack justify="space-between" w="100%">
          <VStack align="start" spacing={1}>
            <Text fontSize="sm">
              <strong>Current Stock:</strong> {product.currentStock}
            </Text>
            <Text fontSize="sm">
              <strong>Min Alert:</strong> {product.minAlertStock}
            </Text>
            <Text fontSize="sm">
              <strong>Max Level:</strong> {product.maxStockLevel}
            </Text>
          </VStack>
          <Link
            color="blue.500"
            textDecoration="underline"
            cursor="pointer"
            onClick={() => {
              const singleVariantData = encodeURIComponent(JSON.stringify([product]));
              router.push(`/production/create?lowStockData=${singleVariantData}`);
            }}
            _hover={{ color: 'blue.700' }}
            fontSize="sm"
            fontWeight="medium"
          >
            Create production
          </Link>
        </HStack>
      </VStack>
    </Box>
  );

  const renderMaterialCard = (material: any, borderColor: string) => (
    <Box
      key={material.id}
      p={4}
      border="1px"
      borderColor={borderColor}
      borderRadius="md"
      bg="white"
      _hover={{ shadow: "md" }}
    >
      <VStack align="start" spacing={3}>
        <HStack justify="space-between" w="100%">
          <VStack align="start" spacing={1}>
            <Text fontWeight="bold" fontSize="lg">
              {material.name}
            </Text>
            <Text fontSize="sm" color="gray.600">
              SKU: {material.sku}
            </Text>
            {material.group && (
              <Text fontSize="sm" color="gray.500">
                Group: {material.group}
              </Text>
            )}
          </VStack>
          <VStack align="end" spacing={1}>
            <Badge colorScheme={borderColor === 'red.200' ? 'red' : borderColor === 'orange.200' ? 'orange' : 'blue'}>
              {material.inventoryStatus}
            </Badge>
            <Text fontSize="sm" color="gray.500">
              {formatDate(material.updatedAt)}
            </Text>
          </VStack>
        </HStack>

        <HStack justify="space-between" w="100%">
          <VStack align="start" spacing={1}>
            <Text fontSize="sm">
              <strong>Current Stock:</strong> {parseFloat(material.currentStock || '0')}
            </Text>
            {material.minAlertStock && (
              <Text fontSize="sm">
                <strong>Min Alert:</strong> {parseFloat(material.minAlertStock)}
              </Text>
            )}
            {material.maxStockLevel && (
              <Text fontSize="sm">
                <strong>Max Level:</strong> {parseFloat(material.maxStockLevel)}
              </Text>
            )}
          </VStack>
          <Link
            color="blue.500"
            textDecoration="underline"
            cursor="pointer"
            onClick={() => {
              const singleMaterialData = encodeURIComponent(JSON.stringify([material]));
              router.push(`/purchasing/create?lowStockData=${singleMaterialData}`);
            }}
            _hover={{ color: 'blue.700' }}
            fontSize="sm"
            fontWeight="medium"
          >
            Create purchasing
          </Link>
        </HStack>
      </VStack>
    </Box>
  );

  const renderProductAlarms = () => (
    <Grid templateColumns="repeat(1, 1fr)" gap={6}>
      {/* Section 1: OUT OF STOCK */}
      <GridItem>
        <Card variant="outline">
          <CardHeader bg="red.50" borderBottom="1px" borderColor="red.200">
            <Flex justify="space-between" align="center">
              <Heading size="md" color="red.700">
                OUT OF STOCK
              </Heading>
              <HStack spacing={4}>
                {outOfStockProducts.length > 0 && (
                  <Link
                    color="blue.500"
                    textDecoration="underline"
                    cursor="pointer"
                    onClick={() => {
                      const outOfStockData = encodeURIComponent(JSON.stringify(outOfStockProducts));
                      router.push(`/production/create?lowStockData=${outOfStockData}`);
                    }}
                    _hover={{ color: 'blue.700' }}
                    fontSize="sm"
                    fontWeight="medium"
                  >
                    Create production
                  </Link>
                )}
                <Badge colorScheme="red" fontSize="md">
                  {outOfStockProducts.length} items
                </Badge>
              </HStack>
            </Flex>
          </CardHeader>
          <CardBody>
            {outOfStockProducts.length === 0 ? (
              <Center minHeight="200px">
                <Alert status="success">
                  <AlertIcon />
                  <AlertTitle>No Out of Stock Items!</AlertTitle>
                  <AlertDescription>
                    All products are currently in stock.
                  </AlertDescription>
                </Alert>
              </Center>
            ) : (
              <VStack spacing={4} align="stretch">
                {outOfStockProducts.map((product) => renderProductCard(product, 'red.200'))}
              </VStack>
            )}
          </CardBody>
        </Card>
      </GridItem>

      {/* Section 2: LOW STOCK */}
      <GridItem>
        <Card variant="outline">
          <CardHeader bg="orange.50" borderBottom="1px" borderColor="orange.200">
            <Flex justify="space-between" align="center">
              <Heading size="md" color="orange.700">
                LOW STOCK
              </Heading>
              <HStack spacing={4}>
                {lowStockProducts.length > 0 && (
                  <Link
                    color="blue.500"
                    textDecoration="underline"
                    cursor="pointer"
                    onClick={() => {
                      const lowStockData = encodeURIComponent(JSON.stringify(lowStockProducts));
                      router.push(`/production/create?lowStockData=${lowStockData}`);
                    }}
                    _hover={{ color: 'blue.700' }}
                    fontSize="sm"
                    fontWeight="medium"
                  >
                    Create production
                  </Link>
                )}
                <Badge colorScheme="orange" fontSize="md">
                  {lowStockProducts.length} items
                </Badge>
              </HStack>
            </Flex>
          </CardHeader>
          <CardBody>
            {lowStockProducts.length === 0 ? (
              <Center minHeight="200px">
                <Alert status="success">
                  <AlertIcon />
                  <AlertTitle>No Low Stock Items!</AlertTitle>
                  <AlertDescription>
                    All products have sufficient stock levels.
                  </AlertDescription>
                </Alert>
              </Center>
            ) : (
              <VStack spacing={4} align="stretch">
                {lowStockProducts.map((product) => renderProductCard(product, 'orange.200'))}
              </VStack>
            )}
          </CardBody>
        </Card>
      </GridItem>

      {/* Section 3: OVER STOCK */}
      <GridItem>
        <Card variant="outline">
          <CardHeader bg="blue.50" borderBottom="1px" borderColor="blue.200">
            <Flex justify="space-between" align="center">
              <Heading size="md" color="blue.700">
                OVER STOCK
              </Heading>
              <Badge colorScheme="blue" fontSize="md">
                {overStockProducts.length} items
              </Badge>
            </Flex>
          </CardHeader>
          <CardBody>
            {overStockProducts.length === 0 ? (
              <Center minHeight="200px">
                <Alert status="success">
                  <AlertIcon />
                  <AlertTitle>No Over Stock Items!</AlertTitle>
                  <AlertDescription>
                    All products have optimal stock levels.
                  </AlertDescription>
                </Alert>
              </Center>
            ) : (
              <VStack spacing={4} align="stretch">
                {overStockProducts.map((product) => renderProductCard(product, 'blue.200'))}
              </VStack>
            )}
          </CardBody>
        </Card>
      </GridItem>
    </Grid>
  );

  const renderMaterialAlarms = () => (
    <Grid templateColumns="repeat(1, 1fr)" gap={6}>
      {/* Section 1: OUT OF STOCK */}
      <GridItem>
        <Card variant="outline">
          <CardHeader bg="red.50" borderBottom="1px" borderColor="red.200">
            <Flex justify="space-between" align="center">
              <Heading size="md" color="red.700">
                OUT OF STOCK
              </Heading>
              <HStack spacing={4}>
                {outOfStockMaterials.length > 0 && (
                  <Link
                    color="blue.500"
                    textDecoration="underline"
                    cursor="pointer"
                    onClick={() => {
                      const outOfStockMaterialData = encodeURIComponent(JSON.stringify(outOfStockMaterials));
                      router.push(`/purchasing/create?lowStockData=${outOfStockMaterialData}`);
                    }}
                    _hover={{ color: 'blue.700' }}
                    fontSize="sm"
                    fontWeight="medium"
                  >
                    Create purchasing
                  </Link>
                )}
                <Badge colorScheme="red" fontSize="md">
                  {outOfStockMaterials.length} items
                </Badge>
              </HStack>
            </Flex>
          </CardHeader>
          <CardBody>
            {outOfStockMaterials.length === 0 ? (
              <Center minHeight="200px">
                <Alert status="success">
                  <AlertIcon />
                  <AlertTitle>No Out of Stock Items!</AlertTitle>
                  <AlertDescription>
                    All materials are currently in stock.
                  </AlertDescription>
                </Alert>
              </Center>
            ) : (
              <VStack spacing={4} align="stretch">
                {outOfStockMaterials.map((material) => renderMaterialCard(material, 'red.200'))}
              </VStack>
            )}
          </CardBody>
        </Card>
      </GridItem>

      {/* Section 2: LOW STOCK */}
      <GridItem>
        <Card variant="outline">
          <CardHeader bg="orange.50" borderBottom="1px" borderColor="orange.200">
            <Flex justify="space-between" align="center">
              <Heading size="md" color="orange.700">
                LOW STOCK
              </Heading>
              <HStack spacing={4}>
                {lowStockMaterials.length > 0 && (
                  <Link
                    color="blue.500"
                    textDecoration="underline"
                    cursor="pointer"
                    onClick={() => {
                      const lowStockMaterialData = encodeURIComponent(JSON.stringify(lowStockMaterials));
                      router.push(`/purchasing/create?lowStockData=${lowStockMaterialData}`);
                    }}
                    _hover={{ color: 'blue.700' }}
                    fontSize="sm"
                    fontWeight="medium"
                  >
                    Create purchasing
                  </Link>
                )}
                <Badge colorScheme="orange" fontSize="md">
                  {lowStockMaterials.length} items
                </Badge>
              </HStack>
            </Flex>
          </CardHeader>
          <CardBody>
            {lowStockMaterials.length === 0 ? (
              <Center minHeight="200px">
                <Alert status="success">
                  <AlertIcon />
                  <AlertTitle>No Low Stock Items!</AlertTitle>
                  <AlertDescription>
                    All materials have sufficient stock levels.
                  </AlertDescription>
                </Alert>
              </Center>
            ) : (
              <VStack spacing={4} align="stretch">
                {lowStockMaterials.map((material) => renderMaterialCard(material, 'orange.200'))}
              </VStack>
            )}
          </CardBody>
        </Card>
      </GridItem>

      {/* Section 3: OVER STOCK */}
      <GridItem>
        <Card variant="outline">
          <CardHeader bg="blue.50" borderBottom="1px" borderColor="blue.200">
            <Flex justify="space-between" align="center">
              <Heading size="md" color="blue.700">
                OVER STOCK
              </Heading>
              <Badge colorScheme="blue" fontSize="md">
                {overStockMaterials.length} items
              </Badge>
            </Flex>
          </CardHeader>
          <CardBody>
            {overStockMaterials.length === 0 ? (
              <Center minHeight="200px">
                <Alert status="success">
                  <AlertIcon />
                  <AlertTitle>No Over Stock Items!</AlertTitle>
                  <AlertDescription>
                    All materials have optimal stock levels.
                  </AlertDescription>
                </Alert>
              </Center>
            ) : (
              <VStack spacing={4} align="stretch">
                {overStockMaterials.map((material) => renderMaterialCard(material, 'blue.200'))}
              </VStack>
            )}
          </CardBody>
        </Card>
      </GridItem>
    </Grid>
  );

  if (loading) {
    return (
      <Center minHeight="400px">
        <Spinner size="xl" />
      </Center>
    );
  }

  return (
    <Box maxW="1200px" mx="auto" p={6}>
      <Heading as="h1" size="xl" mb={6}>
        Inventory Alarms
      </Heading>

      <Tabs variant="enclosed" colorScheme="blue">
        <TabList>
          <Tab>Product Alarms</Tab>
          <Tab>Material Alarms</Tab>
        </TabList>

        <TabPanels>
          <TabPanel>
            {renderProductAlarms()}
          </TabPanel>
          <TabPanel>
            {renderMaterialAlarms()}
          </TabPanel>
        </TabPanels>
      </Tabs>
    </Box>
  );
};

export default AlarmPage;
