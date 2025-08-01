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
} from '@chakra-ui/react';
import { useRouter, useParams } from 'next/navigation';
import { AddIcon, DeleteIcon } from '@chakra-ui/icons';
import { getMaterialById, updateMaterial, getAllMaterialGroup } from '@/services/materialService';
import { getAllSuppliersForAutocomplete } from '@/services/supplierService';
import { Supplier, Variant } from '@/types/material';
import { SupplierAutoComplete } from '@/types/supplier';
import { MaterialGroup } from '@/types/material';
import { MaterialDetailResponse } from '@/types/material';
import useMaterialGroups from '@/hooks/useMaterialGroups';
import useUoM from '@/hooks/useUoM';
import { AxiosError } from 'axios';
import SupabaseUpload from '@/components/upload/SupabaseUpload';
import MaterialSearchBar from '@/components/material/MaterialSearchBar';

interface EditMaterialForm {
  name: string;
  description: string;
  sku: string;
  uom: string;
  imageUrl: string;
  materialGroupId: string;
  newMaterialGroupName: string;
  variants: Variant[];
  suppliers: Supplier[];
}

const EditMaterialPage: React.FC = () => {
  const params = useParams();
  const id = params?.id;
  const router = useRouter();
  const toast = useToast();

  const [form, setForm] = useState<EditMaterialForm>({
    name: '',
    description: '',
    sku: '',
    uom: '',
    imageUrl: '',
    materialGroupId: '',
    newMaterialGroupName: '',
    variants: [],
    suppliers: []
  });

  const [allGroups, setAllGroups] = useState<MaterialGroup[]>([]);
  const [groupSearch, setGroupSearch] = useState('');
  const [filteredGroups, setFilteredGroups] = useState<MaterialGroup[]>([]);
  const [newGroupName, setNewGroupName] = useState('');
  const [groupError, setGroupError] = useState('');
  const { isOpen, onOpen, onClose } = useDisclosure();

  const [errors, setErrors] = useState<Partial<EditMaterialForm>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [allSuppliers, setAllSuppliers] = useState<SupplierAutoComplete[]>([]);
  const [supplierSearch, setSupplierSearch] = useState('');
  const [filteredSuppliers, setFilteredSuppliers] = useState<SupplierAutoComplete[]>([]);
  const [currentSupplierIndex, setCurrentSupplierIndex] = useState<number | null>(null);
  const [materialsForAutocomplete, setMaterialsForAutocomplete] = useState<any[]>([]);
  
  const { materialGroups, loading: groupsLoading } = useMaterialGroups();
  const { uomList, loading: uomLoading } = useUoM();

  // Fetch material data
  useEffect(() => {
    const fetchMaterial = async () => {
      if (!id || allGroups.length === 0) return;
      
      try {
        setIsLoading(true);
        const materialData = await getMaterialById(Number(id));
        
        // Map MaterialDetailResponse to EditMaterialForm
        setForm({
          name: materialData.name,
          description: materialData.shortDes || '',
          sku: materialData.sku,
          uom: materialData.uom || '',
          imageUrl: materialData.imageUrl || '',
          materialGroupId: allGroups.find(g => g.name === materialData.groupMaterial)?.id?.toString() || '',
          newMaterialGroupName: '',
          variants: materialData.variants?.map(v => ({
            id: v.materialVariantResponse.id,
            sku: v.materialVariantResponse.sku,
            name: v.materialVariantResponse.name,
            shortDescription: v.materialVariantResponse.shortDescription,
            imageUrl: v.materialVariantResponse.imageUrl
          })) || [],
          suppliers: materialData.suppliers?.map(s => ({
            supplierId: s.supplierId,
            price: s.price,
            currency: s.currency,
            leadTime: s.leadTimeDays,
            minimumOrderQuantity: s.minOrderQuantity,
            supplierMaterialCode: s.supplierMaterialCode
          })) || []
        });
      } catch (error) {
        console.error('Error fetching material:', error);
        toast({
          title: 'Error',
          description: 'Failed to load material data',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchMaterial();
  }, [id, allGroups, toast]);

  // Fetch all suppliers for autocomplete
  useEffect(() => {
    const fetchSuppliers = async () => {
      try {
        const suppliers = await getAllSuppliersForAutocomplete();
        setAllSuppliers(suppliers);
      } catch (error) {
        console.error('Error fetching suppliers:', error);
      }
    };
    fetchSuppliers();
  }, []);

  // Filter suppliers based on search
  useEffect(() => {
    if (!supplierSearch.trim()) {
      setFilteredSuppliers([]);
      return;
    }

    const filtered = allSuppliers.filter(supplier =>
      supplier.name.toLowerCase().includes(supplierSearch.toLowerCase()) ||
      supplier.email?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
      supplier.contactPerson?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
      supplier.address?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
      supplier.phone?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
      supplier.country?.toLowerCase().includes(supplierSearch.toLowerCase())
    );
    setFilteredSuppliers(filtered);
  }, [supplierSearch, allSuppliers]);

  // Fetch all groups
  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const groups = await getAllMaterialGroup();
        setAllGroups(groups);
      } catch (error) {
        console.error('Error fetching groups:', error);
      }
    };
    fetchGroups();
  }, []);

  // Filter groups based on search
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
      [name]: value,
    }));
  };

  const handleGroupSelect = (group: MaterialGroup) => {
    setForm(prev => ({ ...prev, materialGroupId: group.id.toString(), newMaterialGroupName: '' }));
    setGroupSearch(group.name);
  };

  const handleNewGroupSubmit = () => {
    const exists = allGroups.some(g => g.name.toLowerCase() === newGroupName.toLowerCase());
    if (exists) {
      setGroupError('Group name already exists.');
      return;
    }
    setForm(prev => ({ ...prev, newMaterialGroupName: newGroupName.trim(), materialGroupId: '' }));
    setGroupSearch(newGroupName.trim());
    setGroupError('');
    setNewGroupName('');
    onClose();
  };

  const handleSupplierChange = (
    index: number,
    field: keyof Supplier,
    value: string | number
  ) => {
    const updated = [...form.suppliers];
    updated[index] = {
      ...updated[index],
      [field]: value,
    };
    setForm(prev => ({ ...prev, suppliers: updated }));
  };

  const addSupplier = () => {
    setForm(prev => ({
      ...prev,
      suppliers: [...prev.suppliers, {
        supplierId: undefined,
        price: 0,
        currency: 'USD',
        leadTime: 0,
        minimumOrderQuantity: 1,
        supplierMaterialCode: ''
      }]
    }));
  };

  const removeSupplier = (index: number) => {
    const updated = form.suppliers.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, suppliers: updated }));
  };

  const handleSupplierSelect = (supplier: SupplierAutoComplete, index: number) => {
    const updated = [...form.suppliers];
    updated[index] = {
      ...updated[index],
      supplierId: supplier.id,
    };
    setForm(prev => ({ ...prev, suppliers: updated }));
    setSupplierSearch('');
  };

  const handleCreateNewSupplier = () => {
    // Redirect to supplier add page
    router.push('/supplier/add');
  };

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

  const addVariant = () => {
    setForm(prev => ({
      ...prev,
      variants: [...prev.variants, {
        id: undefined,
        sku: undefined, // null for new variants
        name: '',
        shortDescription: '',
        imageUrl: ''
      }]
    }));
  };

  const removeVariant = (index: number) => {
    const updated = form.variants.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, variants: updated }));
  };

  const validateSuppliers = (): boolean => {
    for (let i = 0; i < form.suppliers.length; i++) {
      const supplier = form.suppliers[i];
      if (!supplier.supplierId) {
        toast({
          title: 'Validation Error',
          description: `Please select a supplier for supplier ${i + 1}`,
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
        return false;
      }
    }
    return true;
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<EditMaterialForm> = {};

    if (!form.name.trim()) {
      newErrors.name = 'Material name is required';
    }

    if (!form.uom.trim()) {
      newErrors.uom = 'Unit of measure is required';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return false;
    }

    return validateSuppliers();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    try {
      // Map form data to backend expected format
      const materialData = {
        name: form.name,
        shortDes: form.description,
        uom: form.uom,
        imageUrl: form.imageUrl,
        materialGroupId: form.materialGroupId || null,
        newMaterialGroupName: form.newMaterialGroupName || null,
        variants: form.variants.map(variant => ({
          id: variant.id,
          sku: variant.sku, // null for new variants, existing SKU for updates
          name: variant.name,
          shortDescription: variant.shortDescription,
          imageUrl: variant.imageUrl
        })),
        suppliers: form.suppliers.map(supplier => ({
          supplierId: supplier.supplierId,
          price: supplier.price,
          currency: supplier.currency,
          leadTimeDays: supplier.leadTime,
          minOrderQuantity: supplier.minimumOrderQuantity,
          supplierMaterialCode: supplier.supplierMaterialCode
        }))
      };

      await updateMaterial(Number(id), materialData);
      
      toast({
        title: 'Success',
        description: 'Material updated successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });

      router.push(`/material/${id}`);
    } catch (err: any) {
      console.error('Error updating material:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to update material',
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
        Edit Material: {form.name}
      </Heading>

      <form onSubmit={handleSubmit}>
        <VStack spacing={4} align="stretch">
          {/* Material Name */}
          <FormControl isRequired isInvalid={!!errors.name}>
            <FormLabel>Material Name</FormLabel>
            <Input
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Enter material name"
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
              placeholder="Enter material description"
            />
          </FormControl>

          {/* SKU (Read-only) */}
          <FormControl>
            <FormLabel>SKU</FormLabel>
            <Input
              name="sku"
              value={form.sku}
              isReadOnly
              bg="gray.50"
            />
            <FormHelperText>SKU cannot be changed</FormHelperText>
          </FormControl>

          {/* Unit of Measure */}
          <FormControl isRequired isInvalid={!!errors.uom}>
            <FormLabel>Unit of Measure</FormLabel>
            <Select
              name="uom"
              value={form.uom}
              onChange={handleChange}
              placeholder={uomLoading ? "Loading UoM..." : "Select unit of measure"}
              isDisabled={uomLoading}
            >
              {uomList.map(uom => (
                <option key={uom.value} value={uom.value}>
                  {uom.displayName}
                </option>
              ))}
            </Select>
            <FormErrorMessage>{errors.uom}</FormErrorMessage>
          </FormControl>

          {/* Main Material Image */}
          <FormControl>
            <FormLabel>Material Image</FormLabel>
            <SupabaseUpload
              onUpload={(url) => setForm(prev => ({ ...prev, imageUrl: url }))}
              folder="materials"
              accept="image/*"
              maxSize={5}
            />
            {form.imageUrl && (
              <Box mt={2} p={3} bg="gray.50" borderRadius="md">
                <Text fontSize="sm" fontWeight="bold" mb={1}>Image Information:</Text>
                <Text fontSize="xs" color="gray.600" mb={1}>
                  Type: {form.imageUrl.startsWith('data:') ? 'Base64 (Local)' : 'Supabase URL'}
                </Text>
                <Text fontSize="xs" color="gray.600" mb={2} noOfLines={2}>
                  URL: {form.imageUrl.length > 100 ? form.imageUrl.substring(0, 100) + '...' : form.imageUrl}
                </Text>
                <Button
                  size="xs"
                  colorScheme="red"
                  onClick={() => setForm(prev => ({ ...prev, imageUrl: '' }))}
                >
                  Remove Image
                </Button>
              </Box>
            )}
          </FormControl>

          {/* Material Group */}
          <FormControl>
            <FormLabel>Material Group</FormLabel>
            <HStack>
              <Input
                placeholder="Search existing group"
                value={groupSearch}
                onChange={e => {
                  setGroupSearch(e.target.value);
                  setForm(prev => ({ ...prev, materialGroupId: '', newMaterialGroupName: '' }));
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

          {/* Variants Section */}
          <Box>
            <Heading size="md" mb={4}>Variants</Heading>
            
            {form.variants.length === 0 && (
              <Box mb={4} fontStyle="italic" color="gray.600">
                No variants added. The main material will be used.
              </Box>
            )}

            {form.variants.map((variant, index) => (
              <Box
                key={index}
                border="1px solid"
                borderColor="gray.200"
                p={4}
                mb={4}
                borderRadius="md"
              >
                <Flex justify="space-between" align="center" mb={3}>
                  <Text fontWeight="bold">Variant #{index + 1}</Text>
                  <IconButton
                    aria-label="Remove variant"
                    icon={<DeleteIcon />}
                    size="sm"
                    onClick={() => removeVariant(index)}
                  />
                </Flex>

                <FormControl mb={3} isRequired>
                  <FormLabel>Name</FormLabel>
                  <Input
                    value={variant.name}
                    onChange={e => handleVariantChange(index, 'name', e.target.value)}
                    placeholder="Enter variant name"
                    maxLength={255}
                  />
                </FormControl>

                <FormControl mb={3}>
                  <FormLabel>Short Description</FormLabel>
                  <Textarea
                    value={variant.shortDescription}
                    onChange={e => handleVariantChange(index, 'shortDescription', e.target.value)}
                    placeholder="Enter variant description"
                  />
                </FormControl>

                <FormControl>
                  <FormLabel>Variant Image</FormLabel>
                  <SupabaseUpload
                    onUpload={(url) => handleVariantChange(index, 'imageUrl', url)}
                    folder="materials/variants"
                    accept="image/*"
                    maxSize={5}
                  />
                  {variant.imageUrl && (
                    <Box mt={2} p={3} bg="gray.50" borderRadius="md">
                      <Text fontSize="sm" fontWeight="bold" mb={1}>Image Information:</Text>
                      <Text fontSize="xs" color="gray.600" mb={1}>
                        Type: {variant.imageUrl.startsWith('data:') ? 'Base64 (Local)' : 'Supabase URL'}
                      </Text>
                      <Text fontSize="xs" color="gray.600" mb={2} noOfLines={2}>
                        URL: {variant.imageUrl.length > 100 ? variant.imageUrl.substring(0, 100) + '...' : variant.imageUrl}
                      </Text>
                      <Button
                        size="xs"
                        colorScheme="red"
                        onClick={() => handleVariantChange(index, 'imageUrl', '')}
                      >
                        Remove Image
                      </Button>
                    </Box>
                  )}
                </FormControl>
              </Box>
            ))}

            <Button leftIcon={<AddIcon />} onClick={addVariant} colorScheme="blue">
              Add Variant
            </Button>
          </Box>

          {/* Suppliers Section */}
          <Box>
            <Heading size="md" mb={4}>Suppliers</Heading>
                  
            {form.suppliers.map((supplier, index) => (
              <Box
                key={index}
                border="1px solid"
                borderColor="gray.200"
                p={4}
                mb={4}
                borderRadius="md"
              >
                <Flex justify="space-between" align="center" mb={3}>
                  <Text fontWeight="bold">Supplier #{index + 1}</Text>
                  <IconButton
                    aria-label="Remove supplier"
                    icon={<DeleteIcon />}
                    size="sm"
                    onClick={() => removeSupplier(index)}
                  />
                </Flex>
          
                {/* Supplier Search/Select */}
                <FormControl mb={3}>
                  <FormLabel>Supplier</FormLabel>
                  <HStack>
                    <Input
                      placeholder="Search and select existing supplier"
                      value={currentSupplierIndex === index ? supplierSearch : ''}
                      onChange={(e) => {
                        setSupplierSearch(e.target.value);
                        setCurrentSupplierIndex(index);
                      }}
                      onFocus={() => {
                        setSupplierSearch('');
                        setCurrentSupplierIndex(index);
                      }}
                      isInvalid={!supplier.supplierId}
                    />
                    <IconButton
                      aria-label="Create new supplier"
                      icon={<AddIcon />}
                      onClick={handleCreateNewSupplier}
                      colorScheme="blue"
                    />
                    </HStack>
                            
                    {/* Search Results */}
                    {currentSupplierIndex === index && filteredSuppliers.length > 0 && (
                      <List bg="white" border="1px solid" borderColor="gray.200" mt={1} maxH="150px" overflowY="auto">
                        {filteredSuppliers.map(s => (
                          <ListItem
                            key={s.id}
                            px={3}
                            py={2}
                            _hover={{ bg: 'gray.100', cursor: 'pointer' }}
                            onClick={() => handleSupplierSelect(s, index)}
                          >
                            <Text fontWeight="medium">{s.name}</Text>
                              {s.email && <Text fontSize="sm" color="gray.600">{s.email}</Text>}
                              {s.country && <Text fontSize="sm" color="gray.500">{s.country}</Text>}
                          </ListItem>
                        ))}
                      </List>
                    )}
          
                    {/* Selected Supplier Display */}
                    {supplier.supplierId && (
                      <Box mt={2} p={2} bg="blue.50" borderRadius="md">
                        <Text fontSize="sm" color="blue.700">
                          Selected: {allSuppliers.find(s => s.id === supplier.supplierId)?.name}
                        </Text>
                      </Box>
                    )}
          
                    {/* Error message if no supplier selected */}
                    {!supplier.supplierId && (
                      <Text fontSize="sm" color="red.500" mt={1}>
                        Please select a supplier
                      </Text>
                    )}
                </FormControl>

                <HStack spacing={3}>
                  <FormControl isRequired>
                    <FormLabel>Price</FormLabel>
                    <Input
                      type="number"
                      step="0.01"
                      min="0"
                      value={supplier.price}
                      onChange={e => handleSupplierChange(index, 'price', parseFloat(e.target.value))}
                      placeholder="Enter price"
                    />
                  </FormControl>

                  <FormControl>
                    <FormLabel>Currency</FormLabel>
                    <Select
                      value={supplier.currency}
                      onChange={e => handleSupplierChange(index, 'currency', e.target.value)}
                    >
                      <option value="USD">USD</option>
                      <option value="EUR">EUR</option>
                      <option value="VND">VND</option>
                    </Select>
                  </FormControl>
                </HStack>

                <HStack spacing={3} mt={3}>
                  <FormControl>
                    <FormLabel>Lead Time (Days)</FormLabel>
                    <Input
                      type="number"
                      min="0"
                      value={supplier.leadTime}
                      onChange={e => handleSupplierChange(index, 'leadTime', parseInt(e.target.value))}
                      placeholder="Enter lead time"
                    />
                  </FormControl>

                  <FormControl>
                    <FormLabel>Min Order Quantity</FormLabel>
                    <Input
                      type="number"
                      min="1"
                      value={supplier.minimumOrderQuantity}
                      onChange={e => handleSupplierChange(index, 'minimumOrderQuantity', parseInt(e.target.value))}
                      placeholder="Enter min order quantity"
                    />
                  </FormControl>
                </HStack>

                <FormControl mt={3}>
                  <FormLabel>Supplier Material Code</FormLabel>
                  <Input
                    value={supplier.supplierMaterialCode}
                    onChange={e => handleSupplierChange(index, 'supplierMaterialCode', e.target.value)}
                    placeholder="Enter supplier material code"
                  />
                </FormControl>
              </Box>
            ))}

            <Button leftIcon={<AddIcon />} onClick={addSupplier} colorScheme="blue">
              Add Supplier
            </Button>
          </Box>

          <HStack spacing={4}>
            <Button type="submit" colorScheme="teal" isLoading={isSubmitting}>
              Update Material
            </Button>
            <Button onClick={() => router.push(`/material/${id}`)}>
              Cancel
            </Button>
          </HStack>
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

export default EditMaterialPage; 