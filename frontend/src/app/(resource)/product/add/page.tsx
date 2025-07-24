// pages/products/add.tsx
"use client";

import {
  Box, Button, FormControl, FormLabel, Input, Textarea, VStack, Heading, useToast, HStack, 
  IconButton, List, ListItem, Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalFooter, 
  ModalCloseButton, useDisclosure, FormHelperText, Select
} from '@chakra-ui/react';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { CldUploadWidget } from 'next-cloudinary';
import api from '@/lib/axios';
import { AddIcon, DeleteIcon } from '@chakra-ui/icons';
import type { CloudinaryUploadWidgetInfo } from 'next-cloudinary';
import { AddProductForm, BOMItem, Group} from '@/types/product';
import SearchProductGroup  from '@/components/product/SearchProductGroup'
import currencyCodes from 'currency-codes';
import { AxiosError } from 'axios';


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
      bomItems[bomIndex].quantity = Number(value);
    } else {
      bomItems[bomIndex].materialSku = value;
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
    updated[variantIndex].bomItems.push({ materialSku: '', quantity: 1 });
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
      updated[index].quantity = Number(value);
    } else {
      updated[index].materialSku = value;
    }
    setForm(prev => ({ ...prev, bomItems: updated }));
  };

  const addBOMItem = () => {
    setForm(prev => ({ ...prev, bomItems: [...prev.bomItems, { materialSku: '', quantity: 1 }] }));
  };

  const removeBOMItem = (index: number) => {
    const updated = form.bomItems.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, bomItems: updated }));
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
            quantity: item.quantity,
        })),
      },
    ];
    } else {
      payload.variants = form.variants.map(variant => ({
        ...variant,
        bomItems: variant.bomItems.map(item => ({
          materialSku: item.materialSku,
          quantity: item.quantity,
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
              <Input
                placeholder="Search existing group"
                value={groupSearch}
                onChange={e => {
                  setGroupSearch(e.target.value);
                  setForm(prev => ({ ...prev, productGroupId: '', newProductGroupName: '' }));
                }}
              />
              <IconButton aria-label="Add group" icon={<AddIcon />} onClick={onOpen} />
            </HStack>
            {filteredGroups.length > 0 && (
              <List bg="white" border="1px solid #ccc" mt={1} maxH="150px" overflowY="auto">
                {filteredGroups.map(group => (
                  <ListItem
                    key={group.id}
                    px={2}
                    py={1}
                    _hover={{ bg: 'gray.100', cursor: 'pointer' }}
                    onClick={() => handleGroupSelect(group)}
                  >
                    {group.name}
                  </ListItem>
                ))}
              </List>
            )}
          </FormControl>

          {/* Upload main product image */}
          <FormControl>
            <FormLabel>Product Image</FormLabel>
            {form.imageUrl && (
              <Box
                mt={3}
                p={3}
                bg="green.50"
                border="1px solid"
                borderColor="green.300"
                borderRadius="md"
                display="flex"
                alignItems="center"
                gap={3}
              >
                <a href={form.imageUrl} target="_blank" rel="noopener noreferrer">
                  <img src={form.imageUrl} alt="Uploaded" width={60} />
                </a>
                <Box>
                  <Box fontWeight="bold" color="green.700">Upload successful</Box>
                  <Box fontSize="sm" color="gray.700" isTruncated maxW="200px">{form.imageUrl}</Box>
                </Box>
              </Box>
            )}
            <CldUploadWidget
              uploadPreset={process.env.NEXT_PUBLIC_CLOUDINARY_UPLOAD_PRESET}
              onUpload={res => {
                console.log("Upload result:", res);
                if (res.event === 'success') {
                  const info = res.info as CloudinaryUploadWidgetInfo;
                  setForm(prev => ({
                    ...prev,
                    imageUrl: info.secure_url,
                  }));

                  toast({
                    title: "Image uploaded",
                    description: "Your product image has been uploaded successfully.",
                    status: "success",
                    duration: 3000,
                    isClosable: true,
                  });
                } 
                if (res.event === 'error') {
                toast({
                  title: "Upload failed",
                  description: "Failed to upload image.",
                  status: "error",
                  duration: 3000,
                  isClosable: true,
                });
                console.error("Upload error:", res);
                }
              }}
            >
              {({ open }) => <Button onClick={() => open()} mt={2}>Upload Image</Button>}
            </CldUploadWidget>
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
                      p={3}
                      bg="green.50"
                      border="1px solid"
                      borderColor="green.300"
                      borderRadius="md"
                      display="flex"
                      alignItems="center"
                      gap={3}
                    >
                      <a href={variant.imageUrl} target="_blank" rel="noopener noreferrer">
                        <img src={variant.imageUrl} width={60} alt={`Variant ${index + 1}`} />
                      </a>
                      <Box>
                        <Box fontWeight="bold" color="green.700">Upload successful</Box>
                        <Box fontSize="sm" color="gray.700" isTruncated maxW="200px">{variant.imageUrl}</Box>
                      </Box>
                    </Box>
                  )}
                  <CldUploadWidget
                    uploadPreset="my_unsigned_preset"
                    onUpload={res => {
                      const info = res.info as CloudinaryUploadWidgetInfo;
                      handleVariantChange(index, 'imageUrl', info.secure_url);
                    
                      toast({
                        title: "Image uploaded",
                        description: `Variant #${index + 1} image uploaded successfully.`,
                        status: "success",
                        duration: 3000,
                        isClosable: true,
                      });
                    }}
                  >
                    {({ open }) => (
                      <Button mt={2} size="sm" onClick={() => open()}>
                        Upload Variant Image
                      </Button>
                    )}
                  </CldUploadWidget>
                </FormControl>

                {/* BOM Items per variant */}
                <Box>
                  <Heading size="sm" mb={2}>BOM Items for this variant</Heading>
                  {(variant.bomItems || []).map((item, bomIndex) => (
                    <HStack key={bomIndex} mb={2}>
                      <Input
                        placeholder="Material SKU"
                        value={item.materialSku}
                        onChange={e => handleVariantBOMChange(index, bomIndex, 'materialSku', e.target.value)}
                      />
                      <Input
                        type="number"
                        min={1}
                        placeholder="Quantity"
                        value={item.quantity}
                        onChange={e => handleVariantBOMChange(index, bomIndex, 'quantity', e.target.value)}
                        width="100px"
                      />
                      <IconButton
                        aria-label="Remove BOM item"
                        icon={<DeleteIcon />}
                        size="sm"
                        onClick={() => removeVariantBOMItem(index, bomIndex)}
                      />
                    </HStack>
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
              <HStack key={index}>
                <Input
                  placeholder="Material SKU"
                  value={item.materialSku}
                  onChange={e => handleBOMChange(index, 'materialSku', e.target.value)}
                />
                <Input
                  placeholder="Quantity"
                  type="number"
                  value={item.quantity}
                  onChange={e => handleBOMChange(index, 'quantity', e.target.value)}
                />
                <IconButton icon={<DeleteIcon />} onClick={() => removeBOMItem(index)} aria-label="Remove BOM item" />
              </HStack>
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