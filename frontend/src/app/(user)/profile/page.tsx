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
  Spinner,
  Alert,
  AlertIcon,
} from '@chakra-ui/react';
import { useUser } from '@/hooks/useUser';
import { getErrorMessage } from '@/lib/utils';

const ProfilePage: React.FC = () => {
  const { currentUser, loading, error, fetchCurrentUser } = useUser();

  useEffect(() => {
    fetchCurrentUser();
  }, []);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <Box p={8} textAlign="center">
        <Spinner size="xl" />
        <Text mt={4}>Loading profile information...</Text>
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
      </Box>
    );
  }

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        {/* Page Header */}
        <Box>
          <Heading size="lg" mb={2}>User Profile</Heading>
          <Text color="gray.600">View and manage your account information</Text>
        </Box>

        {/* Profile Information */}
        {currentUser && (
          <Card>
            <CardBody>
              <VStack spacing={6} align="stretch">
                {/* User Avatar and Basic Info */}
                <HStack spacing={4}>
                  <Box
                    width="80px"
                    height="80px"
                    borderRadius="50%"
                    backgroundColor="#3b82f6"
                    display="flex"
                    alignItems="center"
                    justifyContent="center"
                    color="white"
                    fontSize="24px"
                    fontWeight="bold"
                  >
                    {currentUser.email.charAt(0).toUpperCase()}
                  </Box>
                  <VStack align="start" spacing={2}>
                    <Heading size="md">{currentUser.email}</Heading>
                    <Text color="gray.600">{currentUser.username}</Text>
                    <HStack spacing={2}>
                      <Badge colorScheme={
                        currentUser.role === 'OWNER' ? 'red' : 
                        currentUser.role === 'WAREHOUSE_STAFF' ? 'orange' :
                        currentUser.role === 'SALE_STAFF' ? 'green' :
                        currentUser.role === 'PRODUCTION_STAFF' ? 'purple' : 'blue'
                      }>
                        {currentUser.role}
                      </Badge>
                      <Badge colorScheme={currentUser.enabled ? 'green' : 'yellow'}>
                        {currentUser.enabled ? 'Active' : 'Inactive'}
                      </Badge>
                    </HStack>
                  </VStack>
                </HStack>

                {/* Detailed Information */}
                <Card variant="outline">
                  <CardBody>
                    <VStack spacing={4} align="stretch">
                      <Heading size="sm" mb={4}>Account Details</Heading>
                      
                      <HStack justify="space-between">
                        <Text fontWeight="bold" color="gray.600">User ID:</Text>
                        <Text>{currentUser.id}</Text>
                      </HStack>
                      
                      <HStack justify="space-between">
                        <Text fontWeight="bold" color="gray.600">Email:</Text>
                        <Text>{currentUser.email}</Text>
                      </HStack>
                      
                      <HStack justify="space-between">
                        <Text fontWeight="bold" color="gray.600">Username:</Text>
                        <Text>{currentUser.username}</Text>
                      </HStack>
                      
                      <HStack justify="space-between">
                        <Text fontWeight="bold" color="gray.600">Role:</Text>
                        <Badge colorScheme={
                          currentUser.role === 'OWNER' ? 'red' : 
                          currentUser.role === 'WAREHOUSE_STAFF' ? 'orange' :
                          currentUser.role === 'SALE_STAFF' ? 'green' :
                          currentUser.role === 'PRODUCTION_STAFF' ? 'purple' : 'blue'
                        }>
                          {currentUser.role}
                        </Badge>
                      </HStack>
                      
                      <HStack justify="space-between">
                        <Text fontWeight="bold" color="gray.600">Status:</Text>
                        <Badge colorScheme={currentUser.enabled ? 'green' : 'yellow'}>
                          {currentUser.enabled ? 'Active' : 'Inactive'}
                        </Badge>
                      </HStack>
                      
                      <HStack justify="space-between">
                        <Text fontWeight="bold" color="gray.600">Created At:</Text>
                        <Text>{formatDate(currentUser.createdAt)}</Text>
                      </HStack>
                      
                      {currentUser.createdByEmail && (
                        <HStack justify="space-between">
                          <Text fontWeight="bold" color="gray.600">Created By:</Text>
                          <Text>{currentUser.createdByEmail}</Text>
                        </HStack>
                      )}
                    </VStack>
                  </CardBody>
                </Card>
              </VStack>
            </CardBody>
          </Card>
        )}

        {/* No User Data */}
        {!currentUser && !loading && (
          <Card>
            <CardBody>
              <Text color="gray.500" textAlign="center">
                No profile information available
              </Text>
            </CardBody>
          </Card>
        )}
      </VStack>
    </Box>
  );
};

export default ProfilePage; 