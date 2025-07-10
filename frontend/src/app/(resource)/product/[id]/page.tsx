import { useRouter } from 'next/router';
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
} from '@chakra-ui/react';
import { getProductById } from '@/services/productService';
import { ProductDetailResponse } from '@/types/product';
import VariantTable from '@/components/VariantTable'; // Giả sử bạn hiển thị biến thể sản phẩm trong bảng

const ProductDetailPage = () => {
  const router = useRouter();
  const { id } = router.query;

  const [product, setProduct] = useState<ProductDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    const fetchProduct = async () => {
      setLoading(true);
      try {
        const data = await getProductById(Number(id));
        setProduct(data);
      } catch (err: any) {
        setError('Failed to load product detail.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

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
      <Heading as="h2" size="xl" mb={4}>
        {product.name}
      </Heading>

      <VStack align="start" spacing={4}>
        {product.imageUrl && (
          <Image src={product.imageUrl} alt={product.name} boxSize="300px" objectFit="cover" borderRadius="md" />
        )}

        <Text><strong>SKU:</strong> {product.sku}</Text>
        <Text><strong>Group:</strong> {product.group}</Text>
        <Text><strong>Description:</strong> {product.description}</Text>
        <Text><strong>Price:</strong> ${product.price.toFixed(2)}</Text>
        <Badge colorScheme={product.status === 'Active' ? 'green' : 'gray'}>
          {product.status}
        </Badge>
      </VStack>

      <Divider my={6} />

      <Heading as="h3" size="md" mb={2}>
        Variants & Inventory
      </Heading>

      <VariantTable variants={product.variants} />
    </Box>
  );
};

export default ProductDetailPage;