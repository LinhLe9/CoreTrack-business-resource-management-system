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
  ModalFooter
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { AddIcon, DeleteIcon } from '@chakra-ui/icons';
import { addMaterial, getAllMaterialGroup } from '@/services/materialService';
import currencyCodes from 'currency-codes';
import { getAllSuppliersForAutocomplete } from '@/services/supplierService';
import { Supplier, Variant } from '@/types/material';
import SearchMaterialGroup from '@/components/material/SearchMaterialGroup';
import { SupplierAutoComplete } from '@/types/supplier';
import { MaterialGroup } from '@/types/material';
import useMaterialGroups from '@/hooks/useMaterialGroups';
import useUoM from '@/hooks/useUoM';
import { AxiosError } from 'axios';
import SupabaseUpload from '@/components/upload/SupabaseUpload';
import ProtectedRoute from '@/components/auth/ProtectedRoute';

interface AddMaterialForm {
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

const AddMaterialPage: React.FC = () => {
  const [form, setForm] = useState<AddMaterialForm>({
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

  const [errors, setErrors] = useState<Partial<AddMaterialForm>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [allSuppliers, setAllSuppliers] = useState<SupplierAutoComplete[]>([]);
  const [supplierSearch, setSupplierSearch] = useState('');
  const [filteredSuppliers, setFilteredSuppliers] = useState<SupplierAutoComplete[]>([]);
  const [currentSupplierIndex, setCurrentSupplierIndex] = useState<number | null>(null);
  
  const { materialGroups, loading: groupsLoading } = useMaterialGroups();
  const { uomList, loading: uomLoading } = useUoM();
  const currencyList = currencyCodes.data.filter(c => c.code);
  const toast = useToast();
  const router = useRouter();

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
    if (supplierSearch.trim()) {
      const filtered = allSuppliers.filter(supplier =>
        supplier.name.toLowerCase().includes(supplierSearch.toLowerCase()) ||
        supplier.email?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
        supplier.contactPerson?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
        supplier.address?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
        supplier.phone?.toLowerCase().includes(supplierSearch.toLowerCase()) ||
        supplier.country?.toLowerCase().includes(supplierSearch.toLowerCase())
      );
      setFilteredSuppliers(filtered);
    } else {
      setFilteredSuppliers([]);
    }
  }, [supplierSearch, allSuppliers]);


  // fetch all group 
  useEffect(() => {
      const fetchGroups = async () => {
        try {
          const groups = await getAllMaterialGroup();
          setAllGroups(groups); // res.data is array of GROUP object
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

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    
    // Clear error when user starts typing
    if (errors[name as keyof AddMaterialForm]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

    // handle the material group change 
  const handleGroupSelect = (group: MaterialGroup) => {
    setForm(prev => ({ ...prev, materialGroupId: group.id.toString(), newMaterialGroupName: '' }));
    setGroupSearch(group.name);
  };

  // Add new material group submit
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
    console.log('=== SUPPLIER CHANGE DEBUG ===');
    console.log('Index:', index);
    console.log('Field:', field);
    console.log('Value:', value);
    console.log('Value type:', typeof value);
    
    const updatedSuppliers = [...form.suppliers];
    updatedSuppliers[index] = {
      ...updatedSuppliers[index],
      [field]: value,
    };
    console.log('Updated supplier:', updatedSuppliers[index]);
    console.log('All suppliers after update:', updatedSuppliers);
    
    setForm(prev => {
      const newForm = { ...prev, suppliers: updatedSuppliers };
      console.log('New form state:', newForm);
      console.log('======================');
      return newForm;
    });
  };

  const addSupplier = () => {
    setForm(prev => ({
      ...prev,
      suppliers: [
        ...prev.suppliers,
        {
          supplierId: undefined, // Must be selected via search
          price: 0,
          currency: 'USD',
          leadTime: 1,
          minimumOrderQuantity: 1,
          supplierMaterialCode: ''
        }
      ]
    }));
  };

  const removeSupplier = (index: number) => {
    const updated = form.suppliers.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, suppliers: updated }));
  };

  const handleSupplierSelect = (supplier: SupplierAutoComplete, index: number) => {
    console.log('Selecting supplier:', supplier);
    console.log('SupplierAutoComplete.id:', supplier.id, 'Type:', typeof supplier.id);
    
    // Ensure we're mapping SupplierAutoComplete.id to Supplier.supplierId
    const supplierId = supplier.id;
    console.log('Mapping to supplierId:', supplierId, 'Type:', typeof supplierId);
    
    handleSupplierChange(index, 'supplierId', supplierId);
    setSupplierSearch(''); // Clear search instead of setting supplier name
    setFilteredSuppliers([]);
    setCurrentSupplierIndex(null);
  };

  const handleCreateNewSupplier = () => {
    // Redirect to supplier add page
    router.push('/supplier/add');
  };

  // Variant handlers
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
      variants: [
        ...prev.variants,
        {
          name: '',
          shortDescription: '',
          imageUrl: '',
        },
      ],
    }));
  };

  const removeVariant = (index: number) => {
    const updated = form.variants.filter((_, i) => i !== index);
    setForm(prev => ({ ...prev, variants: updated }));
  };

  // Validate that all suppliers have supplierId
  const validateSuppliers = (): boolean => {
    console.log('Validating suppliers:', form.suppliers);
    for (let i = 0; i < form.suppliers.length; i++) {
      const supplier = form.suppliers[i];
      console.log(`Supplier #${i + 1}:`, supplier);
      console.log(`Supplier #${i + 1} supplierId:`, supplier.supplierId, 'Type:', typeof supplier.supplierId);
      
      if (!supplier.supplierId) {
        console.log(`Supplier #${i + 1} is missing supplierId`);
        toast({
          title: 'Supplier Required',
          description: `Please select a supplier for Supplier #${i + 1}`,
          status: 'error',
          duration: 3000,
          isClosable: true,
        });
        return false;
      }
    }
    console.log('All suppliers have valid supplierId');
    return true;
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<AddMaterialForm> = {};

    if (!form.name.trim()) {
      newErrors.name = 'Material name is required';
    }

    if (!form.uom.trim()) {
      newErrors.uom = 'Unit of measure is required';
    }

    setErrors(newErrors);
    
    // Check if there are form errors
    if (Object.keys(newErrors).length > 0) {
      return false;
    }

    // Validate suppliers
    return validateSuppliers();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      toast({
        title: 'Validation Error',
        description: 'Please fix the errors in the form',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    // Filter out suppliers without supplierId before sending to backend
    const validSuppliers = form.suppliers.filter(supplier => {
      const isValid = supplier.supplierId && typeof supplier.supplierId === 'number' && supplier.supplierId > 0;
      console.log(`Supplier ${supplier.supplierId}:`, { supplierId: supplier.supplierId, type: typeof supplier.supplierId, isValid });
      return isValid;
    });
    console.log('Original suppliers:', form.suppliers);
    console.log('Valid suppliers to send:', validSuppliers);

    // Map frontend supplier fields to backend expected fields
    const mappedSuppliers = validSuppliers.map(supplier => {
      const mapped = {
        supplierId: supplier.supplierId,
        price: supplier.price,
        currency: supplier.currency,
        leadTimeDays: supplier.leadTime, // Map leadTime to leadTimeDays
        minOrderQuantity: supplier.minimumOrderQuantity, // Map minimumOrderQuantity to minOrderQuantity
        supplierMaterialCode: supplier.supplierMaterialCode || ''
      };
      console.log('=== MAPPED SUPPLIER DEBUG ===');
      console.log('Original supplier:', supplier);
      console.log('Mapped supplier:', mapped);
      console.log('============================');
      return mapped;
    });

    // Map variants to backend expected format
    const mappedVariants = (form.variants || []).map(variant => ({
      name: `${form.name} - ${variant.name}`, // Combine material name with variant name
      shortDescription: variant.shortDescription || '',
      imageUrl: variant.imageUrl || ''
    }));

    const materialData = {
      name: form.name,
      description: form.description,
      sku: form.sku,
      uom: form.uom,
      imageUrl: form.imageUrl,
      materialGroupId: form.materialGroupId || null,
      newMaterialGroupName: form.newMaterialGroupName || null,
      variants: mappedVariants,
      suppliers: mappedSuppliers
    };

    setIsSubmitting(true);
    try {
      console.log('Sending materialData to backend:', JSON.stringify(materialData, null, 2));
      await addMaterial(materialData);
      toast({
        title: 'Material added successfully!',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
      router.push('/material');
    } catch (err: any) {
      console.error('Backend error details:', err);
      console.error('Response data:', err?.response?.data);
      console.error('Response status:', err?.response?.status);
      toast({
        title: 'Failed to add material',
        description: err?.response?.data?.message || 'Check your data and try again.',
        status: 'error',
        duration: 4000,
        isClosable: true,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <ProtectedRoute>
      <Box maxW="800px" mx="auto" p={6}>
        <Heading mb={6}>Add New Material</Heading>
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

            {/* SKU */}
            <FormControl>
              <FormLabel>SKU</FormLabel>
              <Input
                name="sku"
                value={form.sku}
                onChange={handleChange}
                placeholder="Enter SKU"
                maxLength={16}
              />
              <FormErrorMessage>{errors.sku}</FormErrorMessage>
              <FormHelperText>Maximum 16 characters</FormHelperText>
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
                <SearchMaterialGroup
                  onSelect={handleGroupSelect}
                  value={groupSearch}
                />
                <IconButton aria-label="Add group" icon={<AddIcon />} onClick={onOpen} />
              </HStack>
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

                  {/* Supplier Details */}
                  <HStack spacing={4}>
                    <FormControl>
                      <FormLabel>Price</FormLabel>
                      <Input
                        type="number"
                        min="0"
                        step="0.01"
                        value={supplier.price}
                        onChange={(e) => handleSupplierChange(index, 'price', parseFloat(e.target.value) || 0)}
                      />
                    </FormControl>

                    <FormControl>
                      <FormLabel>Currency</FormLabel>
                      <Select
                        value={supplier.currency}
                        onChange={(e) => handleSupplierChange(index, 'currency', e.target.value)}
                      >
                        {currencyList.map(curr => (
                          <option key={curr.code} value={curr.code}>
                            {curr.code} - {curr.currency}
                          </option>
                        ))}
                      </Select>
                    </FormControl>
                  </HStack>

                  <HStack spacing={4} mt={3}>
                    <FormControl>
                      <FormLabel>Lead Time (days)</FormLabel>
                      <Input
                        type="number"
                        min="1"
                        value={supplier.leadTime}
                        onChange={(e) => handleSupplierChange(index, 'leadTime', parseInt(e.target.value) || 1)}
                      />
                    </FormControl>

                    <FormControl>
                      <FormLabel>Min Order Qty</FormLabel>
                      <Input
                        type="number"
                        min="1"
                        value={supplier.minimumOrderQuantity}
                        onChange={(e) => handleSupplierChange(index, 'minimumOrderQuantity', parseInt(e.target.value) || 1)}
                      />
                    </FormControl>
                  </HStack>

                  <FormControl mt={3}>
                    <FormLabel>Material Code</FormLabel>
                    <Input
                      placeholder="Enter supplier's material code"
                      value={supplier.supplierMaterialCode}
                      onChange={(e) => handleSupplierChange(index, 'supplierMaterialCode', e.target.value)}
                    />
                  </FormControl>
                </Box>
              ))}

              <Button leftIcon={<AddIcon />} onClick={addSupplier} colorScheme="blue">
                Add Supplier
              </Button>
            </Box>

            {/* Submit Button */}
            <Button
              type="submit"
              colorScheme="teal"
              size="lg"
              isLoading={isSubmitting}
              loadingText="Adding Material"
            >
              Add Material
            </Button>
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
    </ProtectedRoute>
  );
};

export default AddMaterialPage;