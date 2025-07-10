'use client'

import { useState } from 'react'
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  FormErrorMessage,
  Heading,
  useToast,
  Text,
} from '@chakra-ui/react'
import { useRouter } from 'next/navigation'
import api from '@/lib/axios'

export default function TokenVerificationForm() {
  const [token, setToken] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isResending, setIsResending] = useState(false)
  const [error, setError] = useState('')
  const toast = useToast()
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    setError('')
    try {
      const res = await api.post('/auth/verify', { token })

      toast({
        title: 'Email validation succeed!',
        status: 'success',
        duration: 3000,
        isClosable: true,
      })

      router.push('/login')
    } catch (err: any) {
      const message =
        err?.response?.data?.message || 'Error! Please try again'
      setError(message)

      toast({
        title: 'Validation failed',
        description: message,
        status: 'error',
        duration: 3000,
        isClosable: true,
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleResend = async () => {
    setIsResending(true);
    try {
      await api.post('/auth/resend-token');
      toast({
        title: 'Please check your email. New validation code had been sent!',
        status: 'info',
        duration: 3000,
        isClosable: true,
      });
    } catch (err: any) {
      toast({
        title: 'Could not promt sending the new validation code, please try again',
        description: err?.response?.data?.message || 'Error',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setIsResending(false);
    }
  };

  return (
    <Box maxW="md" mx="auto" mt={10} p={6} borderWidth={1} borderRadius="lg">
      <Heading size="md" mb={6} textAlign="center">
        Validation
      </Heading>
      <form onSubmit={handleSubmit}>
        <FormControl isInvalid={!!error} mb={4}>
          <FormLabel>Token</FormLabel>
          <Input
            placeholder="Please enter the validation code sent to your registered email"
            value={token}
            onChange={(e) => setToken(e.target.value)}
            required
          />
          {error && <FormErrorMessage>{error}</FormErrorMessage>}
        </FormControl>

        <Button
          type="submit"
          colorScheme="blue"
          width="full"
          isLoading={isSubmitting}
        >
          Submit
        </Button>
      </form>
      
    <Text mt={4} fontSize="sm">
        Did not receive validation email?{' '}
        <Text
            as="span"
            color="blue.500"
            cursor="pointer"
            fontWeight="medium"
            onClick={handleResend}
            _hover={{ textDecoration: 'underline' }}
        > Resend validation email
        </Text>
    </Text>
    </Box>
  )
}