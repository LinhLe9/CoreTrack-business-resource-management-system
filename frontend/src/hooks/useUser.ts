import { useState, useEffect } from 'react';
import userService, { UserDetailResponse, CreateUserRequest } from '@/services/userService';
import { getErrorMessage } from '@/lib/utils';

export const useUser = () => {
  const [users, setUsers] = useState<UserDetailResponse[]>([]);
  const [currentUser, setCurrentUser] = useState<UserDetailResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

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

  return {
    users,
    currentUser,
    loading,
    error,
    fetchUsers,
    fetchCurrentUser,
    createUser,
    logout,
    clearError,
  };
}; 