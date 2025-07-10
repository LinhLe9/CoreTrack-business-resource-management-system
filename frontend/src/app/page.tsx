'use client'

import {
  Box,
  Button,
  Flex,
  Heading,
  Stack,
  Text,
  useColorModeValue,
} from '@chakra-ui/react'
import { useRouter } from 'next/navigation'

export default function LandingPage() {
  const router = useRouter()

  return (
    <Flex
      minH="100vh"
      align="center"
      justify="center"
      px={6}
      bg={useColorModeValue('gray.50', 'gray.800')}
    >
      <Flex
        maxW="7xl"
        w="full"
        align="center"
        justify="space-between"
        direction={{ base: 'column', md: 'row' }}
        gap={12}
      >
        {/* Left Side: Title + Slogan */}
        <Box flex="1" textAlign={{ base: 'center', md: 'left' }}>
          <Heading as="h1" size="2xl" mb={4}>
            Coretrack
          </Heading>
          <Text fontSize="lg" color="gray.600">
            all in one intuitive system built for small business
          </Text>
        </Box>

        {/* Right Side: CTA + Buttons */}
        <Box flex="1" textAlign={{ base: 'center', md: 'right' }}>
          <Text fontSize="xl" mb={6}>
            Grab your easy resource management system right now
          </Text>
          <Stack direction={{ base: 'column', sm: 'row' }} spacing={4} justify={{ base: 'center', md: 'flex-end' }}>
            <Button
              colorScheme="blue"
              variant="solid"
              onClick={() => router.push('/login')}
            >
              Login
            </Button>
            <Button
              colorScheme="teal"
              variant="outline"
              onClick={() => router.push('/register')}
            >
              Register
            </Button>
          </Stack>
        </Box>
      </Flex>
    </Flex>
  )
}