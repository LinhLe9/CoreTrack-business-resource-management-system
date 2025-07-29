'use client';

import React from 'react';
import {
  Box,
  Card,
  CardBody,
  Flex,
  Text,
  Badge,
  VStack,
  HStack,
  Icon,
} from '@chakra-ui/react';
import { FaBuilding, FaUser, FaEnvelope, FaPhone, FaMapMarkerAlt, FaGlobe } from 'react-icons/fa';
import { Supplier } from '../../types/supplier';

interface SupplierCardProps {
  supplier: Supplier;
}

const SupplierCard: React.FC<SupplierCardProps> = ({ supplier }) => {
  const getStatusColor = (status: string) => {
    return status === 'Active' ? 'green' : 'gray';
  };

  return (
    <Card
      direction={{ base: 'column', lg: 'row' }}
      overflow="hidden"
      variant="outline"
      _hover={{ shadow: 'md', transform: 'translateY(-2px)' }}
      transition="all 0.2s"
      cursor="pointer"
    >
      <CardBody p={6}>
        <Flex direction={{ base: 'column', lg: 'row' }} gap={4} width="100%">
          {/* Left side - All information */}
          <Box flex="1">
            <VStack align="start" spacing={3}>
              {/* Company Name */}
              <HStack spacing={2}>
                <Icon as={FaBuilding} color="blue.500" />
                <Text fontWeight="bold" fontSize="lg" color="gray.800">
                  {supplier.name}
                </Text>
              </HStack>

              {/* Contact Person */}
              {supplier.contactPerson && (
                <HStack spacing={2}>
                  <Icon as={FaUser} color="gray.500" />
                  <Text fontSize="sm" color="gray.600">
                    {supplier.contactPerson}
                  </Text>
                </HStack>
              )}

              {/* Email */}
              {supplier.email && (
                <HStack spacing={2}>
                  <Icon as={FaEnvelope} color="gray.500" />
                  <Text fontSize="sm" color="gray.600">
                    {supplier.email}
                  </Text>
                </HStack>
              )}

              {/* Phone */}
              {supplier.phone && (
                <HStack spacing={2}>
                  <Icon as={FaPhone} color="gray.500" />
                  <Text fontSize="sm" color="gray.600">
                    {supplier.phone}
                  </Text>
                </HStack>
              )}

              {/* Address */}
              {supplier.address && (
                <HStack spacing={2}>
                  <Icon as={FaMapMarkerAlt} color="gray.500" />
                  <Text fontSize="sm" color="gray.600">
                    {supplier.address}
                  </Text>
                </HStack>
              )}

              {/* Country */}
              {supplier.country && (
                <HStack spacing={2}>
                  <Icon as={FaGlobe} color="gray.500" />
                  <Text fontSize="sm" color="gray.600">
                    {supplier.country}
                  </Text>
                </HStack>
              )}

              {/* Description */}
              {supplier.description && (
                <Text fontSize="sm" color="gray.600" mt={2}>
                  {supplier.description}
                </Text>
              )}
            </VStack>
          </Box>

          {/* Right side - Status */}
          <Box display="flex" alignItems="center" justifyContent={{ base: 'flex-start', lg: 'center' }}>
            <Badge
              colorScheme={getStatusColor(supplier.status)}
              fontSize="sm"
              px={3}
              py={1}
              borderRadius="full"
            >
              {supplier.status}
            </Badge>
          </Box>
        </Flex>
      </CardBody>
    </Card>
  );
};

export default SupplierCard;
