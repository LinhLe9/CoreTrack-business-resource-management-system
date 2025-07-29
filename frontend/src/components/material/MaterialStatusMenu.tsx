import React, { useState } from 'react';
import {
  IconButton,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  useToast,
  useDisclosure,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  Button,
  FormControl,
  FormLabel,
  Select,
  Textarea,
  VStack,
  Text,
  Badge,
} from '@chakra-ui/react';
import { HamburgerIcon } from '@chakra-ui/icons';
import { getAvailableStatusTransitions, changeMaterialStatus } from '../../services/materialService';
import { Material } from '../../types/material';

interface MaterialStatusMenuProps {
  material: Material;
  onStatusChange?: () => void;
}

const MaterialStatusMenu: React.FC<MaterialStatusMenuProps> = ({ material, onStatusChange }) => {
  const [loading, setLoading] = useState(false);
  const [availableTransitions, setAvailableTransitions] = useState<string[]>([]);
  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [reason, setReason] = useState<string>('');
  const { isOpen, onOpen, onClose } = useDisclosure();
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

  const handleMenuOpen = async () => {
    try {
      setLoading(true);
      const response = await getAvailableStatusTransitions(material.id);
      setAvailableTransitions(response.availableTransitions || []);
    } catch (error: any) {
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to load available status transitions',
        status: 'error',
        duration: 5000,
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
      const response = await changeMaterialStatus(material.id, selectedStatus, reason);
      
      if (response.success) {
        toast({
          title: 'Success',
          description: response.message,
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
        onStatusChange?.(); // Refresh the material data
        onClose();
        setSelectedStatus('');
        setReason('');
      } else {
        toast({
          title: 'Error',
          description: response.message,
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error: any) {
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to change material status',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Menu onOpen={handleMenuOpen}>
        <MenuButton
          as={IconButton}
          aria-label="Material options"
          icon={<HamburgerIcon />}
          variant="ghost"
          size="sm"
          onClick={(e) => e.stopPropagation()}
        />
        <MenuList>
          <MenuItem onClick={onOpen} isDisabled={loading}>
            Change Status
          </MenuItem>
        </MenuList>
      </Menu>

      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Change Material Status</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={4}>
              <Text fontSize="sm" color="gray.600">
                Current Status: <Badge colorScheme={getStatusColor(material.status)}>{material.status}</Badge>
              </Text>
              
              <FormControl>
                <FormLabel>New Status</FormLabel>
                <Select
                  placeholder="Select new status"
                  value={selectedStatus}
                  onChange={(e) => setSelectedStatus(e.target.value)}
                >
                  {availableTransitions.map((status) => (
                    <option key={status} value={status}>
                      {status}
                    </option>
                  ))}
                </Select>
              </FormControl>

              <FormControl>
                <FormLabel>Reason (Optional)</FormLabel>
                <Textarea
                  placeholder="Enter reason for status change..."
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  rows={3}
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
              isDisabled={!selectedStatus}
            >
              Change Status
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  );
};

export default MaterialStatusMenu; 