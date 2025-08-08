import React from 'react';
import { useRouter } from 'next/navigation';
import { useUser } from '../../hooks/useUser';
import { Box, Center, Spinner, Text } from '@chakra-ui/react';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: 'OWNER' | 'WAREHOUSE_STAFF' | 'SALE_STAFF' | 'PRODUCTION_STAFF';
  requiredRoles?: ('OWNER' | 'WAREHOUSE_STAFF' | 'SALE_STAFF' | 'PRODUCTION_STAFF')[];
  fallbackPath?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRole = 'OWNER',
  requiredRoles,
  fallbackPath = '/'
}) => {
  const { user, loading, isOwner, isWarehouseStaff, isSaleStaff, isProductionStaff } = useUser();
  const router = useRouter();

  React.useEffect(() => {
    if (!loading && !user) {
      router.push('/login');
      return;
    }

    if (!loading && user) {
      let hasRequiredRole = false;

      if (requiredRoles) {
        // Check multiple roles
        hasRequiredRole = requiredRoles.some(role => {
          switch (role) {
            case 'OWNER':
              return isOwner();
            case 'WAREHOUSE_STAFF':
              return isWarehouseStaff();
            case 'SALE_STAFF':
              return isSaleStaff();
            case 'PRODUCTION_STAFF':
              return isProductionStaff();
            default:
              return false;
          }
        });
      } else {
        // Check single role
        switch (requiredRole) {
          case 'OWNER':
            hasRequiredRole = isOwner();
            break;
          case 'WAREHOUSE_STAFF':
            hasRequiredRole = isWarehouseStaff();
            break;
          case 'SALE_STAFF':
            hasRequiredRole = isSaleStaff();
            break;
          case 'PRODUCTION_STAFF':
            hasRequiredRole = isProductionStaff();
            break;
        }
      }

      if (!hasRequiredRole) {
        router.push(fallbackPath);
        return;
      }
    }
  }, [user, loading, requiredRole, requiredRoles, fallbackPath, router, isOwner, isWarehouseStaff, isSaleStaff, isProductionStaff]);

  if (loading) {
    return (
      <Center minH="100vh">
        <Box textAlign="center">
          <Spinner size="xl" color="blue.500" />
          <Text mt={4}>Loading...</Text>
        </Box>
      </Center>
    );
  }

  if (!user) {
    return (
      <Center minH="100vh">
        <Box textAlign="center">
          <Spinner size="xl" color="blue.500" />
          <Text mt={4}>Redirecting to login...</Text>
        </Box>
      </Center>
    );
  }

  // Check if user has required role (redundant check for display, but ensures correct rendering after initial load)
  let hasRequiredRole = false;

  if (requiredRoles) {
    // Check multiple roles
    hasRequiredRole = requiredRoles.some(role => {
      switch (role) {
        case 'OWNER':
          return isOwner();
        case 'WAREHOUSE_STAFF':
          return isWarehouseStaff();
        case 'SALE_STAFF':
          return isSaleStaff();
        case 'PRODUCTION_STAFF':
          return isProductionStaff();
        default:
          return false;
      }
    });
  } else {
    // Check single role
    switch (requiredRole) {
      case 'OWNER':
        hasRequiredRole = isOwner();
        break;
      case 'WAREHOUSE_STAFF':
        hasRequiredRole = isWarehouseStaff();
        break;
      case 'SALE_STAFF':
        hasRequiredRole = isSaleStaff();
        break;
      case 'PRODUCTION_STAFF':
        hasRequiredRole = isProductionStaff();
        break;
    }
  }

  if (!hasRequiredRole) {
    return (
      <Center minH="100vh">
        <Box textAlign="center">
          <Spinner size="xl" color="blue.500" />
          <Text mt={4}>Redirecting...</Text>
        </Box>
      </Center>
    );
  }

  return <>{children}</>;
};

export default ProtectedRoute; 