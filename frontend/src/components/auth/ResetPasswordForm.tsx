'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from "next/navigation";
import {
  Box, Button, FormControl, FormLabel, Input, Heading, VStack, useToast, Text, Link, Alert, AlertIcon
} from '@chakra-ui/react';
import api from '@/lib/axios';

export default function ResetPasswordForm() {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isValidating, setIsValidating] = useState(true);
  const [isTokenValid, setIsTokenValid] = useState(false);
  const [error, setError] = useState('');
  
  const toast = useToast();
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get('token');

  useEffect(() => {
    const validateToken = async () => {
      if (!token) {
        setError('No reset token provided');
        setIsValidating(false);
        return;
      }

      try {
        await api.post('/auth/validate-reset-token', { token });
        setIsTokenValid(true);
      } catch (err: any) {
        const errorMessage = err?.response?.data || 'Invalid or expired token';
        setError(errorMessage);
        setIsTokenValid(false);
      } finally {
        setIsValidating(false);
      }
    };

    validateToken();
  }, [token]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (newPassword !== confirmPassword) {
      toast({
        title: 'Passwords do not match',
        description: 'Please make sure both passwords are the same.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    if (newPassword.length < 6) {
      toast({
        title: 'Password too short',
        description: 'Password must be at least 6 characters long.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    setIsLoading(true);

    try {
      await api.post('/auth/reset-password', { 
        token, 
        newPassword 
      });

      toast({
        title: 'Password reset successfully',
        description: 'You can now login with your new password.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });

      // Redirect to login page after successful reset
      setTimeout(() => {
        router.push('/login');
      }, 2000);

    } catch (err: any) {
      const errorMessage =
        err?.response?.data ||
        err?.message ||
        "Failed to reset password";

      toast({
        title: 'Error',
        description: errorMessage,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setIsLoading(false);
    }
  };

  if (isValidating) {
    return (
      <Box maxW="md" mx="auto" mt={12} p={8} borderWidth={1} borderRadius="lg" boxShadow="lg">
        <Heading mb={6} textAlign="center">Validating Reset Token</Heading>
        <Text textAlign="center" color="gray.600">
          Please wait while we validate your reset token...
        </Text>
      </Box>
    );
  }

  if (!isTokenValid) {
    return (
      <Box maxW="md" mx="auto" mt={12} p={8} borderWidth={1} borderRadius="lg" boxShadow="lg">
        <Heading mb={6} textAlign="center">Invalid Reset Link</Heading>
        <Alert status="error" mb={4}>
          <AlertIcon />
          {error}
        </Alert>
        <Text mb={4} textAlign="center" color="gray.600">
          This password reset link is invalid or has expired.
        </Text>
        <Link 
          color="blue.500" 
          onClick={() => router.push('/forgot-password')}
          _hover={{ textDecoration: 'underline' }}
        >
          Request a new password reset
        </Link>
      </Box>
    );
  }

  return (
    <Box maxW="md" mx="auto" mt={12} p={8} borderWidth={1} borderRadius="lg" boxShadow="lg">
      <Heading mb={6} textAlign="center">Reset Password</Heading>
      <Text mb={6} textAlign="center" color="gray.600">
        Enter your new password below.
      </Text>
      
      <form onSubmit={handleSubmit}>
        <VStack spacing={4}>
          <FormControl isRequired>
            <FormLabel>New Password</FormLabel>
            <Input 
              type="password" 
              value={newPassword} 
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Enter new password"
              minLength={6}
            />
          </FormControl>

          <FormControl isRequired>
            <FormLabel>Confirm New Password</FormLabel>
            <Input 
              type="password" 
              value={confirmPassword} 
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm new password"
              minLength={6}
            />
          </FormControl>

          <Button 
            type="submit" 
            colorScheme="blue" 
            width="full"
            isLoading={isLoading}
            loadingText="Resetting..."
          >
            Reset Password
          </Button>

          <Link 
            color="blue.500" 
            onClick={() => router.push('/login')}
            _hover={{ textDecoration: 'underline' }}
          >
            Back to Login
          </Link>
        </VStack>
      </form>
    </Box>
  );
}
