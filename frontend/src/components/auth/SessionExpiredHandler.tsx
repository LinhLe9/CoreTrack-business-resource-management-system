'use client';

import { useEffect, useState } from 'react';
import { useToast } from '@chakra-ui/react';

const SessionExpiredHandler: React.FC = () => {
  const toast = useToast();
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  useEffect(() => {
    // Only run on client side and after mount
    if (!isMounted || typeof window === 'undefined') return;

    const handleSessionExpired = (event: CustomEvent) => {
      const message = event.detail?.message || 'Your session has expired. Please login again.';
      
      console.log('Session expired:', message);
      
      // Check if we're already on login page to avoid infinite redirects
      if (window.location.pathname === '/login') {
        console.log('Already on login page, skipping redirect');
        return;
      }
      
      // Show toast notification
      toast({
        title: 'Session Expired',
        description: message,
        status: 'warning',
        duration: 5000,
        isClosable: true,
        position: 'top-right',
      });
      
      // Clear auth data
      try {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        localStorage.removeItem('refreshToken');
        sessionStorage.removeItem('refreshToken');
      } catch (error) {
        console.error('Error clearing auth data:', error);
      }
      
      // Redirect to login after a short delay to show the toast
      setTimeout(() => {
        if (window.location.pathname !== '/login') {
          console.log('Redirecting to login page...');
          window.location.href = '/login';
        }
      }, 3000); // Increased delay to ensure toast is shown and avoid conflicts
    };

    // Listen for session expired events
    window.addEventListener('sessionExpired', handleSessionExpired as EventListener);

    return () => {
      window.removeEventListener('sessionExpired', handleSessionExpired as EventListener);
    };
  }, [toast, isMounted]);

  return null; // This component doesn't render anything
};

export default SessionExpiredHandler; 