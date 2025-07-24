// components/ProductCard.tsx
import React from 'react';
import { Box, Image, Text, Heading, VStack, Tag, TagLabel, Flex } from '@chakra-ui/react';
import { Product } from '../../types/product';
import NextLink from 'next/link'; // Dùng NextLink để điều hướng nội bộ

interface ProductCardProps {
  product: Product;
}

const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  const getStatusColor = (status: Product['status']) => {
    switch (status) {
      case 'Active': return 'green';
      case 'Inactive': return 'orange';
      case 'Discontinued': return 'red';
      case 'Pending': return 'blue';
      default: return 'gray';
    }
  };

  return (
    <NextLink href={`/product/${product.id}`} passHref> {/* Giả định có trang chi tiết */}
      <Box
        as="a" // Để Box có thể hoạt động như một link
        borderWidth="1px"
        borderRadius="lg"
        overflow="hidden"
        p={4}
        textAlign="center"
        boxShadow="md"
        transition="all 0.2s"
        _hover={{ transform: 'translateY(-5px)', boxShadow: 'lg', cursor: 'pointer' }}
        display="flex"
        flexDirection="column"
        justifyContent="space-between"
        bg="white"
      >
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
            {product.groupProduct && (
              <Tag size="sm" colorScheme="purple" borderRadius="full">
                <TagLabel>{product.groupProduct}</TagLabel>
              </Tag>
            )}
            <Tag size="sm" colorScheme={getStatusColor(product.status)} borderRadius="full">
              <TagLabel>{product.status}</TagLabel>
            </Tag>
          </Flex>
        </VStack>
      </Box>
    </NextLink>
  );
};

export default ProductCard;