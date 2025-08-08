'use client';

import React, { useState, useEffect } from 'react';
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
  Textarea,
  Select,
  VStack,
  HStack,
  Text,
  useToast,
  Spinner,
} from '@chakra-ui/react';
import { getMaterialTransactionEnums, addMaterialStock, subtractMaterialStock, setMaterialStock } from '../../services/materialInventoryService';
import { TransactionEnumsResponse } from '../../types/productInventory';

interface MaterialStockTransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
  variantId: number;
  variantSku: string;
  variantName: string;
  transactionType: 'set' | 'add' | 'subtract';
  onSuccess: () => void;
}

const MaterialStockTransactionModal: React.FC<MaterialStockTransactionModalProps> = ({
  isOpen,
  onClose,
  variantId,
  variantSku,
  variantName,
  transactionType,
  onSuccess,
}) => {
  const [loading, setLoading] = useState(false);
  const [enumsLoading, setEnumsLoading] = useState(false);
  const [transactionEnums, setTransactionEnums] = useState<TransactionEnumsResponse | null>(null);
  
  // Form fields
  const [quantity, setQuantity] = useState('');
  const [note, setNote] = useState('');
  const [referenceDocumentType, setReferenceDocumentType] = useState('');
  const [referenceDocumentId, setReferenceDocumentId] = useState('');
  const [transactionSource, setTransactionSource] = useState('');

  const toast = useToast();

  // Load transaction enums
  useEffect(() => {
    if (isOpen && !transactionEnums) {
      loadTransactionEnums();
    }
  }, [isOpen, transactionEnums]);

  const loadTransactionEnums = async () => {
    setEnumsLoading(true);
    try {
      const enums = await getMaterialTransactionEnums();
      setTransactionEnums(enums);
    } catch (error) {
      console.error('Error loading transaction enums:', error);
      toast({
        title: 'Error',
        description: 'Failed to load transaction options',
        status: 'error',
        duration: 3000,
      });
    } finally {
      setEnumsLoading(false);
    }
  };

  const getModalTitle = () => {
    switch (transactionType) {
      case 'set':
        return 'Set Stock';
      case 'add':
        return 'Add Stock';
      case 'subtract':
        return 'Subtract Stock';
      default:
        return 'Stock Transaction';
    }
  };

  const getDefaultTransactionType = () => {
    switch (transactionType) {
      case 'add':
        return 'IN';
      case 'subtract':
        return 'OUT';
      case 'set':
        return 'SET';
      default:
        return 'IN';
    }
  };

  const handleSubmit = async () => {
    if (!quantity || parseFloat(quantity) <= 0) {
      toast({
        title: 'Invalid Quantity',
        description: 'Please enter a valid quantity greater than 0.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    setLoading(true);

    try {
      const quantityNum = parseFloat(quantity);
      const referenceId = referenceDocumentId ? parseInt(referenceDocumentId) : undefined;

      if (transactionType === 'add') {
        await addMaterialStock(
          variantId,
          quantityNum,
          note.trim() || undefined,
          referenceDocumentType || undefined,
          referenceId,
          transactionSource || undefined
        );
      } else if (transactionType === 'subtract') {
        await subtractMaterialStock(
          variantId,
          quantityNum,
          note.trim() || undefined,
          referenceDocumentType || undefined,
          referenceId,
          transactionSource || undefined
        );
      } else if (transactionType === 'set') {
        await setMaterialStock(
          variantId,
          quantityNum,
          note.trim() || undefined,
          referenceDocumentType || undefined,
          referenceId,
          transactionSource || undefined
        );
      }

      toast({
        title: 'Success',
        description: `Stock ${transactionType} operation completed successfully.`,
        status: 'success',
        duration: 3000,
        isClosable: true,
      });

      // Reset form
      setQuantity('');
      setNote('');
      setReferenceDocumentType('');
      setReferenceDocumentId('');
      setTransactionSource('');
      
      // Close modal and refresh data
      onClose();
      onSuccess();
    } catch (error: any) {
      console.error('Stock transaction error:', error);
      toast({
        title: 'Error',
        description: error.response?.data?.message || `Failed to ${transactionType} stock.`,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setQuantity('');
    setNote('');
    setReferenceDocumentType('');
    setReferenceDocumentId('');
    setTransactionSource('');
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="lg">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>
          {getModalTitle()} - {variantName}
        </ModalHeader>
        <ModalCloseButton />
        
        <ModalBody>
          <VStack spacing={4}>
            {/* Variant SKU (readonly) */}
            <FormControl>
              <FormLabel>Material Variant SKU</FormLabel>
              <Input
                value={variantSku}
                isReadOnly
                bg="gray.50"
                fontWeight="bold"
              />
            </FormControl>

            {/* Quantity */}
            <FormControl isRequired>
              <FormLabel>Quantity</FormLabel>
              <Input
                type="number"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                placeholder="Enter quantity"
                min="0.01"
                step="0.01"
              />
            </FormControl>

            {/* Transaction Type, Source, Reference */}
            <HStack spacing={4} w="full">
              <FormControl>
                <FormLabel>Type</FormLabel>
                <Select
                  value={getDefaultTransactionType()}
                  isReadOnly
                  bg="gray.50"
                >
                  <option value="IN">IN</option>
                  <option value="OUT">OUT</option>
                  <option value="SET">SET</option>
                </Select>
              </FormControl>

              <FormControl>
                <FormLabel>Source</FormLabel>
                <Select
                  value={transactionSource}
                  onChange={(e) => setTransactionSource(e.target.value)}
                  placeholder="Select source"
                  isDisabled={enumsLoading}
                >
                  {transactionEnums?.inventoryTransactionSourceTypes?.map((source: any) => (
                    <option key={source.value} value={source.value}>
                      {source.displayName}
                    </option>
                  ))}
                </Select>
              </FormControl>
            </HStack>

            <HStack spacing={4} w="full">
              <FormControl>
                <FormLabel>Reference Type</FormLabel>
                <Select
                  value={referenceDocumentType}
                  onChange={(e) => setReferenceDocumentType(e.target.value)}
                  placeholder="Select reference type"
                  isDisabled={enumsLoading}
                >
                  {transactionEnums?.inventoryReferenceDocumentTypes?.map((refType: any) => (
                    <option key={refType.value} value={refType.value}>
                      {refType.displayName}
                    </option>
                  ))}
                </Select>
              </FormControl>

              <FormControl>
                <FormLabel>Reference ID</FormLabel>
                <Input
                  type="number"
                  value={referenceDocumentId}
                  onChange={(e) => setReferenceDocumentId(e.target.value)}
                  placeholder="Enter reference ID"
                />
              </FormControl>
            </HStack>

            {/* Note */}
            <FormControl>
              <FormLabel>Note</FormLabel>
              <Textarea
                value={note}
                onChange={(e) => setNote(e.target.value)}
                placeholder="Enter note (optional)"
                rows={3}
              />
            </FormControl>

            {enumsLoading && (
              <HStack justify="center" w="full">
                <Spinner size="sm" />
                <Text fontSize="sm" color="gray.600">Loading transaction options...</Text>
              </HStack>
            )}
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
            loadingText="Processing..."
          >
            {getModalTitle()}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default MaterialStockTransactionModal; 