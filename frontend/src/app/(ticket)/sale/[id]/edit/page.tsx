"use client";

import { useRouter } from 'next/navigation';
import { useParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import {
  Box,
  Heading,
  Text,
  Spinner,
  Center,
  VStack,
  Button,
  Flex,
  HStack,
  useToast,
  FormControl,
  FormLabel,
  Input,
  FormErrorMessage,
  Card,
  CardBody,
  Divider,
} from '@chakra-ui/react';
import { getSaleTicketById, updateSaleTicket } from '@/services/saleService';
import { SaleTicketResponse } from '@/types/sale';
import { useUser } from '@/hooks/useUser';

const SaleEditPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const id = params?.id;
  const toast = useToast();
  const { isOwner, isSaleStaff, user } = useUser();
  
  const [sale, setSale] = useState<SaleTicketResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  
  // Form fields
  const [expectedCompleteDate, setExpectedCompleteDate] = useState<string>('');
  const [customerName, setCustomerName] = useState<string>('');
  const [customerEmail, setCustomerEmail] = useState<string>('');
  const [customerPhone, setCustomerPhone] = useState<string>('');
  const [customerAddress, setCustomerAddress] = useState<string>('');

  // Check if user has permission to access this page
  useEffect(() => {
    console.log('=== Sale Edit Page Debug ===');
    console.log('User from useUser:', user);
    console.log('isOwner():', isOwner());
    console.log('isSaleStaff():', isSaleStaff());
    console.log('User role:', user?.role);
    console.log('================================');
    
    // Add a small delay to ensure user data is loaded
    const checkPermission = () => {
      const hasOwnerPermission = isOwner();
      const hasSaleStaffPermission = isSaleStaff();
      const hasPermission = hasOwnerPermission || hasSaleStaffPermission;
      
      console.log('Permission check:');
      console.log('- hasOwnerPermission:', hasOwnerPermission);
      console.log('- hasSaleStaffPermission:', hasSaleStaffPermission);
      console.log('- hasPermission:', hasPermission);
      
      if (user && !hasPermission) {
        console.log('Access denied - redirecting to /sale');
        toast({
          title: 'Access Denied',
          description: 'Only owners and sale staff can edit sale tickets.',
          status: 'error',
          duration: 3000,
          isClosable: true,
        });
        router.push('/sale');
      } else if (user && hasPermission) {
        console.log('Access granted - user has permission');
      }
    };

    // Check immediately if user is already loaded
    if (user) {
      checkPermission();
    } else {
      // Wait a bit for user data to load
      const timer = setTimeout(checkPermission, 100);
      return () => clearTimeout(timer);
    }
  }, [user, isOwner, isSaleStaff, router, toast]);

  const refreshSale = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await getSaleTicketById(Number(id));
      setSale(data);
      
      // Set form values
      setExpectedCompleteDate(data.expected_complete_date ? new Date(data.expected_complete_date).toISOString().slice(0, 16) : '');
      setCustomerName(data.customerName || '');
      setCustomerEmail(data.customerEmail || '');
      setCustomerPhone(data.customerPhone || '');
      setCustomerAddress(data.customerAddress || '');
    } catch (err: any) {
      setError('Failed to load sale detail.');
      console.error(err);
      toast({
        title: 'Error',
        description: 'Failed to load sale detail.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!id) return;
    refreshSale();
  }, [id]);

  const handleSave = async () => {
    if (!sale || !id) return;
    
    setSaving(true);
    try {
      await updateSaleTicket(Number(id), {
        expected_complete_date: expectedCompleteDate,
        customerName: customerName,
        customerEmail: customerEmail,
        customerPhone: customerPhone,
        customerAddress: customerAddress,
      });
      
      toast({
        title: 'Success',
        description: 'Sale updated successfully.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      
      // Redirect back to sale detail page
      router.push(`/sale/${id}`);
    } catch (err: any) {
      console.error('Error updating sale:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to update sale.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    router.push(`/sale/${id}`);
  };

  if (loading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  if (error || !sale) {
    return (
      <Center minHeight="300px">
        <Text color="red.500" fontSize="lg">
          {error || 'Sale not found.'}
        </Text>
      </Center>
    );
  }

  return (
    <Box maxW="800px" mx="auto" p={6}>
      {/* Header */}
      <Flex justify="space-between" align="center" mb={6}>
        <Heading as="h2" size="xl">
          Edit Sale Order #{sale.id}
        </Heading>
      </Flex>

      {/* Edit Form */}
      <Card>
        <CardBody>
          <VStack spacing={6} align="stretch">
            <Heading as="h3" size="md">
              Sale Information
            </Heading>
            
            <FormControl>
              <FormLabel>Expected Complete Date</FormLabel>
              <Input
                type="datetime-local"
                value={expectedCompleteDate}
                onChange={(e) => setExpectedCompleteDate(e.target.value)}
                placeholder="Select expected complete date"
              />
            </FormControl>

            <Divider />

            <Heading as="h3" size="md">
              Customer Information
            </Heading>

            <FormControl>
              <FormLabel>Customer Name</FormLabel>
              <Input
                value={customerName}
                onChange={(e) => setCustomerName(e.target.value)}
                placeholder="Enter customer name"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Customer Email</FormLabel>
              <Input
                type="email"
                value={customerEmail}
                onChange={(e) => setCustomerEmail(e.target.value)}
                placeholder="Enter customer email"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Customer Phone</FormLabel>
              <Input
                value={customerPhone}
                onChange={(e) => setCustomerPhone(e.target.value)}
                placeholder="Enter customer phone"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Customer Address</FormLabel>
              <Input
                value={customerAddress}
                onChange={(e) => setCustomerAddress(e.target.value)}
                placeholder="Enter customer address"
              />
            </FormControl>

            {/* Action Buttons */}
            <HStack spacing={4} justify="center" pt={4}>
              <Button
                colorScheme="blue"
                onClick={handleSave}
                isLoading={saving}
                loadingText="Saving..."
              >
                Save Changes
              </Button>
              <Button
                variant="outline"
                onClick={handleCancel}
              >
                Cancel
              </Button>
            </HStack>
          </VStack>
        </CardBody>
      </Card>
    </Box>
  );
};

export default SaleEditPage;
