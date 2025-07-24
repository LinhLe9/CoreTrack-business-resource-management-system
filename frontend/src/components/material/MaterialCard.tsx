// components/ProductCard.tsx
import React from 'react';
import { Box, Image, Text, Heading, VStack, Tag, TagLabel, Flex } from '@chakra-ui/react';
import { Material } from '../../types/material';
import NextLink from 'next/link';

interface MaterialCardProps {
  material: Material;
}

const MaterialCard: React.FC<MaterialCardProps> = ({ material }) => {
  const getStatusColor = (status: Material['status']) => {
    switch (status) {
      case 'ACTIVE': return 'green';
      case 'INACTIVE': return 'orange';
      case 'DISCONTINUED': return 'red';
      case 'PENDING': return 'blue';
      default: return 'gray';
    }
  };

  return (
    <NextLink href={`/material/${material.id}`} passHref> 
      <Box
        as="a"
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
          src={material.imageUrl || '/default-product.jpg'}
          alt={material.name}
          boxSize="150px"
          objectFit="contain"
          mx="auto"
          mb={4}
          borderRadius="md"
        />
        <VStack align="flex-start" spacing={1} flexGrow={1} w="full">
          <Heading as="h3" size="md" noOfLines={2} title={material.name}>
            {material.name}
          </Heading>
          <Text fontSize="sm" color="gray.600">
            SKU: <Text as="span" fontWeight="bold">{material.sku}</Text>
          </Text>
          {material.shortDes && (
            <Text fontSize="sm" noOfLines={2} color="gray.700">
              {material.shortDes}
            </Text>
          )}
          <Flex justify="space-between" align="center" width="full" mt={2}>
            {material.groupMaterial && (
              <Tag size="sm" colorScheme="purple" borderRadius="full">
                <TagLabel>{material.groupMaterial}</TagLabel>
              </Tag>
            )}
            <Tag size="sm" colorScheme={getStatusColor(material.status)} borderRadius="full">
              <TagLabel>{material.status}</TagLabel>
            </Tag>
          </Flex>
        </VStack>
      </Box>
    </NextLink>
  );
};

export default MaterialCard;