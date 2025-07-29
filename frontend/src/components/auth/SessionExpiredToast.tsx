import { useEffect } from 'react';
import { useToast } from '@chakra-ui/react';

interface SessionExpiredToastProps {
  isExpired: boolean;
  onExpired: () => void;
}

const SessionExpiredToast: React.FC<SessionExpiredToastProps> = ({ isExpired, onExpired }) => {
  const toast = useToast();

  useEffect(() => {
    if (isExpired) {
      toast({
        title: 'Session Expired',
        description: 'Your session has expired. Please login again.',
        status: 'warning',
        duration: 5000,
        isClosable: true,
        position: 'top-right',
      });
      onExpired();
    }
  }, [isExpired, toast, onExpired]);

  return null; // This component doesn't render anything
};

export default SessionExpiredToast; 