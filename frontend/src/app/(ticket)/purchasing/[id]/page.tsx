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
import { purchasingTicketService } from '@/services/purchasingTicketService';
import { PurchasingTicketResponse } from '@/types/purchasingTicket';
import { useUser } from '@/hooks/useUser';

const PurchasingTicketDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const id = params?.id;
  const toast = useToast();
  const { isOwner, isWarehouseStaff } = useUser();
  
  const [ticket, setTicket] = useState<PurchasingTicketResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [cancelling, setCancelling] = useState<boolean>(false);
  const [cancelReason, setCancelReason] = useState<string>('');
  const { isOpen, onOpen, onClose } = useDisclosure();

  const refreshTicket = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await purchasingTicketService.getPurchasingTicketById(Number(id));
      setTicket(data);
    } catch (err: any) {
      setError('Failed to load purchasing ticket detail.');
      console.error(err);
      toast({
        title: 'Error',
        description: 'Failed to load purchasing ticket detail.',
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
      case 'NEW':
        return 'blue';
      case 'PARTIAL_APPROVAL':
        return 'yellow';
      case 'APPROVAL':
        return 'purple';
      case 'PARTIAL_SUCCESSFUL':
        return 'orange';
      case 'SUCCESSFUL':
        return 'green';
      case 'PARTIAL_SHIPPING':
        return 'orange';
      case 'SHIPPING':
        return 'cyan';
      case 'PARTIAL_READY':
        return 'orange';
      case 'READY':
        return 'green';
      case 'CLOSED':
        return 'gray';
      case 'PARTIAL_CANCELLED':
        return 'red';
      case 'CANCELLED':
        return 'red';
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
      await purchasingTicketService.cancelPurchasingTicket(Number(id), cancelReason.trim());
      toast({
        title: 'Success',
        description: 'Purchasing ticket cancelled successfully.',
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
        description: err.response?.data?.message || 'Failed to cancel purchasing ticket.',
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
          {error || 'Purchasing ticket not found.'}
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
          {purchasingTicketService.getStatusBadgeText(ticket.status)}
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
                {purchasingTicketService.getStatusBadgeText(ticket.status)}
              </Badge>
            </Text>
            {ticket.completed_date && (
              <Text><strong>Completed Date:</strong> {formatDate(ticket.completed_date)}</Text>
            )}
          </HStack>
          <HStack spacing={8} wrap="wrap">
            <Text><strong>Created:</strong> {formatDate(ticket.createdAt)} by {ticket.createdBy} ({ticket.createdByRole})</Text>
            <Text><strong>Updated:</strong> {formatDate(ticket.lastUpdatedAt)} by {ticket.lastUpdateBy} ({ticket.lastUpdateByRole})</Text>
          </HStack>
        </VStack>
      </Box>

      <Divider my={6} />

      {/* Purchasing Ticket Details */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Purchasing Ticket Details
        </Heading>
        {ticket.detail && ticket.detail.length > 0 ? (
          <TableContainer>
            <Table variant="simple" size="sm">
              <Thead>
                <Tr>
                  <Th>Material Variant SKU</Th>
                  <Th>Quantity</Th>
                  <Th>Status</Th>
                  <Th>Expected Ready Date</Th>
                  <Th>Ready Date</Th>
                  <Th>Created</Th>
                  <Th>Updated</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {ticket.detail.map((detail) => (
                  <Tr key={detail.id}>
                    <Td>
                      <Text fontWeight="medium">{detail.materialVariantSku}</Text>
                    </Td>
                    <Td>
                      <Badge colorScheme="blue" variant="subtle">
                        {detail.quantity}
                      </Badge>
                    </Td>
                    <Td>
                      <Badge colorScheme={getStatusColor(detail.status)}>
                        {purchasingTicketService.getStatusBadgeText(detail.status)}
                      </Badge>
                    </Td>
                    <Td>
                      <Text fontSize="sm">
                        {formatDate(detail.expected_ready_date)}
                      </Text>
                    </Td>
                    <Td>
                      {detail.ready_date ? (
                        <Text fontSize="sm" color="green.600">
                          {formatDate(detail.ready_date)}
                        </Text>
                      ) : (
                        <Text fontSize="sm" color="gray.500">
                          Not ready
                        </Text>
                      )}
                    </Td>
                    <Td>
                      <VStack align="start" spacing={0}>
                        <Text fontSize="xs">{formatDate(detail.createdAt)}</Text>
                        <Text fontSize="xs" color="gray.500">
                          by {detail.createdBy} ({detail.createdByRole})
                        </Text>
                      </VStack>
                    </Td>
                    <Td>
                      <VStack align="start" spacing={0}>
                        <Text fontSize="xs">{formatDate(detail.updatedAt)}</Text>
                        <Text fontSize="xs" color="gray.500">
                          by {detail.updatedBy} ({detail.updatedByRole})
                        </Text>
                      </VStack>
                    </Td>
                    <Td>
                      <Button
                        size="sm"
                        colorScheme="blue"
                        variant="outline"
                        onClick={() => router.push(`/purchasing/${ticket.id}/details/${detail.id}`)}
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
            No purchasing ticket details found.
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
            No status logs found for this purchasing ticket.
          </Alert>
        )}
      </Box>

      {/* Action Buttons */}
      <Box textAlign="center">
        <HStack spacing={4} justify="center">
          {ticket.status !== 'CANCELLED' && ticket.status !== 'Cancelled' && (isOwner() || isWarehouseStaff()) && (
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
            onClick={() => router.push('/purchasing')}
          >
            Back to Purchasing Tickets
          </Button>
        </HStack>
      </Box>

      {/* Cancel Ticket Modal */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Cancel Purchasing Ticket</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl>
              <FormLabel>Reason for cancellation</FormLabel>
              <Input
                value={cancelReason}
                onChange={(e) => setCancelReason(e.target.value)}
                placeholder="Enter reason for cancelling this purchasing ticket..."
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

export default PurchasingTicketDetailPage; 