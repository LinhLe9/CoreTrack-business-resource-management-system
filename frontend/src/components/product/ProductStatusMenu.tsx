'use client';

import React, { useState, useEffect } from 'react';
import {
  IconButton,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Button,
  FormControl,
  FormLabel,
  Input,
  VStack,
  Text,
  Badge,
  useDisclosure
} from '@chakra-ui/react';
import { FiMoreVertical } from 'react-icons/fi';
import { getAvailableStatusTransitions, changeProductStatus } from '@/services/productService';

interface ProductStatusMenuProps {
  productId: number;
  currentStatus: string;
  onStatusChange: () => void;
}

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

export default function ProductStatusMenu({ productId, currentStatus, onStatusChange }: ProductStatusMenuProps) {
  const [availableTransitions, setAvailableTransitions] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [reason, setReason] = useState<string>('');
  const { isOpen, onOpen, onClose } = useDisclosure();
  const toast = useToast();

  useEffect(() => {
    fetchAvailableTransitions();
  }, [productId]);

  const fetchAvailableTransitions = async () => {
    try {
      setLoading(true);
      const response = await getAvailableStatusTransitions(productId);
      setAvailableTransitions(response.availableTransitions || []);
    } catch (error) {
      console.error('Error fetching available transitions:', error);
      toast({
        title: 'Error',
        description: 'Failed to fetch available status transitions',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async () => {
    if (!selectedStatus) {
      toast({
        title: 'Error',
        description: 'Please select a status',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    try {
      setLoading(true);
      await changeProductStatus(productId, selectedStatus, reason);
      
      toast({
        title: 'Success',
        description: `Product status changed to ${selectedStatus}`,
        status: 'success',
        duration: 3000,
        isClosable: true,
      });

      onClose();
      setSelectedStatus('');
      setReason('');
      onStatusChange();
    } catch (error: any) {
      console.error('Error changing product status:', error);
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to change product status',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleMenuClick = (status: string) => {
    setSelectedStatus(status);
    onOpen();
  };

  return (
    <>
      <Menu>
        <MenuButton
          as={IconButton}
          aria-label="Product status options"
          icon={<FiMoreVertical />}
          variant="ghost"
          size="sm"
        />
        <MenuList>
          <MenuItem isDisabled>
            <VStack align="start" spacing={1}>
              <Text fontSize="sm" fontWeight="medium">Current Status</Text>
              <Badge colorScheme={getStatusColor(currentStatus)}>
                {currentStatus}
              </Badge>
            </VStack>
          </MenuItem>
          {availableTransitions.length > 0 && (
            <MenuItem isDisabled>
              <Text fontSize="sm" fontWeight="medium">Change Status</Text>
            </MenuItem>
          )}
          {availableTransitions.map((status) => (
            <MenuItem
              key={status}
              onClick={() => handleMenuClick(status)}
              isDisabled={loading}
            >
              <Badge colorScheme={getStatusColor(status)}>
                {status}
              </Badge>
            </MenuItem>
          ))}
          {availableTransitions.length === 0 && (
            <MenuItem isDisabled>
              <Text fontSize="sm" color="gray.500">
                No available transitions
              </Text>
            </MenuItem>
          )}
        </MenuList>
      </Menu>

      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Change Product Status</ModalHeader>
          <ModalBody>
            <VStack spacing={4}>
              <FormControl>
                <FormLabel>New Status</FormLabel>
                <Badge colorScheme={getStatusColor(selectedStatus)} size="lg">
                  {selectedStatus}
                </Badge>
              </FormControl>
              <FormControl>
                <FormLabel>Reason (Optional)</FormLabel>
                <Input
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  placeholder="Enter reason for status change..."
                />
              </FormControl>
            </VStack>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button
              colorScheme="blue"
              onClick={handleStatusChange}
              isLoading={loading}
              loadingText="Changing Status"
            >
              Change Status
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  );
} 