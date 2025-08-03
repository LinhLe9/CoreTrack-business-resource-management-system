'use client';

import { useEffect, useState } from 'react';
import { Box, Text, Badge, Button, IconButton, VStack } from '@chakra-ui/react';
import { CloseIcon } from '@chakra-ui/icons';
import api from '@/lib/axios';

const BackendStatus: React.FC = () => {
  const [status, setStatus] = useState<'loading' | 'online' | 'offline'>('loading');
  const [lastCheck, setLastCheck] = useState<Date | null>(null);
  const [isMounted, setIsMounted] = useState(false);
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  const checkBackendStatus = async () => {
    try {
      setStatus('loading');
      await api.get('/products/test');
      setStatus('online');
      setLastCheck(new Date());
    } catch (error) {
      console.error('Backend connection failed:', error);
      setStatus('offline');
      setLastCheck(new Date());
    }
  };

  const testSendNotification = async () => {
    try {
      const username = localStorage.getItem('username') || 'testuser';
      await api.post('/test/websocket/send-notification', null, {
        params: { username }
      });
      console.log('Test notification sent successfully');
    } catch (error) {
      console.error('Error sending test notification:', error);
    }
  };

  const testSendUnreadCount = async () => {
    try {
      const username = localStorage.getItem('username') || 'testuser';
      await api.post('/test/websocket/send-unread-count', null, {
        params: { username, count: 5 }
      });
      console.log('Test unread count sent successfully');
    } catch (error) {
      console.error('Error sending test unread count:', error);
    }
  };

  useEffect(() => {
    // Only run on client side and after mount
    if (!isMounted || typeof window === 'undefined') return;
    
    // Don't check backend status on login page to avoid unnecessary API calls
    if (window.location.pathname === '/login') {
      return;
    }
    
    checkBackendStatus();
  }, [isMounted]);

  const getStatusColor = () => {
    switch (status) {
      case 'online': return 'green';
      case 'offline': return 'red';
      default: return 'yellow';
    }
  };

  const getStatusText = () => {
    switch (status) {
      case 'online': return 'Backend Online';
      case 'offline': return 'Backend Offline';
      default: return 'Checking...';
    }
  };

  if (!isVisible) return null;

  return (
    <Box position="fixed" top={4} right={4} zIndex={1000}>
      <Box bg="white" p={3} borderRadius="md" boxShadow="md" border="1px solid" borderColor="gray.200" position="relative">
        <IconButton
          aria-label="Close backend status"
          icon={<CloseIcon />}
          size="xs"
          variant="ghost"
          position="absolute"
          top={1}
          right={1}
          onClick={() => setIsVisible(false)}
          color="gray.500"
          _hover={{ color: 'gray.700' }}
        />
        <Text fontSize="sm" fontWeight="medium" mb={1}>Backend Status</Text>
        <Badge colorScheme={getStatusColor()} mb={2}>
          {getStatusText()}
        </Badge>
        {lastCheck && (
          <Text fontSize="xs" color="gray.600" mb={2}>
            Last check: {lastCheck.toLocaleTimeString()}
          </Text>
        )}
        <VStack spacing={2} align="stretch">
          <Button size="xs" onClick={checkBackendStatus} isLoading={status === 'loading'}>
            Retry
          </Button>
          <Button size="xs" onClick={testSendNotification} colorScheme="blue">
            Test Notification
          </Button>
          <Button size="xs" onClick={testSendUnreadCount} colorScheme="green">
            Test Unread Count
          </Button>
        </VStack>
      </Box>
    </Box>
  );
};

export default BackendStatus; 