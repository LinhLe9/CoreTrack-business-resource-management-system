'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Box, Container, Heading, Text, Spinner, VStack } from '@chakra-ui/react';

interface Supplier {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  isActive: boolean;
}

export default function SupplierDetailPage() {
  const params = useParams();
  const [supplier, setSupplier] = useState<Supplier | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSupplier = async () => {
      try {
        // TODO: Implement actual API call when backend endpoint is available
        setLoading(false);
        setError('Supplier detail endpoint not implemented yet');
      } catch (err) {
        setLoading(false);
        setError('Failed to load supplier details');
      }
    };

    if (params.id) {
      fetchSupplier();
    }
  }, [params.id]);

  if (loading) {
    return (
      <Container maxW="container.xl" py={8}>
        <Box display="flex" justifyContent="center" alignItems="center" minH="400px">
          <Spinner size="xl" />
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxW="container.xl" py={8}>
        <VStack spacing={4} align="center">
          <Heading size="lg">Supplier Details</Heading>
          <Text color="red.500">{error}</Text>
        </VStack>
      </Container>
    );
  }

  return (
    <Container maxW="container.xl" py={8}>
      <VStack spacing={6} align="stretch">
        <Heading size="lg">Supplier Details</Heading>
        <Text>Supplier ID: {params.id}</Text>
        <Text>This page is under development.</Text>
      </VStack>
    </Container>
  );
}
