'use client';

import React, { useEffect, useState } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Badge,
  Card,
  CardBody,
  Heading,
  Button,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  FormControl,
  FormLabel,
  Input,
  Select,
  useDisclosure,
  Spinner,
  Alert,
  AlertIcon,
} from '@chakra-ui/react';
import { useRouter } from 'next/navigation';
import { useUser } from '@/hooks/useUser';
import { CreateUserRequest } from '@/services/userService';
import { getErrorMessage } from '@/lib/utils';

const AdminPage: React.FC = () => {
  const { 
    users, 
    currentUser, 
    loading, 
    error, 
    fetchUsers, 
    fetchCurrentUser, 
    createUser,
    clearError,
    user,
    isOwner
  } = useUser();
  
  const { isOpen, onOpen, onClose } = useDisclosure();
  const toast = useToast();
  const router = useRouter();

  const [formData, setFormData] = useState<CreateUserRequest>({
    email: '',
    role: 'WAREHOUSE_STAFF',
    createdBy: 0,
  });

  // Check if user has permission to access this page
  useEffect(() => {
    console.log('=== User Management Page Debug ===');
    console.log('User from useUser:', user);
    console.log('isOwner():', isOwner());
    console.log('====================================');
    
    if (user && !isOwner()) {
      console.log('Access denied - redirecting to /dashboard');
      toast({
        title: 'Access Denied',
        description: 'Only owners can access the admin dashboard.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      router.push('/dashboard');
    }
  }, [user, isOwner, router, toast]);

  useEffect(() => {
    // Luôn lấy thông tin user hiện tại
    fetchCurrentUser();
  }, []);

  useEffect(() => {
    // if current user us OWNER, lấy danh sách users
    if (currentUser && currentUser.role === 'OWNER') {
      console.log('Fetching users for OWNER:', currentUser.email);
      fetchUsers();
    }
  }, [currentUser]);

  useEffect(() => {
    if (currentUser) {
      setFormData(prev => ({ ...prev, createdBy: currentUser.id }));
    }
  }, [currentUser]);

  const handleCreateUser = async () => {
    const success = await createUser(formData);
    if (success) {
      toast({
        title: 'User created successfully',
        description: 'Verification email has been sent to the user.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      onClose();
      setFormData({ email: '', role: 'WAREHOUSE_STAFF', createdBy: currentUser?.id || 0 });
    } else {
      toast({
        title: 'Failed to create user',
        description: getErrorMessage(error),
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading && !currentUser) {
    return (
      <Box p={8} textAlign="center">
        <Spinner size="xl" />
        <Text mt={4}>Loading user information...</Text>
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={8}>
        <Alert status="error">
          <AlertIcon />
          {getErrorMessage(error)}
        </Alert>
        <Button mt={4} onClick={clearError}>
          Try Again
        </Button>
      </Box>
    );
  }

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        {/* Page Header */}
        <Box>
          <Heading size="lg" mb={2}>Admin Dashboard</Heading>
          <Text color="gray.600">Manage users and system settings</Text>
        </Box>

        {/* Current User Info */}
        {currentUser && (
          <Card>
            <CardBody>
              <Heading size="md" mb={4}>Current User Information</Heading>
              <VStack align="start" spacing={3}>
                <HStack justify="space-between" w="full">
                  <Text><strong>Email:</strong> {currentUser.email}</Text>
                  <HStack>
                    <Badge colorScheme={
                      currentUser.role === 'OWNER' ? 'red' : 
                      currentUser.role === 'WAREHOUSE_STAFF' ? 'orange' :
                      currentUser.role === 'SALE_STAFF' ? 'green' :
                      currentUser.role === 'PRODUCTION_STAFF' ? 'purple' : 'blue'
                    }>
                      {currentUser.role}
                    </Badge>
                    <Badge colorScheme={currentUser.enabled ? 'green' : 'yellow'}>
                      {currentUser.enabled ? 'Enabled' : 'Disabled'}
                    </Badge>
                  </HStack>
                </HStack>
                <Text><strong>Username:</strong> {currentUser.username}</Text>
                <Text><strong>User ID:</strong> {currentUser.id}</Text>
                <Text><strong>Created At:</strong> {formatDate(currentUser.createdAt)}</Text>
                {currentUser.createdByEmail && (
                  <Text><strong>Created By:</strong> {currentUser.createdByEmail}</Text>
                )}
              </VStack>
            </CardBody>
          </Card>
        )}

        {/* Users Management Section - Only for OWNER */}
        {currentUser && currentUser.role === 'OWNER' && (
          <Card>
            <CardBody>
              <HStack justify="space-between" mb={4}>
                <Box>
                  <Heading size="md">Users Management</Heading>
                  <Text color="gray.600" mt={1}>
                    Manage all users in the system ({users.length} users)
                  </Text>
                </Box>
                <Button colorScheme="blue" onClick={onOpen}>
                  Create New User
                </Button>
              </HStack>
              
                             {loading && users.length === 0 ? (
                 <Box textAlign="center" py={8}>
                   <Spinner />
                   <Text mt={2}>Loading users...</Text>
                 </Box>
               ) : users.length === 0 ? (
                <Text color="gray.500" textAlign="center" py={8}>
                  No users found
                </Text>
              ) : (
                <VStack spacing={3} align="stretch">
                  {users.map((user) => (
                    <Card key={user.id} variant="outline">
                      <CardBody>
                        <VStack align="start" spacing={2}>
                          <HStack justify="space-between" w="full">
                            <Box>
                              <Text fontWeight="bold">{user.email}</Text>
                              <Text fontSize="sm" color="gray.600">
                                {user.username}
                              </Text>
                            </Box>
                            <HStack>
                              <Badge colorScheme={
                                user.role === 'OWNER' ? 'red' : 
                                user.role === 'WAREHOUSE_STAFF' ? 'orange' :
                                user.role === 'SALE_STAFF' ? 'green' :
                                user.role === 'PRODUCTION_STAFF' ? 'purple' : 'blue'
                              }>
                                {user.role}
                              </Badge>
                              <Badge colorScheme={user.enabled ? 'green' : 'yellow'}>
                                {user.enabled ? 'Enabled' : 'Disabled'}
                              </Badge>
                            </HStack>
                          </HStack>
                          <Text fontSize="sm" color="gray.600">
                            <strong>Created:</strong> {formatDate(user.createdAt)}
                          </Text>
                          {user.createdByEmail && (
                            <Text fontSize="sm" color="gray.600">
                              <strong>Created by:</strong> {user.createdByEmail}
                            </Text>
                          )}
                        </VStack>
                      </CardBody>
                    </Card>
                  ))}
                </VStack>
              )}
            </CardBody>
          </Card>
        )}

        {/* Access Denied for non-OWNER users */}
        {currentUser && currentUser.role !== 'OWNER' && (
          <Card>
            <CardBody>
              <VStack spacing={4}>
                <Alert status="info">
                  <AlertIcon />
                  You don't have permission to manage users. Only OWNER can access user management features.
                </Alert>
                <Text color="gray.600">
                  Your role: <Badge colorScheme="blue">{currentUser.role}</Badge>
                </Text>
              </VStack>
            </CardBody>
          </Card>
        )}
      </VStack>

      {/* Create User Modal - Only for OWNER */}
      {currentUser && currentUser.role === 'OWNER' && (
        <Modal isOpen={isOpen} onClose={onClose}>
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>Create New User</ModalHeader>
            <ModalBody>
              <VStack spacing={4}>
                <FormControl isRequired>
                  <FormLabel>Email</FormLabel>
                  <Input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData(prev => ({ ...prev, email: e.target.value }))}
                    placeholder="Enter email address"
                  />
                </FormControl>
                
                <FormControl isRequired>
                  <FormLabel>Role</FormLabel>
                  <Select
                    value={formData.role}
                    onChange={(e) => setFormData(prev => ({ ...prev, role: e.target.value as 'OWNER' | 'WAREHOUSE_STAFF' | 'SALE_STAFF' | 'PRODUCTION_STAFF' }))}
                  >
                    <option value="WAREHOUSE_STAFF">Warehouse Staff</option>
                    <option value="SALE_STAFF">Sale Staff</option>
                    <option value="PRODUCTION_STAFF">Production Staff</option>
                    <option value="OWNER">Owner</option>
                  </Select>
                </FormControl>

                <Box w="full" p={3} bg="blue.50" borderRadius="md">
                  <Text fontSize="sm" color="blue.700">
                    <strong>Note:</strong> A verification email will be sent to the user with login credentials.
                  </Text>
                </Box>
              </VStack>
            </ModalBody>
            <ModalFooter>
              <Button variant="ghost" mr={3} onClick={onClose}>
                Cancel
              </Button>
              <Button 
                colorScheme="blue" 
                onClick={handleCreateUser} 
                isLoading={loading}
                isDisabled={!formData.email}
              >
                Create User
              </Button>
            </ModalFooter>
          </ModalContent>
        </Modal>
      )}
    </Box>
  );
};

export default AdminPage; 