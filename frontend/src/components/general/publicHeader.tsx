'use client'

import { Box, Flex, Image } from '@chakra-ui/react'
import Link from 'next/link'

export default function Header() {
  return (
    <Box as="header" w="100%" py={4} px={8} bg="white" boxShadow="sm">
      <Flex align="center">
        <Link href="/">
          <Image src="/coretrack.png" alt="Logo" height="40px" />
        </Link>
      </Flex>
    </Box>
  )
}