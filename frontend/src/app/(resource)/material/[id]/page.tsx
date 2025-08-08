"use client";

import { useRouter, useParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import { formatBigDecimal } from '@/lib/utils';
import {
  Box,
  Heading,
  Text,
  Spinner,
  Center,
  Image,
  VStack,
  Divider,
  Badge,
  HStack,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableContainer,
  Button,
  Flex,
  useToast,
} from '@chakra-ui/react';
import { ChevronLeftIcon } from '@chakra-ui/icons';
import { getMaterialById, deleteMaterial } from '@/services/materialService';
import { MaterialDetailResponse } from '@/types/material';
import MaterialStatusMenu from '@/components/material/MaterialStatusMenu';
import { useUser } from '@/hooks/useUser';

const MaterialDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const id = params?.id;
  const toast = useToast();
  const { isOwner } = useUser();
  
  const [material, setMaterial] = useState<MaterialDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState<boolean>(false);

  const fetchMaterial = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await getMaterialById(Number(id));
      setMaterial(data);
    } catch (err: any) {
      setError('Failed to load material detail.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMaterial();
  }, [id]);

  const handleDelete = async () => {
    if (!id || !material) return;
    
    if (!confirm('Are you sure you want to delete this material? This action cannot be undone.')) {
      return;
    }

    setDeleting(true);
    try {
      await deleteMaterial(Number(id));
      toast({
        title: 'Material deleted successfully!',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
      router.push('/material');
    } catch (err: any) {
      console.error('Error deleting material:', err);
      toast({
        title: 'Failed to delete material',
        description: err.response?.data?.message || 'Please try again.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  if (error || !material) {
    return (
      <Center minHeight="300px">
        <Text color="red.500" fontSize="lg">
          {error || 'Material not found.'}
        </Text>
      </Center>
    );
  }

  return (
    <Box maxW="900px" mx="auto" p={6}>
      <Flex justify="space-between" align="center" mb={4}>
        <Heading as="h2" size="xl">
          {material.name}
        </Heading>
        <MaterialStatusMenu 
          material={{
            id: material.id,
            sku: material.sku,
            name: material.name,
            shortDes: material.shortDes,
            groupMaterial: material.groupMaterial,
            status: material.status as any,
            uom: material.uom,
            imageUrl: material.imageUrl,
          }}
          onStatusChange={() => {
            // Refresh the material data after status change
            fetchMaterial();
          }}
        />
      </Flex>

      <HStack align="start" spacing={6} mb={6}>
        <VStack align="start" spacing={4} flex={1}>
          <Text><strong>SKU:</strong> {material.sku}</Text>
          <Text><strong>Group:</strong> {material.groupMaterial}</Text>
          <Text><strong>Description:</strong> {material.shortDes}</Text>
          <Text><strong>UOM:</strong> {material.uom}</Text>
          <Badge colorScheme={getStatusColor(material.status)}>
            {material.status}
          </Badge>
        </VStack>
        
        {material.imageUrl && (
          <Image 
            src={material.imageUrl} 
            alt={material.name} 
            boxSize="300px" 
            objectFit="cover" 
            borderRadius="md" 
          />
        )}
      </HStack>

      <Divider my={6} />

      {/* Variants Section */}
      {material.variants && material.variants.length > 0 && (
        <>
          <Heading as="h3" size="md" mb={4}>
            Variants
          </Heading>
          <TableContainer>
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th>SKU</Th>
                  <Th>Name</Th>
                  <Th>Description</Th>
                  <Th>Image</Th>
                  <Th>Current Stock</Th>
                  <Th>Min Alert</Th>
                  <Th>Max Stock</Th>
                </Tr>
              </Thead>
              <Tbody>
                {material.variants.map((variant, index) => (
                  <Tr key={index}>
                    <Td>{variant.materialVariantResponse.sku}</Td>
                    <Td>{variant.materialVariantResponse.name}</Td>
                    <Td>{variant.materialVariantResponse.shortDescription}</Td>
                    <Td>
                      {variant.materialVariantResponse.imageUrl && (
                        <Image 
                          src={variant.materialVariantResponse.imageUrl} 
                          alt={variant.materialVariantResponse.name}
                          boxSize="50px"
                          objectFit="cover"
                          borderRadius="sm"
                        />
                      )}
                    </Td>
                    <Td>{formatBigDecimal(variant.inventoryResponse?.currentStock)}</Td>
                    <Td>{formatBigDecimal(variant.inventoryResponse?.minAlertStock)}</Td>
                    <Td>{formatBigDecimal(variant.inventoryResponse?.maxStockLevel)}</Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        </>
      )}

      <Divider my={6} />

      {/* Suppliers Section */}
      {material.suppliers && material.suppliers.length > 0 && (
        <>
          <Heading as="h3" size="md" mb={4}>
            Suppliers
          </Heading>
          <TableContainer>
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th>Supplier Name</Th>
                  <Th>Price</Th>
                  <Th>Currency</Th>
                  <Th>Lead Time (Days)</Th>
                  <Th>Min Order Qty</Th>
                  <Th>Material Code</Th>
                </Tr>
              </Thead>
              <Tbody>
                {material.suppliers.map((supplier, index) => (
                  <Tr key={index}>
                    <Td>{supplier.supplierName}</Td>
                    <Td>{supplier.price}</Td>
                    <Td>{supplier.currency}</Td>
                    <Td>{supplier.leadTimeDays}</Td>
                    <Td>{supplier.minOrderQuantity}</Td>
                    <Td>{supplier.supplierMaterialCode}</Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        </>
      )}

      <Divider my={6} />

      {/* Action Buttons */}
      <Box textAlign="center">
        <HStack spacing={4} justify="center">
          <Button 
            variant="outline"
            leftIcon={<ChevronLeftIcon />}
            size="lg"
            onClick={() => router.push('/material')}
          >
            Back to Catalog
          </Button>
          {isOwner() && (
            <>
              <Button 
                colorScheme="blue" 
                size="lg"
                onClick={() => router.push(`/material/${id}/edit`)}
              >
                Edit Material
              </Button>
              <Button 
                colorScheme="red" 
                size="lg"
                onClick={handleDelete}
                isLoading={deleting}
                loadingText="Deleting..."
                isDisabled={material.status === 'DELETED'}
              >
                Delete Material
              </Button>
            </>
          )}
        </HStack>
      </Box>
    </Box>
  );
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'ACTIVE':
      return 'green';
    case 'INACTIVE':
      return 'yellow';
    case 'DISCONTINUED':
      return 'orange';
    case 'DELETED':
      return 'red';
    default:
      return 'gray';
  }
};

export default MaterialDetailPage;
