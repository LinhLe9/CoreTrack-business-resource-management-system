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
import { PurchasingTicketCardResponse } from '../../types/purchasingTicket';
import { purchasingTicketService } from '../../services/purchasingTicketService';

interface PurchasingTicketCardProps {
  purchasingTicket: PurchasingTicketCardResponse;
  onViewDetails: (id: number) => void;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

const PurchasingTicketCard: React.FC<PurchasingTicketCardProps> = ({
  purchasingTicket,
  onViewDetails,
  onEdit,
  onDelete,
}) => {
  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.600');

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'NEW':
        return 'blue';
      case 'PARTIAL_APPROVAL':
        return 'yellow';
      case 'APPROVAL':
        return 'purple';
      case 'PARTIAL_SUCCESSFUL':
        return 'orange';
      case 'SUCCESSFUL':
        return 'green';
      case 'PARTIAL_SHIPPING':
        return 'orange';
      case 'SHIPPING':
        return 'cyan';
      case 'PARTIAL_READY':
        return 'orange';
      case 'READY':
        return 'green';
      case 'CLOSED':
        return 'gray';
      case 'PARTIAL_CANCELLED':
        return 'red';
      case 'CANCELLED':
        return 'red';
      default:
        return 'blue';
    }
  };

  const getStatusBadgeText = (status: string) => {
    return purchasingTicketService.getStatusBadgeText(status);
  };

  const formatDate = (dateString: string) => {
    return purchasingTicketService.formatDate(dateString);
  };

  const isCancelled = purchasingTicket.status === 'CANCELLED' || 
                     purchasingTicket.status === 'PARTIAL_CANCELLED';

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
              {purchasingTicket.name}
            </Text>
            <Text fontSize="sm" color="gray.500">
              ID: {purchasingTicket.id}
            </Text>
          </VStack>
          
          <Badge
            colorScheme={getStatusColor(purchasingTicket.status)}
            px={3}
            py={1}
            borderRadius="full"
            fontSize="sm"
            fontWeight="medium"
          >
            {getStatusBadgeText(purchasingTicket.status)}
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
              {formatDate(purchasingTicket.createdAt)}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Updated:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {formatDate(purchasingTicket.updatedAt)}
            </Text>
          </HStack>

          {purchasingTicket.completed_date && (
            <HStack justify="space-between">
              <Text fontSize="sm" color="gray.600">
                Completed:
              </Text>
              <Text fontSize="sm" fontWeight="medium">
                {formatDate(purchasingTicket.completed_date)}
              </Text>
            </HStack>
          )}

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Created By:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {purchasingTicket.createdBy}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Role:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {purchasingTicket.createdByRole}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Updated By:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {purchasingTicket.updatedBy}
            </Text>
          </HStack>

          <HStack justify="space-between">
            <Text fontSize="sm" color="gray.600">
              Updated By Role:
            </Text>
            <Text fontSize="sm" fontWeight="medium">
              {purchasingTicket.updatedByRole}
            </Text>
          </HStack>
        </VStack>

        {/* Actions */}
        <HStack spacing={2} justify="center">
          <Button
            leftIcon={<ViewIcon />}
            size="sm"
            colorScheme="blue"
            variant="outline"
            onClick={() => onViewDetails(purchasingTicket.id)}
            flex={1}
          >
            View Details
          </Button>
          
          {onEdit && (
            <IconButton
              aria-label="Edit purchasing ticket"
              icon={<EditIcon />}
              size="sm"
              colorScheme="yellow"
              variant="outline"
              onClick={() => onEdit(purchasingTicket.id)}
            />
          )}
          
          {onDelete && (
            <IconButton
              aria-label="Delete purchasing ticket"
              icon={<DeleteIcon />}
              size="sm"
              colorScheme="red"
              variant="outline"
              onClick={() => onDelete(purchasingTicket.id)}
            />
          )}
        </HStack>
      </VStack>
    </Box>
  );
};

export default PurchasingTicketCard; 