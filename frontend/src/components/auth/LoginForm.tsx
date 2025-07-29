'use client';

import { useState } from 'react';
import { useRouter } from "next/navigation";
import {
  Box, Button, FormControl, FormLabel, Input, Heading, VStack, useToast
} from '@chakra-ui/react';
import api, { resetLogoutFlag } from '@/lib/axios'; 

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const toast = useToast();
  const router = useRouter();


  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post('/auth/login', { email, password });

      // Reset logout flag on successful login
      resetLogoutFlag();

      // save token 
      const token = res.data.token;
      localStorage.removeItem("token");
      localStorage.setItem("token", token);

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
        </VStack>
      </form>
    </Box>
  );
}