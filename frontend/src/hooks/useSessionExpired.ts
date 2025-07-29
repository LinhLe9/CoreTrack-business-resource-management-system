import { useEffect, useState } from 'react';
import { useToast } from '@chakra-ui/react';
import { logout } from '@/lib/auth';

export const useSessionExpired = () => {
  const [isExpired, setIsExpired] = useState(false);
  const [isClient, setIsClient] = useState(false);
  const toast = useToast();

  useEffect(() => {
    setIsClient(true);
  }, []);

  useEffect(() => {
    if (!isClient) return;

    const handleSessionExpired = (event: CustomEvent) => {
      const message = event.detail?.message || 'Your session has expired. Please login again.';
      
      toast({
        title: 'Session Expired',
        description: message,
        status: 'warning',
        duration: 5000,
        isClosable: true,
        position: 'top-right',
      });
      
      setIsExpired(true);
      
      // Auto logout after showing toast
      setTimeout(() => {
        logout();
      }, 2000);
    };

    // Listen for session expired events
    window.addEventListener('sessionExpired', handleSessionExpired as EventListener);

    return () => {
      window.removeEventListener('sessionExpired', handleSessionExpired as EventListener);
    };
  }, [toast, isClient]);

  return { isExpired };
}; 