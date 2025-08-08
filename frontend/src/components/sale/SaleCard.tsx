// components/sale/SaleCard.tsx
import React from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Badge,
  Button,
  IconButton,
  Flex,
  Divider,
  useColorModeValue,
} from '@chakra-ui/react';
import { ViewIcon, EditIcon } from '@chakra-ui/icons';
import { useRouter } from 'next/navigation';
import { SaleCardResponse } from '../../types/sale';
import { useUser } from '../../hooks/useUser';

interface SaleCardProps {
  sale: SaleCardResponse;
  onView?: (saleId: number) => void;
  onEdit?: (saleId: number) => void;
}

const SaleCard: React.FC<SaleCardProps> = ({ sale, onView, onEdit }) => {
  const router = useRouter();
  const { isOwner, isSaleStaff } = useUser();
  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.600');

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'new':
        return 'blue';
      case 'allocated':
        return 'yellow';
      case 'packed':
        return 'orange';
      case 'shipped':
        return 'purple';
      case 'done':
        return 'green';
      case 'cancelled':
        return 'red';
      default:
        return 'gray';
    }
  };

  const handleView = () => {
    if (onView) {
      onView(sale.id);
    } else {
      router.push(`/sale/${sale.id}`);
    }
  };

  const handleEdit = () => {
    if (onEdit) {
      onEdit(sale.id);
    } else {
      router.push(`/sale/${sale.id}/edit`);
    }
  };

  return (
    <Box
      bg={bgColor}
      border="1px"
      borderColor={borderColor}
      borderRadius="lg"
      p={6}
      shadow="sm"
      _hover={{ shadow: "md" }}
      transition="all 0.2s"
    >
      <VStack spacing={4} align="stretch">
        {/* Header */}
        <Flex justify="space-between" align="start">
          <VStack align="start" spacing={1} flex={1}>
            <Text fontWeight="bold" fontSize="lg" noOfLines={1}>
              {sale.sku}
            </Text>
            <Text fontSize="sm" color="gray.500">
              {sale.customerName || 'N/A'}
            </Text>
          </VStack>
          
          <HStack spacing={2}>
            <Badge
              colorScheme={getStatusColor(sale.status)}
              px={3}
              py={1}
              borderRadius="full"
              fontSize="xs"
              fontWeight="medium"
            >
              {sale.status}
            </Badge>
            {onEdit && (isOwner() || isSaleStaff()) && (
              <IconButton
                aria-label="Edit sale"
                icon={<EditIcon />}
                size="sm"
                colorScheme="yellow"
                variant="ghost"
                onClick={handleEdit}
              />
            )}
          </HStack>
        </Flex>

        <Divider />

        {/* Details */}
        <VStack spacing={3} align="stretch">
          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Customer Email:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {sale.customerEmail || 'N/A'}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Customer Phone:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {sale.customerPhone || 'N/A'}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Details:
            </Text>
            <Badge colorScheme="blue" variant="subtle">
              {sale.detailNumber} items
            </Badge>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Created:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {new Date(sale.createdAt).toLocaleDateString()}
            </Text>
          </HStack>

          {sale.customerAddress && (
            <Box>
              <Text fontSize="sm" color="gray.600" mb={1}>
                Address:
              </Text>
              <Text fontSize="sm" noOfLines={2}>
                {sale.customerAddress}
              </Text>
            </Box>
          )}
        </VStack>

        <Divider />

        {/* Actions */}
        <HStack spacing={2} justify="center">
          <Button
            leftIcon={<ViewIcon />}
            size="sm"
            colorScheme="blue"
            variant="outline"
            onClick={handleView}
            flex={1}
          >
            View Details
          </Button>
        </HStack>
      </VStack>
    </Box>
  );
};

export default SaleCard; 