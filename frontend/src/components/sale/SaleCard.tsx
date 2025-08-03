// components/sale/SaleCard.tsx
import React from 'react';
import {
  Box,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Text,
  Badge,
  HStack,
  VStack,
  Flex,
  IconButton,
  useColorModeValue,
} from '@chakra-ui/react';
import { ViewIcon, EditIcon } from '@chakra-ui/icons';
import { useRouter } from 'next/navigation';
import { SaleCardResponse } from '../../types/sale';

interface SaleCardProps {
  sale: SaleCardResponse;
  onView?: (saleId: number) => void;
  onEdit?: (saleId: number) => void;
}

const SaleCard: React.FC<SaleCardProps> = ({ sale, onView, onEdit }) => {
  const router = useRouter();
  const cardBg = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');

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
    <Card
      bg={cardBg}
      borderWidth="1px"
      borderColor={borderColor}
      borderRadius="lg"
      overflow="hidden"
      _hover={{ shadow: 'md', transform: 'translateY(-2px)' }}
      transition="all 0.2s"
    >
      <CardHeader pb={2}>
        <Flex justify="space-between" align="center">
          <VStack align="start" spacing={1}>
            <Heading size="md" color="blue.600">
              {sale.sku}
            </Heading>
            <Text fontSize="sm" color="gray.500">
              {sale.customerName}
            </Text>
          </VStack>
          <HStack spacing={2}>
            <Badge colorScheme={getStatusColor(sale.status)}>
              {sale.status}
            </Badge>
            <HStack spacing={1}>
              <IconButton
                aria-label="View sale details"
                icon={<ViewIcon />}
                size="sm"
                variant="ghost"
                colorScheme="blue"
                onClick={handleView}
              />
              <IconButton
                aria-label="Edit sale"
                icon={<EditIcon />}
                size="sm"
                variant="ghost"
                colorScheme="green"
                onClick={handleEdit}
              />
            </HStack>
          </HStack>
        </Flex>
      </CardHeader>

      <CardBody pt={0}>
        <VStack align="stretch" spacing={3}>
          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Customer Email:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {sale.customerEmail}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Customer Phone:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {sale.customerPhone}
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
      </CardBody>
    </Card>
  );
};

export default SaleCard; 