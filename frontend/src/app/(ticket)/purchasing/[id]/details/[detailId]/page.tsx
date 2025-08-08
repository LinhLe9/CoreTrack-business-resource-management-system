"use client";

import { useRouter } from 'next/navigation';
import { useParams } from 'next/navigation';
import { useEffect, useState, useCallback } from 'react';
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
  Textarea,
  Card,
  CardBody,
  SimpleGrid,
} from '@chakra-ui/react';
import { purchasingTicketService } from '@/services/purchasingTicketService';
import { PurchasingTicketDetailResponse } from '@/types/purchasingTicket';
import { MaterialSupplierResponse } from '@/types/material';
import { useUser } from '@/hooks/useUser';

const PurchasingTicketDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const ticketId = params?.id;
  const detailId = params?.detailId;
  const toast = useToast();
  const { isOwner, isWarehouseStaff } = useUser();
  
  const [detail, setDetail] = useState<PurchasingTicketDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [updatingStatus, setUpdatingStatus] = useState<boolean>(false);
  const [statusTransitionRules, setStatusTransitionRules] = useState<any[]>([]);
  const [selectedNewStatus, setSelectedNewStatus] = useState<string>('');
  const [statusNote, setStatusNote] = useState<string>('');
  const [cancelReason, setCancelReason] = useState<string>('');
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { isOpen: isCancelOpen, onOpen: onCancelOpen, onClose: onCancelClose } = useDisclosure();

  const refreshDetail = useCallback(async () => {
    if (!ticketId || !detailId) return;
    setLoading(true);
    try {
      const data = await purchasingTicketService.getPurchasingTicketDetails(Number(ticketId), Number(detailId));
      setDetail(data);
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
  }, [ticketId, detailId, toast]);

  useEffect(() => {
    if (!ticketId || !detailId) return;
    refreshDetail();
  }, [ticketId, detailId, refreshDetail]);

  // Load status transition rules
  const loadStatusTransitionRules = useCallback(async () => {
    try {
      const rules = await purchasingTicketService.getStatusTransitionRules();
      setStatusTransitionRules(rules);
    } catch (err) {
      console.error('Failed to load status transition rules:', err);
    }
  }, []);

  useEffect(() => {
    loadStatusTransitionRules();
  }, [loadStatusTransitionRules]);

  // Get next available status for current status
  const getNextStatus = useCallback((currentStatus: string) => {
    console.log('Current status:', currentStatus);
    console.log('Available rules:', statusTransitionRules);
    // Convert to uppercase for case-insensitive matching
    const upperCurrentStatus = currentStatus.toUpperCase();
    const rule = statusTransitionRules.find(r => r.currentStatus === upperCurrentStatus);
    console.log('Found rule:', rule);
    const nextStatus = rule?.allowedTransitions?.[0] || null;
    console.log('Next status:', nextStatus);
    return nextStatus;
  }, [statusTransitionRules]);

  const handleUpdateStatus = useCallback(async () => {
    if (!selectedNewStatus || !ticketId || !detailId) return;
    
    setUpdatingStatus(true);
    try {
      await purchasingTicketService.updateDetailStatus(
        Number(ticketId), 
        Number(detailId), 
        {
          newStatus: selectedNewStatus as any, // Cast to enum
          note: statusNote.trim() || undefined
        }
      );
      toast({
        title: 'Success',
        description: `Status updated to ${selectedNewStatus}`,
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      // Refresh the detail data to show updated status
      await refreshDetail();
      onClose();
      setSelectedNewStatus('');
      setStatusNote('');
    } catch (err: any) {
      console.error('Error updating status:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to update status.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setUpdatingStatus(false);
    }
  }, [selectedNewStatus, ticketId, detailId, statusNote, toast, refreshDetail, onClose]);

  // Open status update modal
  const openStatusUpdateModal = useCallback((newStatus: string) => {
    setSelectedNewStatus(newStatus);
    setStatusNote('');
    onOpen();
  }, [onOpen]);

  // Handle cancel ticket detail
  const handleCancelTicket = useCallback(async (reason: string) => {
    if (!ticketId || !detailId) return;
    
    setUpdatingStatus(true);
    try {
      await purchasingTicketService.cancelPurchasingTicketDetail(
        Number(ticketId), 
        Number(detailId), 
        reason
      );
      
      toast({
        title: 'Success',
        description: 'Purchasing ticket detail cancelled successfully',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
      
      // Refresh detail data
      await refreshDetail();
      onCancelClose();
      setCancelReason('');
    } catch (err: any) {
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to cancel ticket detail',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setUpdatingStatus(false);
    }
  }, [ticketId, detailId, toast, refreshDetail, onCancelClose]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'NEW':
        return 'blue';
      case 'APPROVAL':
        return 'yellow';
      case 'SUCCESSFUL':
        return 'green';
      case 'SHIPPING':
        return 'cyan';
      case 'READY':
        return 'green';
      case 'CLOSED':
        return 'gray';
      case 'CANCELLED':
        return 'red';
      default:
        return 'blue';
    }
  };

  const formatDate = (dateString: string) => {
    if (!dateString || dateString.trim() === '') {
      return 'N/A';
    }
    
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return 'Invalid Date';
      }
      
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      console.error('Error formatting date:', dateString, error);
      return 'Invalid Date';
    }
  };

  if (loading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  if (error || !detail) {
    return (
      <Center minHeight="300px">
        <Text color="red.500" fontSize="lg">
          {error || 'Purchasing ticket detail not found.'}
        </Text>
      </Center>
    );
  }

  return (
    <Box maxW="1200px" mx="auto" p={6}>
      {/* Header */}
      <Flex justify="space-between" align="center" mb={6}>
        <Heading as="h2" size="xl">
          Purchasing Ticket Detail
        </Heading>
        <Badge colorScheme={getStatusColor(detail.status)} size="lg" fontSize="md">
          {purchasingTicketService.getStatusBadgeText(detail.status)}
        </Badge>
      </Flex>

      {/* Detail Information */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Detail Information
        </Heading>
        <VStack align="start" spacing={3} p={4} border="1px" borderColor="gray.200" borderRadius="md">
          <HStack spacing={8} wrap="wrap">
            <Text><strong>ID:</strong> {detail.id}</Text>
            <Text><strong>Material Variant SKU:</strong> {detail.materialVariantSku}</Text>
            <Text><strong>Quantity:</strong> {detail.quantity}</Text>
            <Text><strong>Status:</strong> 
              <Badge colorScheme={getStatusColor(detail.status)} ml={2}>
                {purchasingTicketService.getStatusBadgeText(detail.status)}
              </Badge>
            </Text>
          </HStack>
          <HStack spacing={8} wrap="wrap">
            <Text><strong>Expected Ready Date:</strong> {formatDate(detail.expected_ready_date)}</Text>
            {detail.ready_date && (
              <Text><strong>Ready Date:</strong> {formatDate(detail.ready_date)}</Text>
            )}
          </HStack>
          <HStack spacing={8} wrap="wrap">
            <Text><strong>Created:</strong> {formatDate(detail.createdAt)} by {detail.createdBy_name} ({detail.createdBy_role})</Text>
            <Text><strong>Updated:</strong> {formatDate(detail.lastUpdatedAt)} by {detail.lastUpdatedAt_name} ({detail.lastUpdatedAt_role})</Text>
          </HStack>
        </VStack>
      </Box>

      <Divider my={6} />

      {/* Material Suppliers Section */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Material Suppliers ({detail.materialSuppliers?.length || 0})
        </Heading>
        {detail.materialSuppliers && detail.materialSuppliers.length > 0 ? (
          <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} spacing={4}>
            {detail.materialSuppliers.map((supplier, index) => (
              <Card key={index} variant="outline">
                <CardBody>
                  <VStack spacing={2} align="start">
                    <Text fontWeight="bold" fontSize="lg">
                      {supplier.supplierName}
                    </Text>
                    <HStack spacing={4} wrap="wrap">
                      <Text fontSize="sm">
                        <strong>Price:</strong> {supplier.price} {supplier.currency}
                      </Text>
                      <Text fontSize="sm">
                        <strong>Lead Time:</strong> {supplier.leadTimeDays} days
                      </Text>
                    </HStack>
                    <HStack spacing={4} wrap="wrap">
                      <Text fontSize="sm">
                        <strong>Min Order:</strong> {supplier.minOrderQuantity}
                      </Text>
                      {supplier.supplierMaterialCode && (
                        <Text fontSize="sm">
                          <strong>Code:</strong> {supplier.supplierMaterialCode}
                        </Text>
                      )}
                    </HStack>
                  </VStack>
                </CardBody>
              </Card>
            ))}
          </SimpleGrid>
        ) : (
          <Alert status="info">
            <AlertIcon />
            No material suppliers found for this detail.
          </Alert>
        )}
      </Box>

      <Divider my={6} />

      {/* Status Logs Section */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Status Logs
        </Heading>
        {detail.logs && detail.logs.length > 0 ? (
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
                {detail.logs
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
            No status logs found for this detail.
          </Alert>
        )}
      </Box>

      {/* Action Buttons */}
      <Box textAlign="center">
        <VStack spacing={4}>
          {/* Status Update and Cancel Buttons - Side by Side */}
          {(isOwner() || isWarehouseStaff()) && (
            <HStack spacing={4} justify="center">
              {/* Status Update Button */}
              {detail && (() => {
                const nextStatus = getNextStatus(detail.status);
                if (!nextStatus) return null;
                
                const getButtonText = (status: string) => {
                  switch (status) {
                    case 'APPROVAL': return 'Approve';
                    case 'SUCCESSFUL': return 'Mark Successful';
                    case 'SHIPPING': return 'Start Shipping';
                    case 'READY': return 'Make Ready';
                    case 'CLOSED': return 'Close';
                    default: return `Change to ${status}`;
                  }
                };
                
                const getButtonColor = (status: string) => {
                  switch (status) {
                    case 'APPROVAL': return 'purple';
                    case 'SUCCESSFUL': return 'green';
                    case 'SHIPPING': return 'cyan';
                    case 'READY': return 'blue';
                    case 'CLOSED': return 'gray';
                    default: return 'blue';
                  }
                };
                
                return (
                  <Button
                    colorScheme={getButtonColor(nextStatus)}
                    size="lg"
                    onClick={() => openStatusUpdateModal(nextStatus)}
                    isLoading={updatingStatus}
                    loadingText="Updating..."
                  >
                    {getButtonText(nextStatus)}
                  </Button>
                );
              })()}
              
              {/* Cancel Ticket Button */}
              <Button 
                colorScheme="red" 
                size="lg"
                onClick={() => {
                  setCancelReason('');
                  onCancelOpen();
                }}
                isLoading={updatingStatus}
                loadingText="Cancelling..."
                isDisabled={detail.status === 'CANCELLED' || detail.status === 'Cancelled'}
              >
                Cancel Ticket
              </Button>
            </HStack>
          )}
          
          {/* Back to Purchasing Ticket Button - Below */}
          <Button 
            variant="outline" 
            size="lg"
            onClick={() => router.push(`/purchasing/${ticketId}`)}
          >
            Back to Purchasing Ticket
          </Button>
        </VStack>
      </Box>

      {/* Status Update Modal */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Update Status</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={4}>
              <Text>
                Are you sure you want to change the status from{' '}
                <Badge colorScheme={getStatusColor(detail?.status || '')}>
                  {detail?.status}
                </Badge>{' '}
                to{' '}
                <Badge colorScheme={getStatusColor(selectedNewStatus)}>
                  {selectedNewStatus}
                </Badge>?
              </Text>
              <FormControl>
                <FormLabel>Note (Optional)</FormLabel>
                <Textarea
                  value={statusNote}
                  onChange={(e) => setStatusNote(e.target.value)}
                  placeholder="Enter a note for this status change..."
                  rows={3}
                />
              </FormControl>
            </VStack>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button 
              colorScheme="blue" 
              onClick={handleUpdateStatus}
              isLoading={updatingStatus}
              loadingText="Updating..."
            >
              Confirm Update
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Cancel Ticket Modal */}
      <Modal isOpen={isCancelOpen} onClose={onCancelClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Cancel Purchasing Ticket Detail</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={4}>
              <Text>
                Are you sure you want to cancel this purchasing ticket detail?
              </Text>
              <FormControl>
                <FormLabel>Reason for Cancellation</FormLabel>
                <Textarea
                  value={cancelReason}
                  onChange={(e) => setCancelReason(e.target.value)}
                  placeholder="Enter a reason for cancellation..."
                  rows={3}
                  isRequired
                />
              </FormControl>
            </VStack>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onCancelClose}>
              Cancel
            </Button>
            <Button 
              colorScheme="red" 
              onClick={() => {
                if (!cancelReason.trim()) {
                  toast({
                    title: 'Error',
                    description: 'Please enter a reason for cancellation',
                    status: 'error',
                    duration: 3000,
                    isClosable: true,
                  });
                  return;
                }
                handleCancelTicket(cancelReason);
              }}
              isLoading={updatingStatus}
              loadingText="Cancelling..."
              isDisabled={!cancelReason.trim()}
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