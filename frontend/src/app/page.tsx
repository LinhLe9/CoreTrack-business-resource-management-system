'use client'

import {
  Box,
  Button,
  Flex,
  Heading,
  Stack,
  Text,
  useColorModeValue,
  Image,
  Container,
} from '@chakra-ui/react'
import { useRouter } from 'next/navigation'

export default function LandingPage() {
  const router = useRouter()

  return (
    <Container maxW="7xl" px={6} py={12}>
      <Flex
        minH="calc(100vh - 100px)"
        align="center"
        justify="space-between"
        direction={{ base: 'column', lg: 'row' }}
        gap={{ base: 8, lg: 12 }}
      >
        {/* Left Side: Content */}
        <Box flex="1" maxW={{ base: 'full', lg: '50%' }}>
          <Stack spacing={8} align={{ base: 'center', lg: 'start' }} textAlign={{ base: 'center', lg: 'left' }}>
            {/* Heading with impressive font */}
            <Box>
              <Heading 
                as="h1" 
                size="4xl" 
                mb={4}
                bgGradient="linear(to-r, blue.600, teal.600, purple.600)"
                bgClip="text"
                fontWeight="900"
                letterSpacing="tight"
                lineHeight="1.1"
                fontFamily="'Poppins', sans-serif"
                textShadow="2px 2px 4px rgba(0,0,0,0.1)"
              >
                CoreTrack
              </Heading>
              <Text 
                fontSize="xl" 
                color="gray.600"
                fontWeight="500"
                maxW="500px"
                lineHeight="1.6"
              >
                All-in-one intuitive system built for small business
              </Text>
            </Box>

            {/* Description */}
            <Box>
              <Text 
                fontSize="lg" 
                color="gray.700"
                maxW="600px"
                lineHeight="1.7"
                fontWeight="400"
              >
                Grab your easy resource management system right now. Streamline your operations, 
                track inventory, manage production, and grow your business with confidence.
              </Text>
            </Box>

            {/* CTA Buttons */}
            <Stack 
              direction={{ base: 'column', sm: 'row' }} 
              spacing={4} 
              justify={{ base: 'center', lg: 'flex-start' }}
              w="full"
            >
              <Button
                size="lg"
                colorScheme="blue"
                variant="solid"
                onClick={() => router.push('/login')}
                px={8}
                py={6}
                fontSize="lg"
                fontWeight="600"
                _hover={{
                  transform: 'translateY(-2px)',
                  boxShadow: 'lg',
                }}
                transition="all 0.2s"
              >
                Get Started
              </Button>
              <Button
                size="lg"
                colorScheme="teal"
                variant="outline"
                onClick={() => router.push('/register')}
                px={8}
                py={6}
                fontSize="lg"
                fontWeight="600"
                _hover={{
                  transform: 'translateY(-2px)',
                  boxShadow: 'lg',
                }}
                transition="all 0.2s"
              >
                Sign Up Now
              </Button>
            </Stack>

            
          </Stack>
        </Box>

        {/* Right Side: Image */}
        <Box flex="1" display="flex" justifyContent={{ base: 'center', lg: 'flex-end' }} alignItems="center">
                     <Box
             position="relative"
             maxW="500px"
             w="full"
             h="600px"
             borderRadius="2xl"
             overflow="hidden"
           >
            <Image
              src="/life_style_working_51.jpg"
              alt="Modern workspace with CoreTrack"
              w="full"
              h="full"
              objectFit="cover"
              fallbackSrc="/default-product.jpg"
            />
            {/* Overlay gradient for better text readability */}
            <Box
              position="absolute"
              top="0"
              left="0"
              right="0"
              bottom="0"
              bgGradient="linear(to-b, transparent, rgba(0,0,0,0.1))"
              pointerEvents="none"
            />
          </Box>
        </Box>
      </Flex>
    </Container>
  )
}