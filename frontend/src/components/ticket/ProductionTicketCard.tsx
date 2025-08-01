'use client';

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
import { ViewIcon, EditIcon, DeleteIcon } from '@chakra-ui/icons';
import { ProductionTicketCardResponse } from '../../types/productionTicket';
import { productionTicketUtils } from '../../services/productionTicketService';

interface ProductionTicketCardProps {
  productionTicket: ProductionTicketCardResponse;
  onViewDetails: (id: number) => void;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

const ProductionTicketCard: React.FC<ProductionTicketCardProps> = ({
  productionTicket,
  onViewDetails,
  onEdit,
  onDelete,
}) => {
  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.600');

  const getStatusColor = (status: string) => {
    return productionTicketUtils.getStatusColor(status);
  };

  const getStatusBadgeText = (status: string) => {
    return productionTicketUtils.getStatusBadgeText(status);
  };

  const formatDate = (dateString: string) => {
    return productionTicketUtils.formatDate(dateString);
  };

  const isCancelled = productionTicket.status === 'CANCELLED' || 
                     productionTicket.status === 'PARTIAL_CANCELLED';

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
      opacity={isCancelled ? 0.7 : 1}
    >
      <VStack spacing={4} align="stretch">
        {/* Header */}
        <Flex justify="space-between" align="start">
          <VStack align="start" spacing={1} flex={1}>
            <Text fontWeight="bold" fontSize="lg" noOfLines={1}>
              {productionTicket.name}
            </Text>
            <Text fontSize="sm" color="gray.500">
              ID: {productionTicket.id}
            </Text>
          </VStack>
          
          <Badge
            className={getStatusColor(productionTicket.status)}
            px={3}
            py={1}
            borderRadius="full"
            fontSize="xs"
            fontWeight="medium"
          >
            {getStatusBadgeText(productionTicket.status)}
          </Badge>
        </Flex>

        <Divider />

        {/* Details */}
        <VStack spacing={3} align="stretch">
          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Created:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {formatDate(productionTicket.createdAt)}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Updated:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {formatDate(productionTicket.updatedAt)}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Created By:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {productionTicket.createdBy}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Role:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {productionTicket.createdByRole}
            </Text>
          </HStack>
        </VStack>

        <Divider />

        {/* Actions */}
        <HStack spacing={2} justify="center">
          <Button
            leftIcon={<ViewIcon />}
            size="sm"
            colorScheme="blue"
            variant="outline"
            onClick={() => onViewDetails(productionTicket.id)}
            flex={1}
          >
            View Details
          </Button>
          
          {onEdit && (
            <IconButton
              aria-label="Edit production ticket"
              icon={<EditIcon />}
              size="sm"
              colorScheme="yellow"
              variant="outline"
              onClick={() => onEdit(productionTicket.id)}
            />
          )}
          
          {onDelete && (
            <IconButton
              aria-label="Delete production ticket"
              icon={<DeleteIcon />}
              size="sm"
              colorScheme="red"
              variant="outline"
              onClick={() => onDelete(productionTicket.id)}
            />
          )}
        </HStack>
      </VStack>
    </Box>
  );
};

export default ProductionTicketCard; 