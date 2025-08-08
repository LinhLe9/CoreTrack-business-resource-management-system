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
import { getProductTransactionEnums, addProductStock, subtractProductStock, setProductStock } from '../../services/productInventoryService';
import { TransactionEnumsResponse, EnumValue } from '../../types/productInventory';

interface ProductStockTransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
  variantId: number;
  variantSku: string;
  variantName: string;
  transactionType: 'add' | 'subtract' | 'set';
  onSuccess?: () => void;
}

const ProductStockTransactionModal: React.FC<ProductStockTransactionModalProps> = ({
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
      const enums = await getProductTransactionEnums();
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

  const handleSubmit = async () => {
    if (!quantity || parseFloat(quantity) <= 0) {
      toast({
        title: 'Invalid Quantity',
        description: 'Please enter a valid quantity greater than 0',
        status: 'error',
        duration: 3000,
      });
      return;
    }

    setLoading(true);
    try {
      const quantityNum = parseFloat(quantity);
      const referenceId = referenceDocumentId ? parseInt(referenceDocumentId) : undefined;

      if (transactionType === 'add') {
        await addProductStock(
          variantId,
          quantityNum,
          note || undefined,
          referenceDocumentType || undefined,
          referenceId,
          transactionSource || undefined
        );
      } else if (transactionType === 'subtract') {
        await subtractProductStock(
          variantId,
          quantityNum,
          note || undefined,
          referenceDocumentType || undefined,
          referenceId,
          transactionSource || undefined
        );
      } else if (transactionType === 'set') {
        await setProductStock(
          variantId,
          quantityNum,
          note || undefined,
          referenceDocumentType || undefined,
          referenceId,
          transactionSource || undefined
        );
      }

      toast({
        title: 'Success',
        description: `Product stock ${transactionType === 'add' ? 'added' : transactionType === 'subtract' ? 'subtracted' : 'set'} successfully`,
        status: 'success',
        duration: 3000,
      });

      onSuccess?.();
      handleClose();
    } catch (error: any) {
      console.error('Error processing product transaction:', error);
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to process product transaction',
        status: 'error',
        duration: 5000,
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

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="lg">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>
          {transactionType === 'add' ? 'Add Product Stock' : transactionType === 'subtract' ? 'Subtract Product Stock' : 'Set Product Stock'} - {variantName}
        </ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <VStack spacing={4}>
            {/* Variant SKU (readonly) */}
            <FormControl>
              <FormLabel>Product Variant SKU</FormLabel>
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
            colorScheme={transactionType === 'add' ? 'green' : transactionType === 'subtract' ? 'red' : 'blue'}
            onClick={handleSubmit}
            isLoading={loading}
            loadingText="Processing..."
          >
            {transactionType === 'add' ? 'Add Product Stock' : transactionType === 'subtract' ? 'Subtract Product Stock' : 'Set Product Stock'}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default ProductStockTransactionModal;
