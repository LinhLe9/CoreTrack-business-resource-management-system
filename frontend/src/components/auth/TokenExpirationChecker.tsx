'use client';

import { useEffect, useState } from 'react';
import { useToast } from '@chakra-ui/react';
import { checkTokenExpiration } from '@/lib/auth';

const TokenExpirationChecker: React.FC = () => {
  const toast = useToast();
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  useEffect(() => {
    // Only run on client side and after mount
    if (!isMounted || typeof window === 'undefined') return;

    // Don't check token expiration on login page
    if (window.location.pathname === '/login') {
      return;
    }

    const checkToken = () => {
      // Don't check if we're on login page
      if (window.location.pathname === '/login') {
        return;
      }

      if (checkTokenExpiration()) {
        toast({
          title: 'Session Warning',
          description: 'Your session will expire soon. Please save your work.',
          status: 'warning',
          duration: 10000,
          isClosable: true,
          position: 'top-right',
        });
      }
    };

    // Check token every 30 seconds
    const interval = setInterval(checkToken, 30000);

    // Also check immediately on mount
    checkToken();

    return () => {
      clearInterval(interval);
    };
  }, [toast, isMounted]);

  return null; // This component doesn't render anything
};

export default TokenExpirationChecker; 