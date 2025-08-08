'use client';

import { useState } from 'react';
import { useRouter } from "next/navigation";
import {
  Box, Button, FormControl, FormLabel, Input, Heading, VStack, useToast, Text, Link
} from '@chakra-ui/react';
import api, { resetLogoutFlag } from '@/lib/axios';
import notificationPollingService from '@/services/websocketService';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const toast = useToast();
  const router = useRouter();


  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post('/auth/login', { email, password });

      console.log('=== Login Response Debug ===');
      console.log('Full response:', res.data);
      console.log('Token:', res.data.token);
      console.log('User data:', res.data);
      console.log('==========================');

      // Reset logout flag on successful login
      resetLogoutFlag();

      // save token 
      const token = res.data.token;
      localStorage.removeItem("token");
      localStorage.setItem("token", token);

      // Save user data from AuthResponse
      const userData = {
        id: res.data.id,
        username: res.data.username,
        email: res.data.email,
        role: res.data.role,
        enabled: res.data.enabled
      };
      localStorage.removeItem("user");
      localStorage.setItem("user", JSON.stringify(userData));
      console.log('User data saved to localStorage:', userData);

      // Start notification polling after successful login
      notificationPollingService.startAfterLogin();

      // pop up noti
      toast({
        title: 'Login Successfully',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });

      console.log('Login success:', res.data);
      router.push("/dashboard"); 
    } catch (err: any) {
        const errorMessage =
            err?.response?.data?.message ||
            err?.message ||
            "Login failed, please try again";

      toast({
        title: 'Login failed',
        description: errorMessage,
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    }
  };

  return (
    <Box maxW="md" mx="auto" mt={12} p={8} borderWidth={1} borderRadius="lg" boxShadow="lg">
      <Heading mb={6} textAlign="center">Login</Heading>
      <form onSubmit={handleLogin}>
      {/* <form> */}
        <VStack spacing={4}>
          <FormControl isRequired>
            <FormLabel>Email</FormLabel>
            <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            {/* <Input type="email" value={email} /> */}
          </FormControl>

          <FormControl isRequired>
            <FormLabel>Password</FormLabel>
            <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
            {/* <Input type="password"value={password} /> */}
          </FormControl>

          <Button type="submit" colorScheme="blue" width="full">
            Login
          </Button>

          <Text textAlign="center" fontSize="sm" color="gray.600">
            Forgot your password?{' '}
            <Link 
              color="blue.500" 
              onClick={() => router.push('/forgot-password')}
              _hover={{ textDecoration: 'underline' }}
              cursor="pointer"
            >
              Click here
            </Link>
          </Text>
        </VStack>
      </form>
    </Box>
  );
}