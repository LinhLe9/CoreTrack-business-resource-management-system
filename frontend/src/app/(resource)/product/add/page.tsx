// pages/products/add.tsx
"use client";

import {
  Box, Button, FormControl, FormLabel, Input, Textarea, VStack, Heading, useToast, HStack, 
  IconButton, List, ListItem, Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalFooter, 
  ModalCloseButton, useDisclosure, FormHelperText, Select, Text, Flex
} from '@chakra-ui/react';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/axios';
import { AddIcon, DeleteIcon } from '@chakra-ui/icons';
import { AddProductForm, BOMItem, Group} from '@/types/product';
import SearchProductGroup  from '@/components/product/SearchProductGroup'
import MaterialSearchBar from '@/components/material/MaterialSearchBar';
import currencyCodes from 'currency-codes';
import { AxiosError } from 'axios';
import SupabaseUpload from '@/components/upload/SupabaseUpload';
import { MaterialAutoComplete } from '@/types/material';
import { getMaterialById, getAllMaterialsForAutocomplete } from '@/services/materialService';


const AddProductPage = () => {
  const [form, setForm] = useState<AddProductForm>({
    name: '',
    description: '',
    sku: '',
    price: 0,
    currency: 'USD',
    imageUrl: '',
    productGroupId: '',
    newProductGroupName: '',
    variants: [],
    bomItems: []
  });

  const [allGroups, setAllGroups] = useState<Group[]>([]);
  const [groupSearch, setGroupSearch] = useState('');
  const [filteredGroups, setFilteredGroups] = useState<Group[]>([]);
  const [newGroupName, setNewGroupName] = useState('');
  const [groupError, setGroupError] = useState('');
  const [materialsForAutocomplete, setMaterialsForAutocomplete] = useState<MaterialAutoComplete[]>([]);
  const { isOpen, onOpen, onClose } = useDisclosure();
  const currencyList = currencyCodes.data.filter(c => c.code);

  const toast = useToast();
  const router = useRouter();

  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const res = await api.get('/products/product-groups');
        setAllGroups(res.data); // res.data is array of GROUP object
      } catch (error: unknown){
        const axiosError = error as AxiosError;
        console.error("Fetch error: ", axiosError);

        toast({ 
          title: 'Failed to load groups', 
          description: axiosError.response?.data 
            ? JSON.stringify(axiosError.response.data)
            : axiosError.message,
          status: 'error', 
          duration: 5000,
          isClosable: true,
        });
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

  // handle change of product's general infor 
  const handleChange = (
  e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
  const { name, value } = e.target;

    setForm(prev => ({
      ...prev,
      [name]: name === 'price' ? (value === '' ? 0 : parseFloat(value)) : value,
    }));
  };

  // handle the product group change 
  const handleGroupSelect = (group: Group) => {
    setForm(prev => ({ ...prev, productGroupId: group.id.toString(), newProductGroupName: '' }));
    setGroupSearch(group.name);
  };

  // Add new product group submit
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

  // handle change of product variants' general infor
  const handleVariantChange = (
    index: number,
    field: 'name' | 'shortDescription' | 'imageUrl',
    value: string
  ) => {
    const updatedVariants = [...form.variants];
    updatedVariants[index] = {
      ...updatedVariants[index],
      [field]: value,
    };
    setForm(prev => ({ ...prev, variants: updatedVariants }));
  };

  // Handle BOM items change inside variant
  const handleVariantBOMChange = (
    variantIndex: number,
    bomIndex: number,
    field: keyof BOMItem,
    value: string
  ) => {
    const updatedVariants = [...form.variants];
    const bomItems = updatedVariants[variantIndex].bomItems || [];

    if (field === 'quantity') {
      bomItems[bomIndex].quantity = parseFloat(value).toFixed(2);
    } else if (field === 'materialSku') {
      bomItems[bomIndex].materialSku = value;
    } else if (field === 'uom') {
      bomItems[bomIndex].uom = value;
    }

    updatedVariants[variantIndex].bomItems = bomItems;
    setForm(prev => ({ ...prev, variants: updatedVariants }));
  };

  // handle add/ remove variant 
  const addVariant = () => {
    setForm(prev => ({
      ...prev,
      variants: [
        ...prev.variants,
        {
          name: '',
          shortDescription: '',
          imageUrl: '',
          bomItems: [],
        },
      ],
    }));
  };

  const removeVariant = (index: number) => {
    const updated = form.variants.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, variants: updated }));
  };

  // Add/Remove BOM items for variant
  const addVariantBOMItem = (variantIndex: number) => {
    const updated = [...form.variants];
    updated[variantIndex].bomItems.push({ materialSku: '', quantity: '1.00', uom: '' });
    setForm(prev => ({ ...prev, variants: updated }));
  };

  const removeVariantBOMItem = (variantIndex: number, bomIndex: number) => {
    const updated = [...form.variants];
    updated[variantIndex].bomItems = updated[variantIndex].bomItems.filter((_, i) => i !== bomIndex);
    setForm(prev => ({ ...prev, variants: updated }));
  };

  // No variant BOM Items handlers (bomItems of product itself)
  const handleBOMChange = (
    index: number, 
    field: keyof BOMItem, 
    value: string
  ) => {
    const updated = [...form.bomItems];
    if (field === 'quantity') {
      updated[index].quantity = parseFloat(value).toFixed(2);
    } else if (field === 'materialSku') {
      updated[index].materialSku = value;
    } else if (field === 'uom') {
      updated[index].uom = value;
    }
    setForm(prev => ({ ...prev, bomItems: updated }));
  };

  const addBOMItem = () => {
    setForm(prev => ({ ...prev, bomItems: [...prev.bomItems, { materialSku: '', quantity: '1.00', uom: '' }] }));
  };

  const removeBOMItem = (index: number) => {
    const updated = form.bomItems.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, bomItems: updated }));
  };

  // Handle material selection and auto-fill UOM
  const handleMaterialSelect = async (materialId: number, bomIndex: number) => {
    try {
      // Find material from autocomplete list
      const material = materialsForAutocomplete.find(m => m.id === materialId);
      if (!material) return;

      // Get material details to get UOM
      const materialDetails = await getMaterialById(material.id);
      
      // Update BOM item with material SKU and UOM
      const updated = [...form.bomItems];
      updated[bomIndex] = {
        ...updated[bomIndex],
        materialSku: material.sku,
        uom: materialDetails.uom || ''
      };
      
      setForm(prev => ({ ...prev, bomItems: updated }));
    } catch (error) {
      console.error('Error fetching material details:', error);
      // Fallback: just update SKU without UOM
      const material = materialsForAutocomplete.find(m => m.id === materialId);
      if (material) {
        const updated = [...form.bomItems];
        updated[bomIndex] = {
          ...updated[bomIndex],
          materialSku: material.sku
        };
        setForm(prev => ({ ...prev, bomItems: updated }));
      }
    }
  };

  // Handle variant BOM material selection
  const handleVariantMaterialSelect = async (materialId: number, variantIndex: number, bomIndex: number) => {
    try {
      // Find material from autocomplete list
      const material = materialsForAutocomplete.find(m => m.id === materialId);
      if (!material) return;

      // Get material details to get UOM
      const materialDetails = await getMaterialById(material.id);
      
      // Update variant BOM item with material SKU and UOM
      const updatedVariants = [...form.variants];
      const bomItems = updatedVariants[variantIndex].bomItems || [];
      bomItems[bomIndex] = {
        ...bomItems[bomIndex],
        materialSku: material.sku,
        uom: materialDetails.uom || ''
      };
      updatedVariants[variantIndex].bomItems = bomItems;
      
      setForm(prev => ({ ...prev, variants: updatedVariants }));
    } catch (error) {
      console.error('Error fetching material details:', error);
      // Fallback: just update SKU without UOM
      const material = materialsForAutocomplete.find(m => m.id === materialId);
      if (material) {
        const updatedVariants = [...form.variants];
        const bomItems = updatedVariants[variantIndex].bomItems || [];
        bomItems[bomIndex] = {
          ...bomItems[bomIndex],
          materialSku: material.sku
        };
        updatedVariants[variantIndex].bomItems = bomItems;
        setForm(prev => ({ ...prev, variants: updatedVariants }));
      }
    }
  };

  // handle submit form 
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const productGroupIdNum = parseInt(form.productGroupId);
    const payload: any = {
      ...form,
      price: isNaN(form.price) ? 0 : parseFloat(form.price as any),
      productGroupId: !isNaN(productGroupIdNum) ? productGroupIdNum : null,
    };

    if (form.variants.length === 0) {
      payload.variants = [
        {
          name: form.name,
          shortDescription: form.description,
          imageUrl: form.imageUrl,
          bomItems: form.bomItems.map(item => ({
            materialSku: item.materialSku,
            quantity: parseFloat(item.quantity),
        })),
      },
    ];
    } else {
      payload.variants = form.variants.map(variant => ({
        ...variant,
        bomItems: variant.bomItems.map(item => ({
          materialSku: item.materialSku,
          quantity: parseFloat(item.quantity),
        })),
      }));
    }

    try {
      console.log("Payload send:", payload);
      await api.post('/products/add-product', payload);
      toast({ title: 'Product added successfully!', status: 'success', duration: 3000 });
      router.push('/product');
    } catch (err: any) {
      toast({
        title: 'Failed to add product',
        description: err?.response?.data?.message || 'Check your data and try again.',
        status: 'error',
        duration: 4000,
      });
    }
  };

  return (
    <Box maxW="700px" mx="auto" p={6}>
      <Heading mb={6}>Add New Product</Heading>
      <form onSubmit={handleSubmit}>
        <VStack spacing={4} align="stretch">
          {/* Product General Info */}
          <FormControl isRequired>
            <FormLabel>Name</FormLabel>
            <Input name="name" value={form.name} onChange={handleChange} maxLength={255} />
          </FormControl>

          <FormControl>
            <FormLabel>Description</FormLabel>
            <Textarea name="description" value={form.description} onChange={handleChange} />
          </FormControl>

          <FormControl>
            <FormLabel>SKU</FormLabel>
            <Input name="sku" value={form.sku} onChange={handleChange} maxLength={16} />
            <FormHelperText>Maximum 16 characters</FormHelperText>
          </FormControl>

          <FormControl isRequired>
            <FormLabel>Price</FormLabel>
            <Input type="number" name="price" value={form.price} onChange={handleChange} min="0" step="0.01" />
          </FormControl>

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

           {/* PRODUCT GROUP SEARCH + ADD */}
          <FormControl>
            <FormLabel>Product Group</FormLabel>
            <HStack>
              <SearchProductGroup
                onSelect={handleGroupSelect}
              />
              <IconButton aria-label="Add group" icon={<AddIcon />} onClick={onOpen} />
            </HStack>
          </FormControl>

          {/* Upload main product image */}
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
              showPreview={false}
            />
            
            {/* Image Preview and Delete */}
            {form.imageUrl && (
              <Box
                mt={3}
                p={4}
                bg="gray.50"
                border="1px solid"
                borderColor="gray.200"
                borderRadius="md"
              >
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
                
                <Box textAlign="center">
                  <img 
                    src={form.imageUrl} 
                    alt="Product preview" 
                    style={{ 
                      maxWidth: '200px', 
                      maxHeight: '200px', 
                      objectFit: 'contain',
                      borderRadius: '8px'
                    }} 
                  />
                </Box>
                
                <Box mt={2} fontSize="sm" color="gray.600">
                  {form.imageUrl.startsWith('data:') ? (
                    <Text color="green.600">✅ Local file uploaded</Text>
                  ) : (
                    <Text color="blue.600">✅ Stored in Supabase</Text>
                  )}
                </Box>
              </Box>
            )}
          </FormControl>

           {/* VARIANTS */}
          <Box>
            <Heading size="md" mb={4}>Variants</Heading>

            {form.variants.length === 0 && (
              <Box mb={4} fontStyle="italic" color="gray.600">
                No variants added. BOM items below will apply to the main product.
              </Box>
            )}

            {form.variants.map((variant, index) => (
              <Box
                key={index}
                border="1px solid #ccc"
                p={4}
                mb={6}
                borderRadius="md"
                position="relative"
              >
                <HStack justifyContent="space-between" mb={2}>
                  <Heading size="sm">Variant #{index + 1}</Heading>
                  <IconButton
                    aria-label="Remove variant"
                    icon={<DeleteIcon />}
                    size="sm"
                    onClick={() => removeVariant(index)}
                  />
                </HStack>

                <FormControl mb={2} isRequired>
                  <FormLabel>Name</FormLabel>
                  <Input
                    value={variant.name}
                    onChange={e => handleVariantChange(index, 'name', e.target.value)}
                    maxLength={255}
                  />
                </FormControl>

                <FormControl mb={2}>
                  <FormLabel>Short Description</FormLabel>
                  <Textarea
                    value={variant.shortDescription}
                    onChange={e => handleVariantChange(index, 'shortDescription', e.target.value)}
                  />
                </FormControl>

                <FormControl mb={4}>
                  <FormLabel>Variant Image</FormLabel>
                  {variant.imageUrl && (
                    <Box
                      mt={3}
                      p={4}
                      bg="green.50"
                      border="1px solid"
                      borderColor="green.300"
                      borderRadius="md"
                    >
                      <Flex justify="space-between" align="center" mb={3}>
                        <Text fontWeight="bold" color="green.700">
                          Variant #{index + 1} Image
                        </Text>
                        <IconButton
                          aria-label="Remove variant image"
                          icon={<DeleteIcon />}
                          size="sm"
                          colorScheme="red"
                          variant="ghost"
                          onClick={() => {
                            handleVariantChange(index, 'imageUrl', '');
                            toast({
                              title: "Image removed",
                              description: `Variant #${index + 1} image has been removed.`,
                              status: "info",
                              duration: 2000,
                              isClosable: true,
                            });
                          }}
                        />
                      </Flex>
                      
                      <Flex align="center" gap={3}>
                        <Box>
                          <img 
                            src={variant.imageUrl} 
                            width={80} 
                            height={80} 
                            alt={`Variant ${index + 1}`}
                            style={{ 
                              objectFit: 'contain',
                              borderRadius: '8px',
                              border: '1px solid #e2e8f0'
                            }}
                          />
                        </Box>
                        <Box flex={1}>
                          <VStack align="start" spacing={1}>
                            <Text fontSize="sm" fontWeight="medium" color="gray.700">
                              Image Type: {variant.imageUrl.startsWith('data:') ? 'Local File' : 'Supabase Storage'}
                            </Text>
                            <Text fontSize="xs" color="gray.500" isTruncated maxW="300px">
                              {variant.imageUrl.startsWith('data:') 
                                ? 'Base64 encoded image data'
                                : variant.imageUrl
                              }
                            </Text>
                            <Text fontSize="xs" color="green.600">
                              ✅ Successfully uploaded
                            </Text>
                          </VStack>
                        </Box>
                      </Flex>
                    </Box>
                  )}
                  
                  {/* Upload for Variant */}
                  <SupabaseUpload
                    onUpload={(url) => {
                      handleVariantChange(index, 'imageUrl', url);
                      toast({
                        title: "Image uploaded",
                        description: `Variant #${index + 1} image uploaded successfully.`,
                        status: "success",
                        duration: 3000,
                        isClosable: true,
                      });
                    }}
                    folder="product-variants"
                    maxSize={5}
                    showPreview={false}
                  />
                </FormControl>

                {/* BOM Items per variant */}
                <Box>
                  <Heading size="sm" mb={2}>BOM Items for this variant</Heading>
                  {(variant.bomItems || []).map((item, bomIndex) => (
                    <Box key={bomIndex} mb={3} p={3} border="1px solid" borderColor="gray.200" borderRadius="md">
                      <VStack spacing={3} align="stretch">
                        <FormControl>
                          <FormLabel fontSize="sm">Material</FormLabel>
                          <MaterialSearchBar
                            materialsForAutocomplete={materialsForAutocomplete}
                            onSelectMaterial={(materialId) => handleVariantMaterialSelect(materialId, index, bomIndex)}
                            onSearch={() => {}} // Not needed for BOM
                            initialSearchTerm=""
                          />
                        </FormControl>
                        
                        <HStack spacing={3}>
                          <FormControl>
                            <FormLabel fontSize="sm">Quantity</FormLabel>
                            <Input
                              type="number"
                              min={1}
                              placeholder="Quantity"
                                                          value={parseFloat(item.quantity) || 0}
                            onChange={e => handleVariantBOMChange(index, bomIndex, 'quantity', e.target.value)}
                            />
                          </FormControl>
                          
                          <FormControl>
                            <FormLabel fontSize="sm">UOM</FormLabel>
                            <Input
                              placeholder="Unit of Measure"
                              value={item.uom || ''}
                              onChange={e => handleVariantBOMChange(index, bomIndex, 'uom', e.target.value)}
                              isReadOnly
                              bg="gray.50"
                            />
                          </FormControl>
                          
                          <IconButton
                            aria-label="Remove BOM item"
                            icon={<DeleteIcon />}
                            size="sm"
                            onClick={() => removeVariantBOMItem(index, bomIndex)}
                            alignSelf="end"
                            mt={6}
                          />
                        </HStack>
                      </VStack>
                    </Box>
                  ))}
                  <Button size="sm" leftIcon={<AddIcon />} onClick={() => addVariantBOMItem(index)}>
                    Add BOM Item
                  </Button>
                </Box>
              </Box>
            ))}

            <Button leftIcon={<AddIcon />} onClick={addVariant} mb={6}>
              Add Variant
            </Button>
          </Box>

          <Box w="full">
            <FormLabel>BOM Items</FormLabel>
            {form.bomItems.map((item, index) => (
              <Box key={index} mb={3} p={3} border="1px solid" borderColor="gray.200" borderRadius="md">
                <VStack spacing={3} align="stretch">
                  <FormControl>
                    <FormLabel fontSize="sm">Material</FormLabel>
                    <MaterialSearchBar
                      materialsForAutocomplete={materialsForAutocomplete}
                      onSelectMaterial={(materialId) => handleMaterialSelect(materialId, index)}
                      onSearch={() => {}} // Not needed for BOM
                      initialSearchTerm=""
                    />
                  </FormControl>
                  
                  <HStack spacing={3}>
                    <FormControl>
                      <FormLabel fontSize="sm">Quantity</FormLabel>
                      <Input
                        type="number"
                        min={1}
                        placeholder="Quantity"
                                                    value={parseFloat(item.quantity) || 0}
                            onChange={e => handleBOMChange(index, 'quantity', e.target.value)}
                      />
                    </FormControl>
                    
                    <FormControl>
                      <FormLabel fontSize="sm">UOM</FormLabel>
                      <Input
                        placeholder="Unit of Measure"
                        value={item.uom || ''}
                        onChange={e => handleBOMChange(index, 'uom', e.target.value)}
                        isReadOnly
                        bg="gray.50"
                      />
                    </FormControl>
                    
                    <IconButton 
                      icon={<DeleteIcon />} 
                      onClick={() => removeBOMItem(index)} 
                      aria-label="Remove BOM item"
                      alignSelf="end"
                      mt={6}
                    />
                  </HStack>
                </VStack>
              </Box>
            ))}
            <Button mt={2} size="sm" leftIcon={<AddIcon />} onClick={addBOMItem}>Add BOM Item</Button>
          </Box>

          {/* Variants (optional) có thể thêm nếu cần mở rộng thêm UI */}

          <Button type="submit" colorScheme="teal" w="full">Add Product</Button>
        </VStack>
      </form>

      {/* ADD NEW GROUP MODAL */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Create New Group</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl isInvalid={!!groupError}>
              <FormLabel>Group Name</FormLabel>
              <Input value={newGroupName} onChange={(e) => {
                setNewGroupName(e.target.value);
                setGroupError('');
              }} />
            </FormControl>
            {groupError && <Box color="red.500" mt={2}>{groupError}</Box>}
          </ModalBody>
          <ModalFooter>
            <Button onClick={handleNewGroupSubmit} colorScheme="teal" mr={3}>Add</Button>
            <Button onClick={onClose}>Cancel</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default AddProductPage;