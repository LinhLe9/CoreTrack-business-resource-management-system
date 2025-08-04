'use client'

import { Box, Flex, Image, Button, HStack, Text } from '@chakra-ui/react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'

export default function PublicHeader() {
  const router = useRouter();

  return (
    <Box as="header" w="100%" py={4} px={8} bg="white" boxShadow="sm" borderBottom="1px solid" borderColor="gray.200">
      <Flex maxW="7xl" mx="auto" justify="space-between" align="center">
        {/* Left side: Logo */}
        <Link href="/">
          <Box display="flex" alignItems="center" cursor="pointer">
            <Image src="/coretrack.png" alt="Logo" height="40px" />
          </Box>
        </Link>

        {/* Right side: Basic navigation for public pages */}
        <HStack spacing={4}>
          <Button 
            variant="ghost" 
            onClick={() => router.push('/login')}
            color="blue.600"
            _hover={{ color: 'blue.700' }}
          >
            Login
          </Button>
          <Button 
            variant="outline" 
            onClick={() => router.push('/register')}
            colorScheme="blue"
          >
            Register
          </Button>
        </HStack>
      </Flex>
    </Box>
  )
}