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
  Switch,
  FormControl,
  FormLabel,
  FormHelperText,
  Button,
  useToast,
  Divider,
} from '@chakra-ui/react';
import { useUser } from '@/hooks/useUser';
import { getErrorMessage } from '@/lib/utils';
import { emailService, EmailAlertConfig } from '@/services/emailService';

const ProfilePage: React.FC = () => {
  const { currentUser, loading, error, fetchCurrentUser } = useUser();
  const [emailConfig, setEmailConfig] = useState<EmailAlertConfig | null>(null);
  const [emailLoading, setEmailLoading] = useState(false);
  const [emailError, setEmailError] = useState<string | null>(null);
  const toast = useToast();

  useEffect(() => {
    fetchCurrentUser();
  }, []);

  // Load email configuration
  useEffect(() => {
    const loadEmailConfig = async () => {
      if (currentUser && (currentUser.role === 'OWNER' )) {
        setEmailLoading(true);
        setEmailError(null);
        try {
          const config = await emailService.getEmailAlertConfig();
          setEmailConfig(config);
        } catch (err: any) {
          setEmailError(getErrorMessage(err));
          console.error('Error loading email config:', err);
        } finally {
          setEmailLoading(false);
        }
      }
    };

    loadEmailConfig();
  }, [currentUser]);

  const handleEmailConfigChange = async (field: keyof EmailAlertConfig, value: boolean) => {
    if (!emailConfig) return;

    const updatedConfig = { ...emailConfig, [field]: value };
    setEmailConfig(updatedConfig);

    try {
      await emailService.updateEmailAlertConfig(updatedConfig);
      toast({
        title: 'Email settings updated',
        description: 'Your email alert preferences have been saved.',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
    } catch (err: any) {
      // Revert on error
      setEmailConfig(emailConfig);
      toast({
        title: 'Failed to update settings',
        description: getErrorMessage(err),
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

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

        {/* Email Alert Settings - Only for OWNER */}
        {currentUser && (currentUser.role === 'OWNER') && (
          <Card>
            <CardBody>
              <VStack spacing={6} align="stretch">
                <Heading size="md">Email Alert Settings</Heading>
                <Text color="gray.600" fontSize="sm">
                  Configure which email alerts you want to receive for inventory and ticket status changes.
                </Text>

                {emailLoading ? (
                  <Box textAlign="center" py={4}>
                    <Spinner />
                    <Text mt={2}>Loading email settings...</Text>
                  </Box>
                ) : emailError ? (
                  <Alert status="error">
                    <AlertIcon />
                    {emailError}
                  </Alert>
                ) : emailConfig ? (
                  <VStack spacing={4} align="stretch">
                    {/* Inventory Alerts */}
                    <Box>
                      <Text fontWeight="bold" mb={3} color="blue.600">
                        📦 Inventory Alerts
                      </Text>
                      
                      <VStack spacing={3} align="stretch">
                        <HStack justify="space-between" align="center">
                          <Text fontSize="xs" color="gray.500" flex="1">
                            Receive emails when product/material stock falls below minimum level
                          </Text>
                          <Switch
                            id="low-stock"
                            isChecked={emailConfig.lowStockEnabled}
                            onChange={(e) => handleEmailConfigChange('lowStockEnabled', e.target.checked)}
                            colorScheme="blue"
                          />
                        </HStack>

                        <HStack justify="space-between" align="center">
                          <Text fontSize="xs" color="gray.500" flex="1">
                            Receive emails when product/material stock exceeds maximum level
                          </Text>
                          <Switch
                            id="over-stock"
                            isChecked={emailConfig.overStockEnabled}
                            onChange={(e) => handleEmailConfigChange('overStockEnabled', e.target.checked)}
                            colorScheme="blue"
                          />
                        </HStack>

                        <HStack justify="space-between" align="center">
                          <Text fontSize="xs" color="gray.500" flex="1">
                            Receive emails when product/material stock reaches zero
                          </Text>
                          <Switch
                            id="out-of-stock"
                            isChecked={emailConfig.outOfStockEnabled}
                            onChange={(e) => handleEmailConfigChange('outOfStockEnabled', e.target.checked)}
                            colorScheme="blue"
                          />
                        </HStack>
                      </VStack>
                    </Box>

                    <Divider />

                    {/* Ticket Alerts */}
                    <Box>
                      <Text fontWeight="bold" mb={3} color="green.600">
                        🎫 Ticket Status Alerts
                      </Text>
                      
                      <HStack justify="space-between" align="center">
                        <Text fontSize="xs" color="gray.500" flex="1">
                          Receive emails when production, purchasing, or sale ticket status changes
                        </Text>
                        <Switch
                          id="ticket-status"
                          isChecked={emailConfig.ticketStatusChangeEnabled}
                          onChange={(e) => handleEmailConfigChange('ticketStatusChangeEnabled', e.target.checked)}
                          colorScheme="green"
                        />
                      </HStack>
                    </Box>

                    {/* Info Box */}
                    <Box p={4} bg="blue.50" borderRadius="md" border="1px solid" borderColor="blue.200">
                      <Text fontSize="sm" color="blue.700">
                        <strong>ℹ️ Note:</strong> Email alerts will be sent to you and relevant staff members based on your role and the type of alert. 
                        The system automatically determines the appropriate recipients for each alert type.
                      </Text>
                    </Box>
                  </VStack>
                ) : (
                  <Text color="gray.500" textAlign="center">
                    Unable to load email settings
                  </Text>
                )}
              </VStack>
            </CardBody>
          </Card>
        )}
      </VStack>
    </Box>
  );
};

export default ProfilePage; 