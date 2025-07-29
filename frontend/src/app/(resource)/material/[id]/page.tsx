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
} from '@chakra-ui/react';
import { getMaterialById } from '@/services/materialService';
import { MaterialDetailResponse } from '@/types/material';
import MaterialStatusMenu from '@/components/material/MaterialStatusMenu';

const MaterialDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const id = params?.id;
  
  const [material, setMaterial] = useState<MaterialDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    const fetchMaterial = async () => {
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

    fetchMaterial();
  }, [id]);

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
            if (id) {
              getMaterialById(Number(id)).then(setMaterial).catch(console.error);
            }
          }}
        />
      </Flex>

      <VStack align="start" spacing={4}>
        {material.imageUrl && (
          <Image 
            src={material.imageUrl} 
            alt={material.name} 
            boxSize="300px" 
            objectFit="cover" 
            borderRadius="md" 
          />
        )}

        <Text><strong>SKU:</strong> {material.sku}</Text>
        <Text><strong>Group:</strong> {material.groupMaterial}</Text>
        <Text><strong>Description:</strong> {material.shortDes}</Text>
        <Text><strong>UOM:</strong> {material.uom}</Text>
        <Badge colorScheme={material.status.toLowerCase() === 'active' ? 'green' : 'gray'}>
          {material.status}
        </Badge>
      </VStack>

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

         {/* Edit Button */}
         <Box textAlign="center">
           <Button 
             colorScheme="blue" 
             size="lg"
             onClick={() => router.push(`/material/${id}/edit`)}
           >
             Edit Material
           </Button>
         </Box>
       </Box>
     );
};

export default MaterialDetailPage;
