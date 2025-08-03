'use client';

import React, { useState, useEffect } from 'react';
import {
  Box,
  Heading,
  VStack,
  HStack,
  FormControl,
  FormLabel,
  Input,
  FormErrorMessage,
  Button,
  Text,
  useToast,
  Card,
  CardBody,
  CardHeader,
  IconButton,
  Divider,
  Alert,
  AlertIcon,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { AddIcon, CloseIcon } from '@chakra-ui/icons';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

import ProductVariantSearchBar from '../../../../components/product/ProductVariantSearchBar';
import { createSaleTicket } from '../../../../services/saleService';
import { getAllProductVariantsForAutocomplete } from '../../../../services/productService';
import { SaleCreateRequest, SaleCreateDetailRequest } from '../../../../types/sale';
import { ProductVariantAutoComplete } from '../../../../types/product';

// Validation schema
const createSaleSchema = z.object({
  sku: z.string().optional().refine((val) => {
    if (!val) return true; // Allow empty
    return val.length >= 8 && val.length <= 12;
  }, 'SKU must be between 8 and 12 characters if provided'),
  expected_complete_date: z.string().min(1, 'Expected complete date is required'),
  customerName: z.string().min(1, 'Customer name is required'),
  customerEmail: z.string().email('Invalid email format').min(1, 'Customer email is required'),
  customerPhone: z.string().min(1, 'Customer phone is required'),
  customerAddress: z.string().min(1, 'Customer address is required'),
  total: z.string().min(1, 'Total is required'),
  promotion: z.string().min(1, 'Promotion is required'),
  netTotal: z.string().min(1, 'Net total is required'),
});

type CreateSaleFormData = z.infer<typeof createSaleSchema>;

interface SaleDetailItem {
  productVariantSku: string;
  productVariantName: string;
  quantity: string;
}

const CreateSalePage: React.FC = () => {
  const [saleDetails, setSaleDetails] = useState<SaleDetailItem[]>([]);
  const [selectedProductVariant, setSelectedProductVariant] = useState<ProductVariantAutoComplete | null>(null);
  const [quantity, setQuantity] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [productVariantsForAutocomplete, setProductVariantsForAutocomplete] = useState<ProductVariantAutoComplete[]>([]);
  
  const toast = useToast();
  const router = useRouter();

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    watch,
    setValue,
  } = useForm<CreateSaleFormData>({
    resolver: zodResolver(createSaleSchema),
    mode: 'onChange',
  });

  // Auto-calculate totals when details change
  useEffect(() => {
    const total = saleDetails.reduce((sum, detail) => {
      const quantity = parseFloat(detail.quantity) || 0;
      // Assuming unit price is 100 for demo - in real app this would come from product data
      return sum + (quantity * 100);
    }, 0);
    
    setValue('total', total.toString());
  }, [saleDetails, setValue]);

  // Auto-calculate net total when total or promotion changes
  useEffect(() => {
    const total = parseFloat(watch('total')) || 0;
    const promotion = parseFloat(watch('promotion')) || 0;
    const netTotal = total - promotion;
    
    setValue('netTotal', netTotal.toString());
  }, [watch('total'), watch('promotion'), setValue]);

  const handleProductVariantSelect = (productVariant: ProductVariantAutoComplete) => {
    setSelectedProductVariant(productVariant);
  };

  const handleProductVariantSearch = async (searchTerm: string) => {
    try {
      const data = await getAllProductVariantsForAutocomplete(searchTerm);
      setProductVariantsForAutocomplete(data);
    } catch (err) {
      console.error("Error fetching product variants:", err);
    }
  };

  const handleAddDetail = () => {
    if (!selectedProductVariant || !quantity || parseFloat(quantity) <= 0) {
      toast({
        title: 'Invalid Input',
        description: 'Please select a product variant and enter a valid quantity.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    // Check for duplicate product variant
    const isDuplicate = saleDetails.some(detail => detail.productVariantSku === selectedProductVariant.variantSku);
    if (isDuplicate) {
      toast({
        title: 'Duplicate Product',
        description: 'This product variant has already been added.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    const newDetail: SaleDetailItem = {
      productVariantSku: selectedProductVariant.variantSku,
      productVariantName: selectedProductVariant.variantName,
      quantity: quantity,
    };

    setSaleDetails([...saleDetails, newDetail]);
    setSelectedProductVariant(null);
    setQuantity('');
  };

  const handleRemoveDetail = (index: number) => {
    setSaleDetails(saleDetails.filter((_, i) => i !== index));
  };

  const handleCancel = () => {
    router.push('/sale');
  };

  const onSubmit = async (data: CreateSaleFormData) => {
    if (saleDetails.length === 0) {
      toast({
        title: 'No Products Added',
        description: 'Please add at least one product to the sale.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    setLoading(true);
    try {
      const saleDetailsRequest: SaleCreateDetailRequest[] = saleDetails.map(detail => ({
        productVariantSku: detail.productVariantSku,
        quantity: detail.quantity,
      }));

      const request: SaleCreateRequest = {
        sku: data.sku || '',
        total: data.total,
        promotion: data.promotion,
        netTotal: data.netTotal,
        expected_complete_date: data.expected_complete_date,
        details: saleDetailsRequest,
        customerName: data.customerName,
        customerEmail: data.customerEmail,
        customerPhone: data.customerPhone,
        customerAddress: data.customerAddress,
      };

      await createSaleTicket(request);
      
      toast({
        title: 'Success',
        description: 'Sale order created successfully!',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });

      router.push('/sale');
    } catch (error: any) {
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to create sale order.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box maxW="800px" mx="auto" p={6}>
      <Heading as="h1" size="xl" mb={6}>
        Create Sale Order
      </Heading>

      <form onSubmit={handleSubmit(onSubmit)}>
        <VStack spacing={6} align="stretch">
          {/* Basic Information */}
          <Card>
            <CardHeader>
              <Heading size="md">Basic Information</Heading>
            </CardHeader>
            <CardBody>
              <VStack spacing={4} align="stretch">
                <FormControl isInvalid={!!errors.sku}>
                  <FormLabel>SKU (Optional)</FormLabel>
                  <Input
                    {...register('sku')}
                    placeholder="Enter SKU"
                  />
                  <FormErrorMessage>{errors.sku?.message}</FormErrorMessage>
                  <Text fontSize="xs" color="gray.500" mt={1}>
                    SKU must be more than 8 characters and less than 12 characters. If SKU is missing, an automatic generated SKU will be used.
                  </Text>
                </FormControl>

                <FormControl isInvalid={!!errors.expected_complete_date}>
                  <FormLabel>Expected Complete Date *</FormLabel>
                  <Input
                    {...register('expected_complete_date')}
                    type="date"
                    required
                  />
                  <FormErrorMessage>{errors.expected_complete_date?.message}</FormErrorMessage>
                </FormControl>

                <FormControl isInvalid={!!errors.customerName}>
                  <FormLabel>Customer Name *</FormLabel>
                  <Input
                    {...register('customerName')}
                    placeholder="Enter customer name"
                    required
                  />
                  <FormErrorMessage>{errors.customerName?.message}</FormErrorMessage>
                </FormControl>

                <FormControl isInvalid={!!errors.customerEmail}>
                  <FormLabel>Customer Email *</FormLabel>
                  <Input
                    {...register('customerEmail')}
                    type="email"
                    placeholder="Enter customer email"
                    required
                  />
                  <FormErrorMessage>{errors.customerEmail?.message}</FormErrorMessage>
                </FormControl>

                <FormControl isInvalid={!!errors.customerPhone}>
                  <FormLabel>Customer Phone *</FormLabel>
                  <Input
                    {...register('customerPhone')}
                    placeholder="Enter customer phone"
                    required
                  />
                  <FormErrorMessage>{errors.customerPhone?.message}</FormErrorMessage>
                </FormControl>

                <FormControl isInvalid={!!errors.customerAddress}>
                  <FormLabel>Customer Address *</FormLabel>
                  <Input
                    {...register('customerAddress')}
                    placeholder="Enter customer address"
                    required
                  />
                  <FormErrorMessage>{errors.customerAddress?.message}</FormErrorMessage>
                </FormControl>
              </VStack>
            </CardBody>
          </Card>

          {/* Product Details */}
          <Card>
            <CardHeader>
              <Heading size="md">Product Details</Heading>
            </CardHeader>
            <CardBody>
              <VStack spacing={4} align="stretch">
                <HStack spacing={4} align="end">
                  <Box flex={1}>
                    <FormLabel>Product Variant *</FormLabel>
                    <ProductVariantSearchBar
                      onSearch={handleProductVariantSearch}
                      onSelectProductVariantObject={handleProductVariantSelect}
                      selectedProductVariant={selectedProductVariant}
                      productVariantsForAutocomplete={productVariantsForAutocomplete}
                    />
                  </Box>
                  <Box flex={1}>
                    <FormLabel>Quantity *</FormLabel>
                    <Input
                      type="number"
                      min="1"
                      value={quantity}
                      onChange={(e) => setQuantity(e.target.value)}
                      placeholder="Enter quantity"
                    />
                  </Box>
                  <Button
                    colorScheme="blue"
                    onClick={handleAddDetail}
                    isDisabled={!selectedProductVariant || !quantity}
                  >
                    <AddIcon />
                  </Button>
                </HStack>

                {saleDetails.length > 0 && (
                  <VStack spacing={2} align="stretch">
                    <Text fontWeight="semibold">Added Products:</Text>
                    {saleDetails.map((detail, index) => (
                      <HStack key={index} justify="space-between" p={3} bg="gray.50" borderRadius="md">
                        <VStack align="start" spacing={1}>
                          <Text fontWeight="medium">{detail.productVariantName}</Text>
                          <Text fontSize="sm" color="gray.600">SKU: {detail.productVariantSku}</Text>
                        </VStack>
                        <HStack spacing={2}>
                          <Text fontWeight="medium">Qty: {detail.quantity}</Text>
                          <IconButton
                            aria-label="Remove product"
                            icon={<CloseIcon />}
                            size="sm"
                            colorScheme="red"
                            variant="ghost"
                            onClick={() => handleRemoveDetail(index)}
                          />
                        </HStack>
                      </HStack>
                    ))}
                  </VStack>
                )}
              </VStack>
            </CardBody>
          </Card>

          {/* Totals */}
          <Card>
            <CardHeader>
              <Heading size="md">Order Totals</Heading>
            </CardHeader>
            <CardBody>
              <VStack spacing={4} align="stretch">
                <FormControl isInvalid={!!errors.total}>
                  <FormLabel>Total *</FormLabel>
                  <Input
                    {...register('total')}
                    type="number"
                    step="0.01"
                    placeholder="0.00"
                    required
                    onChange={(e) => {
                      const value = parseFloat(e.target.value) || 0;
                      const promotion = parseFloat(watch('promotion')) || 0;
                      const netTotal = value - promotion;
                      setValue('netTotal', netTotal.toString());
                    }}
                  />
                  <FormErrorMessage>{errors.total?.message}</FormErrorMessage>
                </FormControl>

                <FormControl isInvalid={!!errors.promotion}>
                  <FormLabel>Promotion *</FormLabel>
                  <Input
                    {...register('promotion')}
                    type="number"
                    step="0.01"
                    placeholder="0.00"
                    required
                    onChange={(e) => {
                      const total = parseFloat(watch('total')) || 0;
                      const value = parseFloat(e.target.value) || 0;
                      const netTotal = total - value;
                      setValue('netTotal', netTotal.toString());
                    }}
                  />
                  <FormErrorMessage>{errors.promotion?.message}</FormErrorMessage>
                </FormControl>

                <FormControl isInvalid={!!errors.netTotal}>
                  <FormLabel>Net Total *</FormLabel>
                  <Input
                    {...register('netTotal')}
                    type="number"
                    step="0.01"
                    placeholder="0.00"
                    required
                    readOnly
                    bg="gray.50"
                  />
                  <FormErrorMessage>{errors.netTotal?.message}</FormErrorMessage>
                </FormControl>
              </VStack>
            </CardBody>
          </Card>

          {/* Action Buttons */}
          <HStack spacing={4} justify="end">
            <Button onClick={handleCancel} variant="outline">
              Cancel
            </Button>
            <Button
              type="submit"
              colorScheme="blue"
              isLoading={loading}
              isDisabled={!isValid || saleDetails.length === 0}
            >
              Create Sale Order
            </Button>
          </HStack>
        </VStack>
      </form>
    </Box>
  );
};

export default CreateSalePage; 