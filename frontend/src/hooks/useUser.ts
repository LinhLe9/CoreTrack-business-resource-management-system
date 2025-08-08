import { useState, useEffect } from 'react';
import userService, { UserDetailResponse, CreateUserRequest } from '@/services/userService';
import { getErrorMessage } from '@/lib/utils';

interface User {
  id: number;
  username: string;
  email: string;
  role: 'OWNER' | 'WAREHOUSE_STAFF' | 'SALE_STAFF' | 'PRODUCTION_STAFF';
  enabled: boolean;
}

export const useUser = () => {
  const [user, setUser] = useState<User | null>(null);
  const [users, setUsers] = useState<UserDetailResponse[]>([]);
  const [currentUser, setCurrentUser] = useState<UserDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const getUser = () => {
      try {
        const userStr = localStorage.getItem('user') || sessionStorage.getItem('user');
        console.log('=== useUser Debug ===');
        console.log('User string from storage:', userStr);
        if (userStr) {
          const userData = JSON.parse(userStr);
          console.log('Parsed user data:', userData);
          console.log('User role:', userData?.role);
          setUser(userData);
        } else {
          console.log('No user data found in storage');
          setUser(null);
        }
        console.log('=====================');
      } catch (error) {
        console.error('Error parsing user data:', error);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    getUser();
  }, []);

  // Get all users
  const fetchUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const usersData = await userService.getAllUsers();
      console.log('Setting users in useUser:', usersData);
      setUsers(usersData);
    } catch (err: any) {
      setError(getErrorMessage(err));
      console.error('Error fetching users:', err);
    } finally {
      setLoading(false);
    }
  };

  // Get current user details
  const fetchCurrentUser = async () => {
    setLoading(true);
    setError(null);
    try {
      const userData = await userService.getCurrentUserDetails();
      setCurrentUser(userData);
    } catch (err: any) {
      setError(getErrorMessage(err));
      console.error('Error fetching current user:', err);
    } finally {
      setLoading(false);
    }
  };

  // Create new user
  const createUser = async (createUserRequest: CreateUserRequest): Promise<boolean> => {
    setLoading(true);
    setError(null);
    try {
      await userService.createUser(createUserRequest);
      // Refresh users list after creating new user
      await fetchUsers();
      return true;
    } catch (err: any) {
      setError(getErrorMessage(err));
      console.error('Error creating user:', err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  // Logout user
  const logout = async (): Promise<boolean> => {
    setLoading(true);
    setError(null);
    try {
      await userService.logout();
      // Clear local state
      setUsers([]);
      setCurrentUser(null);
      setUser(null);
      // Clear token from localStorage
      localStorage.removeItem('token');
      return true;
    } catch (err: any) {
      setError(getErrorMessage(err));
      console.error('Error during logout:', err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  // Clear error
  const clearError = () => {
    setError(null);
  };

  // Refresh user data from localStorage (for debugging)
  const refreshUserData = () => {
    try {
      const userStr = localStorage.getItem('user') || sessionStorage.getItem('user');
      console.log('=== refreshUserData Debug ===');
      console.log('User string from storage:', userStr);
      if (userStr) {
        const userData = JSON.parse(userStr);
        console.log('Parsed user data:', userData);
        console.log('User role:', userData?.role);
        setUser(userData);
      } else {
        console.log('No user data found in storage');
        setUser(null);
      }
      console.log('=============================');
    } catch (error) {
      console.error('Error parsing user data:', error);
      setUser(null);
    }
  };

  // Role-based checks
  const isOwner = () => {
    const result = user?.role === 'OWNER';
    console.log('=== isOwner() Debug ===');
    console.log('Current user:', user);
    console.log('User role:', user?.role);
    console.log('isOwner result:', result);
    console.log('=======================');
    return result;
  };
  const isWarehouseStaff = () => {
    const result = user?.role === 'WAREHOUSE_STAFF';
    console.log('=== isWarehouseStaff() Debug ===');
    console.log('Current user:', user);
    console.log('User role:', user?.role);
    console.log('isWarehouseStaff result:', result);
    console.log('===============================');
    return result;
  };
  const isSaleStaff = () => {
    const result = user?.role === 'SALE_STAFF';
    console.log('=== isSaleStaff() Debug ===');
    console.log('Current user:', user);
    console.log('User role:', user?.role);
    console.log('isSaleStaff result:', result);
    console.log('=============================');
    return result;
  };
  const isProductionStaff = () => {
    const result = user?.role === 'PRODUCTION_STAFF';
    console.log('=== isProductionStaff() Debug ===');
    console.log('Current user:', user);
    console.log('User role:', user?.role);
    console.log('isProductionStaff result:', result);
    console.log('=================================');
    return result;
  };

  return {
    user,
    users,
    currentUser,
    loading,
    error,
    fetchUsers,
    fetchCurrentUser,
    createUser,
    logout,
    clearError,
    refreshUserData,
    isOwner,
    isWarehouseStaff,
    isSaleStaff,
    isProductionStaff,
  };
}; 