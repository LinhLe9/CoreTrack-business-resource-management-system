"use client";

import { useRouter } from 'next/navigation';
import { useParams } from 'next/navigation';
import { useEffect, useState } from 'react';
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
  Button,
  Flex,
  HStack,
} from '@chakra-ui/react';
import { getProductById } from '@/services/productService';
import { ProductDetailResponse } from '@/types/product';
import VariantTable from '@/components/product/VariantTable';
import ProductStatusMenu from '@/components/product/ProductStatusMenu'; 

const ProductDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const id = params?.id;
  
  const [product, setProduct] = useState<ProductDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const refreshProduct = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await getProductById(Number(id));
      setProduct(mapProductResponse(data));
    } catch (err: any) {
      setError('Failed to load product detail.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!id) return;
    refreshProduct();
  }, [id]);

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

  if (loading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  if (error || !product) {
    return (
      <Center minHeight="300px">
        <Text color="red.500" fontSize="lg">
          {error || 'Product not found.'}
        </Text>
      </Center>
    );
  }

  return (
    <Box maxW="900px" mx="auto" p={6}>
      <Flex justify="space-between" align="center" mb={4}>
        <Heading as="h2" size="xl">
          {product.name}
        </Heading>
        <ProductStatusMenu 
          productId={Number(id)}
          currentStatus={product.status}
          onStatusChange={refreshProduct}
        />
      </Flex>

      <HStack align="start" spacing={6} mb={6}>
        <VStack align="start" spacing={4} flex={1}>
          <Text><strong>SKU:</strong> {product.sku}</Text>
          <Text><strong>Group:</strong> {product.group}</Text>
          <Text><strong>Description:</strong> {product.description}</Text>
          <Text><strong>Price:</strong> ${product.price.toFixed(2)}</Text>
          <Badge colorScheme={getStatusColor(product.status)}>
            {product.status}
          </Badge>
        </VStack>
        
        {product.imageUrl && (
          <Image 
            src={product.imageUrl} 
            alt={product.name} 
            boxSize="300px" 
            objectFit="cover" 
            borderRadius="md" 
          />
        )}
      </HStack>

      <Divider my={6} />

      <Heading as="h3" size="md" mb={2}>
        Variants & Inventory
      </Heading>

      <VariantTable variants={product.variants} />

      <Divider my={6} />

      {/* Edit Button */}
      <Box textAlign="center">
        <Button 
          colorScheme="blue" 
          size="lg"
          onClick={() => router.push(`/product/${id}/edit`)}
        >
          Edit Product
        </Button>
      </Box>
    </Box>
  );
};

const mapProductResponse = (data: any): ProductDetailResponse => ({
  ...data,
  group: typeof data.groupProduct === 'object' && data.groupProduct !== null ? data.groupProduct.name : data.groupProduct,
  variants: data.variantInventory,
  status: data.status || '',
});

export default ProductDetailPage;