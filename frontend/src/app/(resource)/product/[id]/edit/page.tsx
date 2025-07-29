'use client';

import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  Textarea,
  VStack,
  Heading,
  useToast,
  FormErrorMessage,
  Select,
  HStack,
  Text,
  List,
  ListItem,
  IconButton,
  Flex,
  FormHelperText,
  useDisclosure,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalCloseButton,
  ModalBody,
  ModalFooter,
  Spinner,
  Center,
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
  Image,
} from '@chakra-ui/react';
import { useRouter, useParams } from 'next/navigation';
import { AddIcon, DeleteIcon } from '@chakra-ui/icons';
import { getProductById, updateProduct, getAllProductGroups } from '@/services/productService';
import { getAllMaterialsForAutocomplete } from '@/services/materialService';
import { ProductDetailResponse } from '@/types/product';
import { MaterialAutoComplete } from '@/types/material';
import { Group } from '@/types/product';
import { AxiosError } from 'axios';
import SupabaseUpload from '@/components/upload/SupabaseUpload';
import SearchProductGroup from '@/components/product/SearchProductGroup';
import MaterialSelectBar from '@/components/product/MaterialSelectBar';

interface EditProductForm {
  name: string;
  description: string;
  price: number;
  currency: string;
  imageUrl: string;
  productGroupId: string;
  newProductGroupName: string;
  variants: EditVariant[];
}

interface EditVariant {
  id?: number; // For existing variants
  name: string;
  description: string;
  imageUrl: string;
  bomItems: EditBOMItem[];
}

interface EditBOMItem {
  materialId: number;
  materialName: string;
  quantity: string; // BigDecimal from backend
  notes?: string;
}

const EditProductPage: React.FC = () => {
  const params = useParams();
  const id = params?.id;
  const router = useRouter();
  const toast = useToast();

  const [form, setForm] = useState<EditProductForm>({
    name: '',
    description: '',
    price: 0,
    currency: 'USD',
    imageUrl: '',
    productGroupId: '',
    newProductGroupName: '',
    variants: []
  });

  const [allGroups, setAllGroups] = useState<Group[]>([]);
  const [groupSearch, setGroupSearch] = useState('');
  const [filteredGroups, setFilteredGroups] = useState<Group[]>([]);
  const [newGroupName, setNewGroupName] = useState('');
  const [groupError, setGroupError] = useState('');
  const { isOpen, onOpen, onClose } = useDisclosure();

  const [errors, setErrors] = useState<{[key: string]: string}>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [materialsForAutocomplete, setMaterialsForAutocomplete] = useState<MaterialAutoComplete[]>([]);
  
  const currencyList = [
    { code: 'USD', currency: 'US Dollar' },
    { code: 'EUR', currency: 'Euro' },
    { code: 'GBP', currency: 'British Pound' },
    { code: 'JPY', currency: 'Japanese Yen' },
    { code: 'VND', currency: 'Vietnamese Dong' },
  ];

  // Fetch product data
  useEffect(() => {
    const fetchProduct = async () => {
      if (!id) return;
      
      try {
        setIsLoading(true);
        const productData = await getProductById(Number(id));
        
        // Map ProductDetailResponse to EditProductForm
        setForm({
          name: productData.name,
          description: productData.description || '',
          price: productData.price || 0,
          currency: productData.currency || 'USD',
          imageUrl: productData.imageUrl || '',
          productGroupId: productData.group || '',
          newProductGroupName: '',
          variants: productData.variants?.map(v => ({
            id: v.variant.id,
            name: v.variant.name,
            description: v.variant.description || '',
            imageUrl: v.variant.imageUrl || '',
            bomItems: v.variant.bomItems?.map(bom => ({
              materialId: bom.materialId,
              materialName: bom.materialName,
              quantity: bom.quantity,
              notes: bom.notes
            })) || []
          })) || []
        });
      } catch (error) {
        console.error('Error fetching product:', error);
        toast({
          title: 'Error',
          description: 'Failed to load product data',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchProduct();
  }, [id, toast]);

  // Fetch all product groups
  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const groups = await getAllProductGroups();
        setAllGroups(groups);
      } catch (error) {
        console.error('Error fetching groups:', error);
      }
    };
    fetchGroups();
  }, []);

  // Fetch materials for autocomplete
  useEffect(() => {
    const fetchMaterials = async () => {
      try {
        const materials = await getAllMaterialsForAutocomplete();
        setMaterialsForAutocomplete(materials);
      } catch (error) {
        console.error('Error fetching materials:', error);
      }
    };
    fetchMaterials();
  }, []);

  useEffect(() => {
    const q = groupSearch.toLowerCase();
    setFilteredGroups(allGroups.filter(g => g.name.toLowerCase().includes(q)));
  }, [groupSearch, allGroups]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: name === 'price' ? (value === '' ? 0 : parseFloat(value)) : value,
    }));
  };

  const handleGroupSelect = (group: Group) => {
    setForm(prev => ({ ...prev, productGroupId: group.id.toString(), newProductGroupName: '' }));
    setGroupSearch(group.name);
  };

  const handleNewGroupSubmit = () => {
    const exists = allGroups.some(g => g.name.toLowerCase() === newGroupName.toLowerCase());
    if (exists) {
      setGroupError('Group name already exists.');
      return;
    }
    setForm(prev => ({ ...prev, newProductGroupName: newGroupName.trim(), productGroupId: '' }));
    setGroupSearch(newGroupName.trim());
    setGroupError('');
    setNewGroupName('');
    onClose();
  };

  const handleVariantChange = (
    index: number,
    field: 'name' | 'description' | 'imageUrl',
    value: string
  ) => {
    const updatedVariants = [...form.variants];
    updatedVariants[index] = {
      ...updatedVariants[index],
      [field]: value,
    };
    setForm(prev => ({ ...prev, variants: updatedVariants }));
  };

  const addVariant = () => {
    setForm(prev => ({
      ...prev,
      variants: [...prev.variants, {
        name: '',
        description: '',
        imageUrl: '',
        bomItems: []
      }]
    }));
  };

  const removeVariant = (index: number) => {
    const updated = form.variants.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, variants: updated }));
  };

  const handleBOMItemChange = (
    variantIndex: number,
    bomIndex: number,
    field: 'quantity' | 'notes',
    value: string
  ) => {
    const updatedVariants = [...form.variants];
    const updatedBOMItems = [...updatedVariants[variantIndex].bomItems];
    updatedBOMItems[bomIndex] = {
      ...updatedBOMItems[bomIndex],
      [field]: value,
    };
    updatedVariants[variantIndex] = {
      ...updatedVariants[variantIndex],
      bomItems: updatedBOMItems
    };
    setForm(prev => ({ ...prev, variants: updatedVariants }));
  };

  const addBOMItem = (variantIndex: number) => {
    const updatedVariants = [...form.variants];
    updatedVariants[variantIndex] = {
      ...updatedVariants[variantIndex],
                  bomItems: [...updatedVariants[variantIndex].bomItems, {
              materialId: 0,
              materialName: '',
              quantity: '1.00',
              notes: ''
            }]
    };
    setForm(prev => ({ ...prev, variants: updatedVariants }));
  };

  const removeBOMItem = (variantIndex: number, bomIndex: number) => {
    const updatedVariants = [...form.variants];
    const updatedBOMItems = updatedVariants[variantIndex].bomItems.filter((_, i) => i !== bomIndex);
    updatedVariants[variantIndex] = {
      ...updatedVariants[variantIndex],
      bomItems: updatedBOMItems
    };
    setForm(prev => ({ ...prev, variants: updatedVariants }));
  };

  const handleMaterialSelect = (variantIndex: number, bomIndex: number, material: MaterialAutoComplete) => {
    const updatedVariants = [...form.variants];
    updatedVariants[variantIndex].bomItems[bomIndex] = {
      ...updatedVariants[variantIndex].bomItems[bomIndex],
      materialId: material.id,
      materialName: material.name
    };
    setForm(prev => ({ ...prev, variants: updatedVariants }));
  };

  const validateForm = (): boolean => {
    const newErrors: {[key: string]: string} = {};

    if (!form.name.trim()) {
      newErrors.name = 'Product name is required';
    }

    if (form.price <= 0) {
      newErrors.price = 'Price must be greater than 0';
    }

    // Validate variants
    for (let i = 0; i < form.variants.length; i++) {
      const variant = form.variants[i];
      if (!variant.name.trim()) {
        toast({
          title: 'Validation Error',
          description: `Variant ${i + 1} name is required`,
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
        return false;
      }

      // Validate BOM items
      for (let j = 0; j < variant.bomItems.length; j++) {
        const bomItem = variant.bomItems[j];
        if (bomItem.materialId === 0) {
          toast({
            title: 'Validation Error',
            description: `Please select a material for variant ${i + 1}, BOM item ${j + 1}`,
            status: 'error',
            duration: 5000,
            isClosable: true,
          });
          return false;
        }
        const quantity = parseFloat(bomItem.quantity);
        if (isNaN(quantity) || quantity <= 0) {
          toast({
            title: 'Validation Error',
            description: `Quantity must be greater than 0 for variant ${i + 1}, BOM item ${j + 1}`,
            status: 'error',
            duration: 5000,
            isClosable: true,
          });
          return false;
        }
      }
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    try {
      // Map form data to backend expected format
      const productData = {
        name: form.name,
        description: form.description,
        price: form.price,
        currency: form.currency,
        imageUrl: form.imageUrl,
        productGroupId: form.productGroupId || null,
        newProductGroupName: form.newProductGroupName || null,
        variants: form.variants.map(variant => ({
          id: variant.id, // Include ID for existing variants
          name: variant.name,
          description: variant.description,
          imageUrl: variant.imageUrl,
          bomItems: variant.bomItems.map(bom => ({
            materialId: bom.materialId,
            quantity: bom.quantity, // Already a string from BigDecimal
            notes: bom.notes
          }))
        }))
      };

      await updateProduct(Number(id), productData);
      
      toast({
        title: 'Success',
        description: 'Product updated successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });

      router.push(`/product/${id}`);
    } catch (err: any) {
      console.error('Error updating product:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to update product',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  return (
    <Box maxW="800px" mx="auto" p={6}>
      <Heading as="h1" size="xl" mb={6}>
        Edit Product: {form.name}
      </Heading>

      <form onSubmit={handleSubmit}>
        <VStack spacing={4} align="stretch">
          {/* Product Name */}
          <FormControl isRequired isInvalid={!!errors.name}>
            <FormLabel>Product Name</FormLabel>
            <Input
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Enter product name"
              maxLength={255}
            />
            <FormErrorMessage>{errors.name}</FormErrorMessage>
          </FormControl>

          {/* Description */}
          <FormControl>
            <FormLabel>Description</FormLabel>
            <Textarea
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Enter product description"
            />
          </FormControl>

          {/* Price */}
          <FormControl isRequired isInvalid={!!errors.price}>
            <FormLabel>Price</FormLabel>
            <NumberInput
              value={form.price}
              onChange={(valueString) => {
                setForm(prev => ({
                  ...prev,
                  price: parseFloat(valueString) || 0
                }));
              }}
              min={0}
              step={0.01}
            >
              <NumberInputField />
              <NumberInputStepper>
                <NumberIncrementStepper />
                <NumberDecrementStepper />
              </NumberInputStepper>
            </NumberInput>
            <FormErrorMessage>{errors.price}</FormErrorMessage>
          </FormControl>

          {/* Currency */}
          <FormControl isRequired>
            <FormLabel>Currency</FormLabel>
            <Select name="currency" value={form.currency} onChange={handleChange}>
              {currencyList.map(curr => (
                <option key={curr.code} value={curr.code}>
                  {curr.code} - {curr.currency}
                </option>
              ))}
            </Select>
          </FormControl>

          {/* Product Group */}
          <FormControl>
            <FormLabel>Product Group</FormLabel>
            <HStack>
              <SearchProductGroup
                onSelect={handleGroupSelect}
              />
              <IconButton aria-label="Add group" icon={<AddIcon />} onClick={onOpen} />
            </HStack>
          </FormControl>

          {/* Product Image */}
          <FormControl>
            <FormLabel>Product Image</FormLabel>
            <SupabaseUpload
              onUpload={(url) => {
                setForm(prev => ({
                  ...prev,
                  imageUrl: url,
                }));
                toast({
                  title: "Image uploaded",
                  description: "Image uploaded successfully",
                  status: "success",
                  duration: 3000,
                });
              }}
              folder="products"
              maxSize={5}
            />
            {form.imageUrl && (
              <Box mt={3} p={4} bg="gray.50" border="1px solid" borderColor="gray.200" borderRadius="md">
                <Flex justify="space-between" align="center" mb={3}>
                  <Text fontWeight="bold" color="gray.700">Image Preview</Text>
                  <IconButton
                    aria-label="Delete image"
                    icon={<DeleteIcon />}
                    size="sm"
                    colorScheme="red"
                    variant="ghost"
                    onClick={() => {
                      setForm(prev => ({ ...prev, imageUrl: '' }));
                      toast({
                        title: "Image removed",
                        description: "Product image has been removed.",
                        status: "info",
                        duration: 2000,
                      });
                    }}
                  />
                </Flex>
                <Image src={form.imageUrl} alt="Product" maxH="200px" objectFit="contain" />
              </Box>
            )}
          </FormControl>

          {/* Variants Section */}
          <Box>
            <Flex justify="space-between" align="center" mb={4}>
              <Heading as="h3" size="md">Variants</Heading>
              <Button leftIcon={<AddIcon />} onClick={addVariant} size="sm">
                Add Variant
              </Button>
            </Flex>

            {form.variants.map((variant, variantIndex) => (
              <Box key={variantIndex} p={4} border="1px solid" borderColor="gray.200" borderRadius="md" mb={4}>
                <Flex justify="space-between" align="center" mb={3}>
                  <Text fontWeight="bold">Variant {variantIndex + 1}</Text>
                  <IconButton
                    aria-label="Remove variant"
                    icon={<DeleteIcon />}
                    size="sm"
                    colorScheme="red"
                    variant="ghost"
                    onClick={() => removeVariant(variantIndex)}
                  />
                </Flex>

                <VStack spacing={3} align="stretch">
                  <FormControl isRequired>
                    <FormLabel>Variant Name</FormLabel>
                    <Input
                      value={variant.name}
                      onChange={(e) => handleVariantChange(variantIndex, 'name', e.target.value)}
                      placeholder="Enter variant name"
                    />
                  </FormControl>

                  <FormControl>
                    <FormLabel>Description</FormLabel>
                    <Textarea
                      value={variant.description}
                      onChange={(e) => handleVariantChange(variantIndex, 'description', e.target.value)}
                      placeholder="Enter variant description"
                    />
                  </FormControl>

                  <FormControl>
                    <FormLabel>Variant Image</FormLabel>
                    <SupabaseUpload
                      onUpload={(url) => {
                        handleVariantChange(variantIndex, 'imageUrl', url);
                        toast({
                          title: "Image uploaded",
                          description: "Variant image uploaded successfully",
                          status: "success",
                          duration: 3000,
                        });
                      }}
                      folder="products"
                      maxSize={5}
                    />
                    {variant.imageUrl && (
                      <Box mt={2}>
                        <Image src={variant.imageUrl} alt="Variant" maxH="100px" objectFit="contain" />
                      </Box>
                    )}
                  </FormControl>

                  {/* BOM Items */}
                  <Box>
                    <Flex justify="space-between" align="center" mb={3}>
                      <Text fontWeight="bold">BOM Items</Text>
                      <Button size="sm" onClick={() => addBOMItem(variantIndex)}>
                        Add BOM Item
                      </Button>
                    </Flex>

                    {variant.bomItems.map((bomItem, bomIndex) => (
                      <Box key={bomIndex} p={3} bg="gray.50" borderRadius="md" mb={2}>
                        <Flex justify="space-between" align="center" mb={2}>
                          <Text fontWeight="medium">BOM Item {bomIndex + 1}</Text>
                          <IconButton
                            aria-label="Remove BOM item"
                            icon={<DeleteIcon />}
                            size="xs"
                            colorScheme="red"
                            variant="ghost"
                            onClick={() => removeBOMItem(variantIndex, bomIndex)}
                          />
                        </Flex>

                        <VStack spacing={2} align="stretch">
                          <FormControl isRequired>
                            <FormLabel>Material</FormLabel>
                            <MaterialSelectBar
                              onSelect={(material) => handleMaterialSelect(variantIndex, bomIndex, material)}
                              selectedMaterial={materialsForAutocomplete.find(m => m.id === bomItem.materialId)}
                            />
                          </FormControl>

                          <FormControl isRequired>
                            <FormLabel>Quantity</FormLabel>
                            <NumberInput
                              value={parseFloat(bomItem.quantity) || 0}
                              onChange={(valueString) => {
                                const numValue = parseFloat(valueString) || 0;
                                handleBOMItemChange(variantIndex, bomIndex, 'quantity', numValue.toFixed(2));
                              }}
                              min={0}
                              step={0.01}
                            >
                              <NumberInputField />
                              <NumberInputStepper>
                                <NumberIncrementStepper />
                                <NumberDecrementStepper />
                              </NumberInputStepper>
                            </NumberInput>
                          </FormControl>

                          <FormControl>
                            <FormLabel>Notes</FormLabel>
                            <Textarea
                              value={bomItem.notes || ''}
                              onChange={(e) => handleBOMItemChange(variantIndex, bomIndex, 'notes', e.target.value)}
                              placeholder="Enter notes (optional)"
                            />
                          </FormControl>
                        </VStack>
                      </Box>
                    ))}
                  </Box>
                </VStack>
              </Box>
            ))}
          </Box>

          <HStack spacing={4}>
            <Button type="submit" colorScheme="teal" isLoading={isSubmitting}>
              Update Product
            </Button>
            <Button onClick={() => router.push(`/product/${id}`)}>
              Cancel
            </Button>
          </HStack>
        </VStack>
      </form>

      {/* Add New Group Modal */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Add New Product Group</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl isInvalid={!!groupError}>
              <FormLabel>Group Name</FormLabel>
              <Input
                value={newGroupName}
                onChange={(e) => setNewGroupName(e.target.value)}
                placeholder="Enter group name"
              />
              <FormErrorMessage>{groupError}</FormErrorMessage>
            </FormControl>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={handleNewGroupSubmit}>
              Add Group
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default EditProductPage; 