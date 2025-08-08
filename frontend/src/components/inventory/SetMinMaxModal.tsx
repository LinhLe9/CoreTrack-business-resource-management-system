import React, { useState } from 'react';
import {
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
  Input,
  VStack,
  Text,
  useToast,
  Spinner,
} from '@chakra-ui/react';
import { setMinimumAlertStock as setProductMinimumAlertStock, setMaximumStockLevel as setProductMaximumStockLevel } from '../../services/productInventoryService';
import { setMinimumAlertStock as setMaterialMinimumAlertStock, setMaximumStockLevel as setMaterialMaximumStockLevel } from '../../services/materialInventoryService';
import { formatBigDecimal } from '../../lib/utils';

interface SetMinMaxModalProps {
  isOpen: boolean;
  onClose: () => void;
  variantId: number;
  variantSku: string;
  variantName: string;
  type: 'minimum' | 'maximum';
  currentValue: string;
  onSuccess: () => void;
  serviceType?: 'product' | 'material'; // Add service type prop
}

const SetMinMaxModal: React.FC<SetMinMaxModalProps> = ({
  isOpen,
  onClose,
  variantId,
  variantSku,
  variantName,
  type,
  currentValue,
  onSuccess,
  serviceType = 'product', // Default to product
}) => {
  const [value, setValue] = useState('');
  const [loading, setLoading] = useState(false);
  const toast = useToast();

  const handleSubmit = async () => {
    if (!value || isNaN(Number(value)) || Number(value) < 0) {
      toast({
        title: 'Invalid Value',
        description: 'Please enter a valid positive number.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    setLoading(true);
    try {
      const numValue = Number(value);
      
      if (type === 'minimum') {
        if (serviceType === 'material') {
          await setMaterialMinimumAlertStock(variantId, numValue);
        } else {
          await setProductMinimumAlertStock(variantId, numValue);
        }
        toast({
          title: 'Success',
          description: 'Minimum alert stock updated successfully.',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      } else {
        if (serviceType === 'material') {
          await setMaterialMaximumStockLevel(variantId, numValue);
        } else {
          await setProductMaximumStockLevel(variantId, numValue);
        }
        toast({
          title: 'Success',
          description: 'Maximum stock level updated successfully.',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      }
      
      onSuccess();
      onClose();
    } catch (error: any) {
      console.error('Error updating stock level:', error);
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to update stock level.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setValue('');
    onClose();
  };

  const title = type === 'minimum' ? 'Set Minimum Alert Stock' : 'Set Maximum Stock Level';
  const label = type === 'minimum' ? 'Minimum Alert Stock' : 'Maximum Stock Level';
  const placeholder = `Enter new ${type === 'minimum' ? 'minimum alert' : 'maximum'} stock level`;
  const itemType = serviceType === 'material' ? 'Material' : 'Product';

  return (
    <Modal isOpen={isOpen} onClose={handleClose} isCentered>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>{title}</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <VStack spacing={4}>
            <Text fontSize="sm" color="gray.600">
              <strong>{itemType}:</strong> {variantName}
            </Text>
            <Text fontSize="sm" color="gray.600">
              <strong>SKU:</strong> {variantSku}
            </Text>
            <Text fontSize="sm" color="gray.600">
              <strong>Current {label}:</strong> {formatBigDecimal(currentValue)}
            </Text>
            
            <FormControl>
              <FormLabel>New {label}</FormLabel>
              <Input
                type="number"
                step="0.01"
                min="0"
                placeholder={placeholder}
                value={value}
                onChange={(e) => setValue(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    handleSubmit();
                  }
                }}
              />
            </FormControl>
          </VStack>
        </ModalBody>

        <ModalFooter>
          <Button variant="ghost" mr={3} onClick={handleClose}>
            Cancel
          </Button>
          <Button
            colorScheme="blue"
            onClick={handleSubmit}
            isLoading={loading}
            loadingText="Updating..."
            isDisabled={!value || isNaN(Number(value)) || Number(value) < 0}
          >
            {loading ? <Spinner size="sm" /> : 'Update'}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default SetMinMaxModal; 