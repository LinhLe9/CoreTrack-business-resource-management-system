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
} from '@chakra-ui/react';
import { productionTicketService, productionTicketUtils } from '@/services/productionTicketService';
import { ProductionTicketDetailResponse, ProductionTicketDetailStatus } from '@/types/productionTicket';
import { useUser } from '@/hooks/useUser';

const ProductionTicketDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const ticketId = params?.id;
  const detailId = params?.detailId;
  const toast = useToast();
  const { isOwner, isProductionStaff, isWarehouseStaff } = useUser();
  
  const [detail, setDetail] = useState<ProductionTicketDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [updating, setUpdating] = useState<boolean>(false);
  const [statusTransitionRules, setStatusTransitionRules] = useState<any[]>([]);
  const [selectedNewStatus, setSelectedNewStatus] = useState<string>('');
  const [note, setNote] = useState<string>('');
  const [cancelReason, setCancelReason] = useState<string>('');
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { isOpen: isCancelOpen, onOpen: onCancelOpen, onClose: onCancelClose } = useDisclosure();

  const refreshDetail = useCallback(async () => {
    if (!ticketId || !detailId) return;
    setLoading(true);
    try {
      const data = await productionTicketService.getProductionTicketDetails(Number(ticketId), Number(detailId));
      setDetail(data);
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
  }, [ticketId, detailId, toast]);

  useEffect(() => {
    if (!ticketId || !detailId) return;
    refreshDetail();
  }, [ticketId, detailId, refreshDetail]);

  // Load status transition rules
  const loadStatusTransitionRules = useCallback(async () => {
    try {
      const rules = await productionTicketService.getStatusTransitionRules();
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

  // Handle status update
  const handleStatusUpdate = useCallback(async () => {
    if (!selectedNewStatus || !ticketId || !detailId) return;
    
    setUpdating(true);
    try {
      await productionTicketService.updateDetailStatus(
        Number(ticketId), 
        Number(detailId), 
        { newStatus: selectedNewStatus as ProductionTicketDetailStatus, note: note.trim() || undefined }
      );
      
      toast({
        title: 'Success',
        description: `Status updated to ${selectedNewStatus}`,
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
      
      // Refresh detail data
      await refreshDetail();
      onClose();
      setSelectedNewStatus('');
      setNote('');
    } catch (err: any) {
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to update status',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setUpdating(false);
    }
  }, [selectedNewStatus, ticketId, detailId, note, toast, refreshDetail, onClose]);

  // Open status update modal
  const openStatusUpdateModal = useCallback((newStatus: string) => {
    setSelectedNewStatus(newStatus);
    setNote('');
    onOpen();
  }, [onOpen]);

  // Handle cancel ticket detail
  const handleCancelTicket = useCallback(async (reason: string) => {
    if (!ticketId || !detailId) return;
    
    setUpdating(true);
    try {
      await productionTicketService.cancelProductionTicketDetail(
        Number(ticketId), 
        Number(detailId), 
        reason
      );
      
      toast({
        title: 'Success',
        description: 'Production ticket detail cancelled successfully',
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
      setUpdating(false);
    }
  }, [ticketId, detailId, toast, refreshDetail, onCancelClose]);

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
          {error || 'Production ticket detail not found.'}
        </Text>
      </Center>
    );
  }

  return (
    <Box maxW="1200px" mx="auto" p={6}>
      {/* Header */}
      <Flex justify="space-between" align="center" mb={6}>
        <Heading as="h2" size="xl">
          Production Ticket Detail #{detail.id}
        </Heading>
        <Badge colorScheme={getStatusColor(detail.status)} size="lg" fontSize="md">
          {productionTicketUtils.getStatusBadgeText(detail.status)}
        </Badge>
      </Flex>

      {/* Detail Information */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Detail Information
        </Heading>
        <VStack align="start" spacing={2} p={4} border="1px" borderColor="gray.200" borderRadius="md">
          <Text><strong>ID:</strong> {detail.id}</Text>
          <Text><strong>Product Variant SKU:</strong> {detail.productVariantSku}</Text>
          <Text><strong>Status:</strong> 
            <Badge colorScheme={getStatusColor(detail.status)} ml={2}>
              {productionTicketUtils.getStatusBadgeText(detail.status)}
            </Badge>
          </Text>
          <Text><strong>Quantity:</strong> {detail.quantity}</Text>
          <Text><strong>Expected Complete Date:</strong> {formatDate(detail.expected_complete_date)}</Text>
          {detail.completed_date && (
            <Text><strong>Completed Date:</strong> {formatDate(detail.completed_date)}</Text>
          )}
          <Text><strong>Created:</strong> {formatDate(detail.createdAt)} by {detail.createdBy_name || 'N/A'} ({detail.createdBy_role || 'N/A'})</Text>
          <Text><strong>Updated:</strong> {formatDate(detail.lastUpdatedAt)} by {detail.lastUpdatedAt_name || 'N/A'} ({detail.lastUpdatedAt_role || 'N/A'})</Text>
        </VStack>
      </Box>

      <Divider my={6} />

      {/* BOM Items Section */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          BOM Items
        </Heading>
        {detail.boms && detail.boms.length > 0 ? (
          <TableContainer>
            <Table variant="simple" size="sm">
              <Thead>
                <Tr>
                  <Th>Material Variant SKU</Th>
                  <Th>Planned Quantity</Th>
                  <Th>Actual Quantity</Th>
                </Tr>
              </Thead>
              <Tbody>
                {detail.boms.map((bomItem) => (
                  <Tr key={bomItem.id}>
                    <Td>
                      <Text fontWeight="medium">{bomItem.materialVariantSku}</Text>
                    </Td>
                    <Td>
                      <Badge colorScheme="blue" variant="subtle">
                        {bomItem.plannedQuantity}
                      </Badge>
                    </Td>
                    <Td>
                      <Badge colorScheme="green" variant="subtle">
                        {bomItem.actualQuantity}
                      </Badge>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        ) : (
          <Alert status="info">
            <AlertIcon />
            No BOM items found for this detail.
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
            No status logs found for this production ticket detail.
          </Alert>
        )}
      </Box>

      {/* Action Buttons */}
      <Box textAlign="center">
        <VStack spacing={4}>
          {/* Status Update and Cancel Buttons - Side by Side */}
          {(isOwner() || isProductionStaff() || isWarehouseStaff()) && (
            <HStack spacing={4} justify="center">
              {/* Status Update Button */}
              {detail && (() => {
                const nextStatus = getNextStatus(detail.status);
                if (!nextStatus) return null;
                
                const getButtonText = (status: string) => {
                  switch (status) {
                    case 'APPROVAL': return 'Approve';
                    case 'COMPLETE': return 'Complete';
                    case 'READY': return 'Make Ready';
                    case 'CLOSED': return 'Close';
                    default: return `Change to ${status}`;
                  }
                };
                
                const getButtonColor = (status: string) => {
                  switch (status) {
                    case 'APPROVAL': return 'purple';
                    case 'COMPLETE': return 'green';
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
                    isLoading={updating}
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
                isLoading={updating}
                loadingText="Cancelling..."
                isDisabled={detail.status === 'CANCELLED' || detail.status === 'Cancelled'}
              >
                Cancel Ticket
              </Button>
            </HStack>
          )}
          
          {/* Back to Production Ticket Button - Below */}
          <Button 
            variant="outline" 
            size="lg"
            onClick={() => router.push(`/production/${ticketId}`)}
          >
            Back to Production Ticket
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
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
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
              onClick={handleStatusUpdate}
              isLoading={updating}
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
          <ModalHeader>Cancel Production Ticket Detail</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={4}>
              <Text>
                Are you sure you want to cancel this production ticket detail?
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
              isLoading={updating}
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

export default ProductionTicketDetailPage; 