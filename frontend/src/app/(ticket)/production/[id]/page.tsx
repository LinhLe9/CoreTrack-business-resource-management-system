"use client";

import { useRouter } from 'next/navigation';
import { useParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import {
  Box,
  Heading,
  Text,
  Spinner,
  Center,
  VStack,
  Divider,
  Badge,
  Button,
  Flex,
  HStack,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableContainer,
  Alert,
  AlertIcon,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  useDisclosure,
  FormControl,
  FormLabel,
  Input,
} from '@chakra-ui/react';
import { productionTicketService, productionTicketUtils } from '@/services/productionTicketService';
import { ProductionTicketResponse } from '@/types/productionTicket';
import { useUser } from '@/hooks/useUser';

const ProductionTicketDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const id = params?.id;
  const toast = useToast();
  const { isOwner, isProductionStaff } = useUser();
  
  const [ticket, setTicket] = useState<ProductionTicketResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [cancelling, setCancelling] = useState<boolean>(false);
  const [cancelReason, setCancelReason] = useState<string>('');
  const { isOpen, onOpen, onClose } = useDisclosure();

  const refreshTicket = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await productionTicketService.getProductionTicketById(Number(id));
      setTicket(data);
    } catch (err: any) {
      setError('Failed to load production ticket detail.');
      console.error(err);
      toast({
        title: 'Error',
        description: 'Failed to load production ticket detail.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!id) return;
    refreshTicket();
  }, [id]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'New':
        return 'blue';
      case 'In Progress':
        return 'yellow';
      case 'Partial Complete':
        return 'orange';
      case 'Complete':
        return 'green';
      case 'Partial Cancelled':
        return 'red';
      case 'Cancelled':
        return 'red';
      case 'Approved':
        return 'purple';
      case 'Ready':
        return 'green';
      case 'Closed':
        return 'gray';
      default:
        return 'blue';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const handleCancelTicket = async () => {
    if (!ticket || !id) return;
    
    if (!cancelReason.trim()) {
      toast({
        title: 'Error',
        description: 'Please enter a reason for cancelling.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
      return;
    }
    
    setCancelling(true);
    try {
      await productionTicketService.cancelProductionTicket(Number(id), cancelReason.trim());
      toast({
        title: 'Success',
        description: 'Production ticket cancelled successfully.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      // Refresh the ticket data to show updated status
      await refreshTicket();
      onClose();
      setCancelReason('');
    } catch (err: any) {
      console.error('Error cancelling ticket:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to cancel production ticket.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setCancelling(false);
    }
  };

  const openCancelModal = () => {
    setCancelReason('');
    onOpen();
  };

  if (loading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  if (error || !ticket) {
    return (
      <Center minHeight="300px">
        <Text color="red.500" fontSize="lg">
          {error || 'Production ticket not found.'}
        </Text>
      </Center>
    );
  }

  return (
    <Box maxW="1200px" mx="auto" p={6}>
      {/* Header */}
      <Flex justify="space-between" align="center" mb={6}>
        <Heading as="h2" size="xl">
          {ticket.name}
        </Heading>
        <Badge colorScheme={getStatusColor(ticket.status)} size="lg" fontSize="md">
          {productionTicketUtils.getStatusBadgeText(ticket.status)}
        </Badge>
      </Flex>

      {/* Ticket Information */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Ticket Information
        </Heading>
        <VStack align="start" spacing={3} p={4} border="1px" borderColor="gray.200" borderRadius="md">
          <HStack spacing={8} wrap="wrap">
            <Text><strong>ID:</strong> {ticket.id}</Text>
            <Text><strong>Status:</strong> 
              <Badge colorScheme={getStatusColor(ticket.status)} ml={2}>
                {productionTicketUtils.getStatusBadgeText(ticket.status)}
              </Badge>
            </Text>
            {ticket.completed_date && (
              <Text><strong>Completed Date:</strong> {formatDate(ticket.completed_date)}</Text>
            )}
          </HStack>
          <HStack spacing={8} wrap="wrap">
            <Text><strong>Created:</strong> {formatDate(ticket.createdAt)} by {ticket.createdBy || 'N/A'} ({ticket.createdByRole || 'N/A'})</Text>
            <Text><strong>Updated:</strong> {formatDate(ticket.updatedAt)} by {ticket.updatedBy || 'N/A'} ({ticket.updatedByRole || 'N/A'})</Text>
          </HStack>
        </VStack>
      </Box>

      <Divider my={6} />

      {/* Production Ticket Details */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Production Ticket Details
        </Heading>
        {ticket.detail && ticket.detail.length > 0 ? (
          <TableContainer>
            <Table variant="simple" size="sm">
              <Thead>
                <Tr>
                  <Th>Product Variant SKU</Th>
                  <Th>Quantity</Th>
                  <Th>Status</Th>
                  <Th>Expected Complete Date</Th>
                  <Th>Completed Date</Th>
                  <Th>Created</Th>
                  <Th>Updated</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {ticket.detail.map((detail) => (
                  <Tr key={detail.id}>
                    <Td>
                      <Text fontWeight="medium">{detail.productVariantSku}</Text>
                    </Td>
                    <Td>
                      <Badge colorScheme="blue" variant="subtle">
                        {detail.quantity}
                      </Badge>
                    </Td>
                    <Td>
                      <Badge colorScheme={getStatusColor(detail.status)}>
                        {productionTicketUtils.getStatusBadgeText(detail.status)}
                      </Badge>
                    </Td>
                    <Td>
                      <Text fontSize="sm">
                        {formatDate(detail.expected_complete_date)}
                      </Text>
                    </Td>
                    <Td>
                      {detail.completed_date ? (
                        <Text fontSize="sm" color="green.600">
                          {formatDate(detail.completed_date)}
                        </Text>
                      ) : (
                        <Text fontSize="sm" color="gray.500">
                          Not completed
                        </Text>
                      )}
                    </Td>
                    <Td>
                      <VStack align="start" spacing={0}>
                        <Text fontSize="xs">{formatDate(detail.createdAt)}</Text>
                        <Text fontSize="xs" color="gray.500">
                          by {detail.createdBy || 'N/A'} ({detail.createdByRole || 'N/A'})
                        </Text>
                      </VStack>
                    </Td>
                    <Td>
                      <VStack align="start" spacing={0}>
                        <Text fontSize="xs">{formatDate(detail.updatedAt)}</Text>
                        <Text fontSize="xs" color="gray.500">
                          by {detail.updatedBy || 'N/A'} ({detail.updatedByRole || 'N/A'})
                        </Text>
                      </VStack>
                    </Td>
                    <Td>
                      <Button
                        size="sm"
                        colorScheme="blue"
                        variant="outline"
                        onClick={() => router.push(`/production/${ticket.id}/details/${detail.id}`)}
                      >
                        View Detail
                      </Button>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        ) : (
          <Alert status="info">
            <AlertIcon />
            No production ticket details found.
          </Alert>
        )}
      </Box>

      <Divider my={6} />

      {/* Status Logs Section */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Status Logs
        </Heading>
        {ticket.logs && ticket.logs.length > 0 ? (
          <TableContainer>
            <Table variant="simple" size="sm">
              <Thead>
                <Tr>
                  <Th>Status Change</Th>
                  <Th>Note</Th>
                  <Th>Updated At</Th>
                  <Th>Updated By</Th>
                </Tr>
              </Thead>
              <Tbody>
                {ticket.logs
                  .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
                  .map((log) => (
                  <Tr key={log.id}>
                    <Td>
                      <VStack align="start" spacing={1}>
                        <HStack spacing={2}>
                          {log.old_status && (
                            <>
                              <Badge colorScheme={getStatusColor(log.old_status)} variant="outline">
                                {log.old_status}
                              </Badge>
                              <Text fontSize="xs" color="gray.500">→</Text>
                            </>
                          )}
                          <Badge colorScheme={getStatusColor(log.new_status)}>
                            {log.new_status}
                          </Badge>
                        </HStack>
                      </VStack>
                    </Td>
                    <Td>
                      <Text fontSize="sm" maxW="200px" noOfLines={2}>
                        {log.note || 'No note provided'}
                      </Text>
                    </Td>
                    <Td>
                      <Text fontSize="sm">
                        {formatDate(log.updatedAt)}
                      </Text>
                    </Td>
                    <Td>
                      <VStack align="start" spacing={0}>
                        <Text fontSize="sm" fontWeight="medium">
                          {log.updatedByName}
                        </Text>
                        <Text fontSize="xs" color="gray.500">
                          {log.updatedByRole}
                        </Text>
                      </VStack>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        ) : (
          <Alert status="info">
            <AlertIcon />
            No status logs found for this production ticket.
          </Alert>
        )}
      </Box>

      {/* Action Buttons */}
      <Box textAlign="center">
        <HStack spacing={4} justify="center">
          {ticket.status !== 'CANCELLED' && ticket.status !== 'Cancelled' && (isOwner() || isProductionStaff()) && (
            <Button 
              colorScheme="red" 
              size="lg"
              onClick={openCancelModal}
              isLoading={cancelling}
              loadingText="Cancelling..."
            >
              Cancel Ticket
            </Button>
          )}
          <Button 
            variant="outline" 
            size="lg"
            onClick={() => router.push('/production')}
          >
            Back to Production Tickets
          </Button>
        </HStack>
      </Box>

      {/* Cancel Ticket Modal */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Cancel Production Ticket</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl>
              <FormLabel>Reason for cancellation</FormLabel>
              <Input
                value={cancelReason}
                onChange={(e) => setCancelReason(e.target.value)}
                placeholder="Enter reason for cancelling this production ticket..."
                size="md"
              />
            </FormControl>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button 
              colorScheme="red" 
              onClick={handleCancelTicket}
              isLoading={cancelling}
              loadingText="Cancelling..."
            >
              Confirm Cancel
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default ProductionTicketDetailPage; 