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
  Box,
  Badge,
  useToast,
  Spinner,
} from '@chakra-ui/react';
import { 
  bulkAddMaterialStock, 
  bulkSubtractMaterialStock, 
  getMaterialTransactionEnums 
} from '../../services/materialInventoryService';
import { TransactionEnumsResponse } from '../../types/productInventory';

interface SelectedItem {
  id: number;
  sku: string;
  name: string;
}

interface MaterialBulkStockTransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
  selectedItems: SelectedItem[];
  transactionType: 'add' | 'subtract';
  onSuccess: () => void;
}

const MaterialBulkStockTransactionModal: React.FC<MaterialBulkStockTransactionModalProps> = ({
  isOpen,
  onClose,
  selectedItems,
  transactionType,
  onSuccess,
}) => {
  const [loading, setLoading] = useState(false);
  const [enumsLoading, setEnumsLoading] = useState(false);
  const [transactionEnums, setTransactionEnums] = useState<TransactionEnumsResponse | null>(null);
  const [formData, setFormData] = useState({
    quantity: '',
    note: '',
    referenceDocumentType: '',
    referenceDocumentId: '',
    transactionSource: '',
  });

  const toast = useToast();

  // Fetch transaction enums when modal opens
  useEffect(() => {
    if (isOpen && !transactionEnums) {
      fetchTransactionEnums();
    }
  }, [isOpen, transactionEnums]);

  const fetchTransactionEnums = async () => {
    setEnumsLoading(true);
    try {
      const enums = await getMaterialTransactionEnums();
      setTransactionEnums(enums);
    } catch (error) {
      console.error('Error fetching transaction enums:', error);
      toast({
        title: 'Error',
        description: 'Failed to load transaction options',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setEnumsLoading(false);
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSubmit = async () => {
    if (!formData.quantity || parseFloat(formData.quantity) <= 0) {
      toast({
        title: 'Invalid Quantity',
        description: 'Please enter a valid quantity greater than 0',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    setLoading(true);
    try {
      const variantIds = selectedItems.map(item => item.id);
      const quantityNum = parseFloat(formData.quantity);
      const referenceId = formData.referenceDocumentId ? parseInt(formData.referenceDocumentId) : undefined;

      if (transactionType === 'add') {
        await bulkAddMaterialStock(
          variantIds,
          quantityNum,
          formData.note || undefined,
          formData.referenceDocumentType || undefined,
          referenceId,
          formData.transactionSource || undefined
        );
        toast({
          title: 'Success',
          description: `Material stock added to ${selectedItems.length} item(s)`,
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      } else {
        await bulkSubtractMaterialStock(
          variantIds,
          quantityNum,
          formData.note || undefined,
          formData.referenceDocumentType || undefined,
          referenceId,
          formData.transactionSource || undefined
        );
        toast({
          title: 'Success',
          description: `Material stock subtracted from ${selectedItems.length} item(s)`,
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      }

      onSuccess();
      onClose();
      resetForm();
    } catch (error: any) {
      console.error('Error performing bulk operation:', error);
      toast({
        title: 'Error',
        description: error?.response?.data?.message || 'Failed to perform bulk operation',
        status: 'error',
        duration: 4000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      quantity: '',
      note: '',
      referenceDocumentType: '',
      referenceDocumentId: '',
      transactionSource: '',
    });
  };

  const handleClose = () => {
    resetForm();
    onClose();
  };

  const getDefaultTransactionSource = () => {
    if (transactionType === 'add') {
      return 'MATERIAL_ADJUSTMENT_INCREASE';
    } else {
      return 'MATERIAL_ADJUSTMENT_DECREASE';
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="xl">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>
          Bulk {transactionType === 'add' ? 'Add' : 'Subtract'} Material Stock
        </ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <VStack spacing={4}>
            {/* Selected Items Display */}
            <Box w="full">
              <Text fontWeight="medium" mb={2}>
                Selected Material Items ({selectedItems.length}):
              </Text>
              <Box maxH="200px" overflowY="auto" border="1px" borderColor="gray.200" borderRadius="md" p={2}>
                {selectedItems.map((item, index) => (
                  <HStack key={item.id} justify="space-between" mb={1}>
                    <Text fontSize="sm" noOfLines={1}>
                      {item.name}
                    </Text>
                    <Badge colorScheme="blue" fontSize="xs">
                      {item.sku}
                    </Badge>
                  </HStack>
                ))}
              </Box>
            </Box>

            {/* Form Fields */}
            <FormControl isRequired>
              <FormLabel>Quantity</FormLabel>
              <Input
                type="number"
                step="0.01"
                min="0.01"
                value={formData.quantity}
                onChange={(e) => handleInputChange('quantity', e.target.value)}
                placeholder="Enter quantity"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Note</FormLabel>
              <Textarea
                value={formData.note}
                onChange={(e) => handleInputChange('note', e.target.value)}
                placeholder="Optional note for this transaction"
                rows={3}
              />
            </FormControl>

            <HStack spacing={4} w="full">
              <FormControl>
                <FormLabel>Reference Document Type</FormLabel>
                <Select
                  value={formData.referenceDocumentType}
                  onChange={(e) => handleInputChange('referenceDocumentType', e.target.value)}
                  placeholder="Select reference type"
                  isDisabled={enumsLoading}
                >
                  {transactionEnums?.inventoryReferenceDocumentTypes?.map((type: any) => (
                    <option key={type.value} value={type.value}>
                      {type.displayName}
                    </option>
                  ))}
                </Select>
              </FormControl>

              <FormControl>
                <FormLabel>Reference Document ID</FormLabel>
                <Input
                  type="number"
                  value={formData.referenceDocumentId}
                  onChange={(e) => handleInputChange('referenceDocumentId', e.target.value)}
                  placeholder="Document ID"
                />
              </FormControl>
            </HStack>

            <FormControl>
              <FormLabel>Transaction Source</FormLabel>
              <Select
                value={formData.transactionSource || getDefaultTransactionSource()}
                onChange={(e) => handleInputChange('transactionSource', e.target.value)}
                placeholder="Select transaction source"
                isDisabled={enumsLoading}
              >
                {transactionEnums?.inventoryTransactionSourceTypes?.map((type: any) => (
                  <option key={type.value} value={type.value}>
                    {type.displayName}
                  </option>
                ))}
              </Select>
            </FormControl>

            {enumsLoading && (
              <HStack>
                <Spinner size="sm" />
                <Text fontSize="sm">Loading transaction options...</Text>
              </HStack>
            )}
          </VStack>
        </ModalBody>

        <ModalFooter>
          <Button variant="ghost" mr={3} onClick={handleClose}>
            Cancel
          </Button>
          <Button
            colorScheme={transactionType === 'add' ? 'green' : 'red'}
            onClick={handleSubmit}
            isLoading={loading}
            loadingText="Processing..."
          >
            {transactionType === 'add' ? 'Add' : 'Subtract'} Material Stock
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default MaterialBulkStockTransactionModal; 