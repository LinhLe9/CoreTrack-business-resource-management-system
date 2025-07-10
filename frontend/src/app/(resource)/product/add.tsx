// pages/products/add.tsx
import {
  Box, Button, FormControl, FormLabel, Input, Select, Textarea, VStack, Heading, useToast, HStack, IconButton
} from '@chakra-ui/react';
import { useState } from 'react';
import { useRouter } from 'next/router';
import { CldUploadWidget } from 'next-cloudinary';
import axios from 'axios';
import { AddIcon, DeleteIcon } from '@chakra-ui/icons';
import type { CloudinaryUploadWidgetInfo } from 'next-cloudinary';

type BOMItem = {
  materialId: string;
  quantity: number;
};

type AddProductForm = {
  name: string;
  description: string;
  sku: string;
  price: string;
  imageUrl: string;
  productGroupId: string;
  newProductGroupName: string;
  bomItems: BOMItem[];
  variants: any[]; // bạn có thể làm rõ thêm sau
};

const AddProductPage = () => {
  const [form, setForm] = useState<AddProductForm>({
    name: '',
    description: '',
    sku: '',
    price: '',
    imageUrl: '',
    productGroupId: '',
    newProductGroupName: '',
    bomItems: [{ materialId: '', quantity: 1 }],
    variants: [],
  });

  const toast = useToast();
  const router = useRouter();

  const handleChange = (e: any) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleBOMChange = (
    index: number,
    field: 'materialId' | 'quantity',
    value: string
    ) => {
    const updated = [...form.bomItems];

    if (field === 'quantity') {
        updated[index].quantity = Number(value);
    } else {
        updated[index].materialId = value;
    }

    setForm(prev => ({ ...prev, bomItems: updated }));
    };

  const addBOMItem = () => {
    setForm(prev => ({
      ...prev,
      bomItems: [...prev.bomItems, { materialId: '', quantity: 1 }],
    }));
  };

  const removeBOMItem = (index: number) => {
    const updated = form.bomItems.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, bomItems: updated }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const payload = {
      ...form,
      price: parseFloat(form.price),
      productGroupId: form.productGroupId ? parseInt(form.productGroupId) : null,
      bomItems: form.bomItems.map(item => ({
        materialId: parseInt(item.materialId),
        quantity: item.quantity,
      })),
    };

    try {
      await axios.post('/api/products', payload);
      toast({ title: 'Product added successfully!', status: 'success', duration: 3000 });
      router.push('/products');
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
        <VStack spacing={4}>
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
          </FormControl>

          <FormControl isRequired>
            <FormLabel>Price</FormLabel>
            <Input type="number" name="price" value={form.price} onChange={handleChange} min="0" step="0.01" />
          </FormControl>

          <FormControl>
            <FormLabel>Existing Group</FormLabel>
            <Input name="productGroupId" placeholder="Enter group ID (optional)" value={form.productGroupId} onChange={handleChange} />
          </FormControl>

          <FormControl>
            <FormLabel>New Group Name</FormLabel>
            <Input name="newProductGroupName" placeholder="Or create new group" value={form.newProductGroupName} onChange={handleChange} maxLength={100} />
          </FormControl>

          <FormControl>
            <FormLabel>Product Image</FormLabel>
            {form.imageUrl && <img src={form.imageUrl} width="150" alt="preview" />}
            <CldUploadWidget
              uploadPreset="your_upload_preset"
              onUpload={res => {
                const info = res.info as CloudinaryUploadWidgetInfo;
                setForm(prev => ({ ...prev, imageUrl: info.secure_url }));
              }}
            >
              {({ open }) => <Button onClick={() => open()} mt={2}>Upload Image</Button>}
            </CldUploadWidget>
          </FormControl>

          <Box w="full">
            <FormLabel>BOM Items</FormLabel>
            {form.bomItems.map((item, index) => (
              <HStack key={index}>
                <Input
                  placeholder="Material ID"
                  value={item.materialId}
                  onChange={e => handleBOMChange(index, 'materialId', e.target.value)}
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
    </Box>
  );
};

export default AddProductPage;