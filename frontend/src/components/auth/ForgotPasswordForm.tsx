'use client';

import { useState } from 'react';
import { useRouter } from "next/navigation";
import {
  Box, Button, FormControl, FormLabel, Input, Heading, VStack, useToast, Text, Link
} from '@chakra-ui/react';
import api from '@/lib/axios';

export default function ForgotPasswordForm() {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const toast = useToast();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const res = await api.post('/auth/forgot-password', { email });

      toast({
        title: 'Email sent successfully',
        description: 'Please check your email for password reset instructions.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });

      // Redirect to login page after successful submission
      setTimeout(() => {
        router.push('/login');
      }, 2000);

    } catch (err: any) {
      const errorMessage =
        err?.response?.data ||
        err?.message ||
        "Failed to send password reset email";

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

  return (
    <Box maxW="md" mx="auto" mt={12} p={8} borderWidth={1} borderRadius="lg" boxShadow="lg">
      <Heading mb={6} textAlign="center">Forgot Password</Heading>
      <Text mb={6} textAlign="center" color="gray.600">
        Enter your email address and we'll send you a link to reset your password.
      </Text>
      
      <form onSubmit={handleSubmit}>
        <VStack spacing={4}>
          <FormControl isRequired>
            <FormLabel>Email</FormLabel>
            <Input 
              type="email" 
              value={email} 
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email address"
            />
          </FormControl>

          <Button 
            type="submit" 
            colorScheme="blue" 
            width="full"
            isLoading={isLoading}
            loadingText="Sending..."
          >
            Send Reset Link
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
