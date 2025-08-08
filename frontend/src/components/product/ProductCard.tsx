// components/ProductCard.tsx
import React, { useState } from 'react';
import { Box, Image, Text, Heading, VStack, Tag, TagLabel, Flex, IconButton, useToast } from '@chakra-ui/react';
import { DeleteIcon } from '@chakra-ui/icons';
import { Product } from '../../types/product';
import NextLink from 'next/link'; // Dùng NextLink để điều hướng nội bộ
import { deleteProduct } from '../../services/productService';
import { useRouter } from 'next/navigation';

interface ProductCardProps {
  product: Product;
  onDelete?: () => void;
  showDeleteButton?: boolean;
}

const ProductCard: React.FC<ProductCardProps> = ({ product, onDelete, showDeleteButton = false }) => {
  const [deleting, setDeleting] = useState(false);
  const toast = useToast();
  const router = useRouter();

  const getStatusColor = (status: Product['status']) => {
    switch (status) {
      case 'Active': return 'green';
      case 'Inactive': return 'yellow';
      case 'Discontinued': return 'orange';
      case 'Deleted': return 'red';
      default: return 'gray';
    }
  };

  const handleDelete = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (!confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
      return;
    }

    setDeleting(true);
    try {
      await deleteProduct(product.id);
      toast({
        title: 'Product deleted successfully!',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
      onDelete?.();
    } catch (err: any) {
      console.error('Error deleting product:', err);
      toast({
        title: 'Failed to delete product',
        description: err.response?.data?.message || 'Please try again.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Box
      borderWidth="1px"
      borderRadius="lg"
      overflow="hidden"
      p={4}
      textAlign="center"
      boxShadow="md"
      transition="all 0.2s"
      _hover={{ transform: 'translateY(-5px)', boxShadow: 'lg' }}
      display="flex"
      flexDirection="column"
      justifyContent="space-between"
      bg="white"
      position="relative"
    >
      {/* Delete Icon - Top Right Corner - Only show if showDeleteButton is true */}
      {showDeleteButton && (
        <IconButton
          aria-label="Delete product"
          icon={<DeleteIcon />}
          size="sm"
          colorScheme="red"
          variant="ghost"
          position="absolute"
          top={2}
          right={2}
          zIndex={10}
          onClick={handleDelete}
          isLoading={deleting}
          isDisabled={product.status === 'Deleted'}
          opacity={0.8}
          _hover={{ bg: 'red.100', opacity: 1 }}
        />
      )}

      <NextLink href={`/product/${product.id}`} passHref>
        <Box cursor="pointer">
          <Image
            src={product.imageUrl || '/default-product.jpg'}
            alt={product.name}
            boxSize="150px"
            objectFit="contain"
            mx="auto"
            mb={4}
            borderRadius="md"
          />
          <VStack align="flex-start" spacing={1} flexGrow={1} w="full">
            <Heading as="h3" size="md" noOfLines={2} title={product.name}>
              {product.name}
            </Heading>
            <Text fontSize="sm" color="gray.600">
              SKU: <Text as="span" fontWeight="bold">{product.sku}</Text>
            </Text>
            {product.shortDescription && (
              <Text fontSize="sm" noOfLines={2} color="gray.700">
                {product.shortDescription}
              </Text>
            )}
            <Flex justify="space-between" align="center" width="full" mt={2}>
              {product.group && (
                <Tag size="sm" colorScheme="purple" borderRadius="full">
                  <TagLabel>{product.group}</TagLabel>
                </Tag>
              )}
              <Tag size="sm" colorScheme={getStatusColor(product.status)} borderRadius="full">
                <TagLabel>{product.status}</TagLabel>
              </Tag>
            </Flex>
          </VStack>
        </Box>
      </NextLink>
    </Box>
  );
};

export default ProductCard;