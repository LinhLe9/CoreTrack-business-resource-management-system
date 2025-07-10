'use client';

import { useState } from 'react';
import { useRouter } from "next/navigation";
import {
  Box, Button, FormControl, FormLabel, Input, Heading, VStack, useToast
} from '@chakra-ui/react';
import api from '@/lib/axios'; 

export default function RegisterForm() {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const toast = useToast();
  const router = useRouter();

  const handleRegister = async(e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/auth/register', {
        email,
        username,
        password,
      });

      toast({
        title: "Registration successful",
        description: "Please check your email to validate your account.",
        status: "success",
        duration: 4000,
        isClosable: true,
      });

      // Redirect to validate
      router.push("/register/validation"); 
    } catch (err: any) {
        const errorMessage =
            err?.response?.data?.message ||
            err?.message ||
            "Register failed, please try again";

      toast({
        title: "Registration failed",
        description: errorMessage,
        status: "error",
        duration: 4000,
        isClosable: true,
      });
    }
  };

  return (
    <Box maxW="md" mx="auto" mt={12} p={8} borderWidth={1} borderRadius="lg" boxShadow="lg">
      <Heading mb={6} textAlign="center">Register</Heading>
      <form onSubmit={handleRegister}>
        {/* <form> */}
        <VStack spacing={4}>
          <FormControl isRequired>
            <FormLabel>Email</FormLabel>
            <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            {/* <Input type="email" /> */}
          </FormControl>

          <FormControl isRequired>
            <FormLabel>username</FormLabel>
            <Input value={username} onChange={(e) => setUsername(e.target.value)} />
            {/* <Input type="username" /> */}
          </FormControl>

          <FormControl isRequired>
            <FormLabel>Password</FormLabel>
            <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
            {/* <Input type="password" /> */}
          </FormControl>

          <Button type="submit" colorScheme="green" width="full">
            Register
          </Button>
        </VStack>
      </form>
    </Box>
  );
}