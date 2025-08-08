// components/MaterialCard.tsx
import React, { useState } from 'react';
import { Box, Image, Text, Heading, VStack, Tag, TagLabel, Flex, IconButton, useToast } from '@chakra-ui/react';
import { DeleteIcon } from '@chakra-ui/icons';
import { Material } from '../../types/material';
import NextLink from 'next/link';
import { deleteMaterial } from '../../services/materialService';

interface MaterialCardProps {
  material: Material;
  onDelete?: () => void;
  showDeleteButton?: boolean;
}

const MaterialCard: React.FC<MaterialCardProps> = ({ material, onDelete, showDeleteButton = false }) => {
  const [deleting, setDeleting] = useState(false);
  const toast = useToast();

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'green';
      case 'INACTIVE': return 'orange';
      case 'DISCONTINUED': return 'red';
      case 'DELETED': return 'gray';
      default: return 'gray';
    }
  };

  const handleDelete = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (!confirm('Are you sure you want to delete this material? This action cannot be undone.')) {
      return;
    }

    setDeleting(true);
    try {
      await deleteMaterial(material.id);
      toast({
        title: 'Material deleted successfully!',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
      onDelete?.();
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
          aria-label="Delete material"
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
          isDisabled={material.status === 'DELETED'}
          opacity={0.8}
          _hover={{ bg: 'red.100', opacity: 1 }}
        />
      )}

      <NextLink href={`/material/${material.id}`} passHref>
        <Box cursor="pointer">
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
    </Box>
  );
};

export default MaterialCard;