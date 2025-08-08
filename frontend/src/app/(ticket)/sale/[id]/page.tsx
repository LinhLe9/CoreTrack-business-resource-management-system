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
  Select,
} from '@chakra-ui/react';
import { 
  getSaleTicketById, 
  cancelSaleTicket, 
  allocateOrderDetail, 
  getStatusTransitionRules,
  updateSaleStatus
} from '@/services/saleService';
import { 
  SaleTicketResponse, 
  SaleDetailResponse, 
  SaleOrderStatusLogResponse, 
  SaleStatusTransitionRule,
  OrderStatus 
} from '@/types/sale';

const SaleDetailPage = () => {
  const router = useRouter();
  const params = useParams(); 
  const id = params?.id;
  const toast = useToast();
  
  const [sale, setSale] = useState<SaleTicketResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [cancelling, setCancelling] = useState<boolean>(false);
  const [allocating, setAllocating] = useState<boolean>(false);
  const [statusTransitionRules, setStatusTransitionRules] = useState<SaleStatusTransitionRule[]>([]);
  const [cancelReason, setCancelReason] = useState<string>('');
  const [statusNote, setStatusNote] = useState<string>('');
  const [newStatus, setNewStatus] = useState<OrderStatus | ''>('');
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { 
    isOpen: isStatusModalOpen, 
    onOpen: onStatusModalOpen, 
    onClose: onStatusModalClose 
  } = useDisclosure();

  const refreshSale = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await getSaleTicketById(Number(id));
      setSale(data);
    } catch (err: any) {
      setError('Failed to load sale detail.');
      console.error(err);
      toast({
        title: 'Error',
        description: 'Failed to load sale detail.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const loadStatusTransitionRules = async () => {
    try {
      const rules = await getStatusTransitionRules();
      setStatusTransitionRules(rules);
    } catch (err) {
      console.error('Error loading status transition rules:', err);
    }
  };

  useEffect(() => {
    if (!id) return;
    refreshSale();
    loadStatusTransitionRules();
  }, [id]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'NEW':
        return 'blue';
      case 'ALLOCATED':
        return 'yellow';
      case 'PACKED':
        return 'orange';
      case 'SHIPPED':
        return 'purple';
      case 'DONE':
        return 'green';
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

  const formatCurrency = (amount: string) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(parseFloat(amount));
  };

  const handleCancelSale = async () => {
    if (!sale || !id) return;
    
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
      await cancelSaleTicket(Number(id), cancelReason.trim());
      
      toast({
        title: 'Success',
        description: 'Sale cancelled successfully.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      onClose();
      setCancelReason('');
      // Redirect to sale page after successful cancellation
      router.push('/sale');
    } catch (err: any) {
      console.error('Error cancelling sale:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to cancel sale.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setCancelling(false);
    }
  };

  const handleAllocateDetail = async (detailId: number) => {
    if (!sale || !id) return;
    
    setAllocating(true);
    try {
      await allocateOrderDetail(Number(id), detailId);
      toast({
        title: 'Success',
        description: 'Order detail allocated successfully.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      await refreshSale();
    } catch (err: any) {
      console.error('Error allocating detail:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to allocate order detail.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setAllocating(false);
    }
  };



  const handleStatusChange = async () => {
    if (!sale || !id || !newStatus) return;
    
    try {
      await updateSaleStatus(Number(id), newStatus as OrderStatus, statusNote);
      
      toast({
        title: 'Success',
        description: 'Sale status updated successfully.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      await refreshSale();
      onStatusModalClose();
      setNewStatus('');
      setStatusNote('');
    } catch (err: any) {
      console.error('Error updating status:', err);
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to update sale status.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const getAvailableStatusTransitions = () => {
    if (!sale || !statusTransitionRules.length) {
      return [];
    }
    
    // Convert display name to enum name for comparison
    const statusMap: { [key: string]: string } = {
      'New': 'NEW',
      'Allocated': 'ALLOCATED', 
      'Packed': 'PACKED',
      'Shipped': 'SHIPPED',
      'Done': 'DONE',
      'Cancelled': 'CANCELLED'
    };
    
    const saleStatusEnum = statusMap[sale.status] || sale.status;
    
    const availableTransitions = statusTransitionRules.filter(rule => {
      const matches = rule.currentStatus === saleStatusEnum;
      return matches;
    });
    return availableTransitions;
  };



  const openCancelModal = () => {
    setCancelReason('');
    onOpen();
  };

  const openStatusModal = () => {
    setNewStatus('');
    setStatusNote('');
    onStatusModalOpen();
  };

  if (loading) {
    return (
      <Center minHeight="300px">
        <Spinner size="xl" />
      </Center>
    );
  }

  if (error || !sale) {
    return (
      <Center minHeight="300px">
        <Text color="red.500" fontSize="lg">
          {error || 'Sale not found.'}
        </Text>
      </Center>
    );
  }

  const availableStatusTransitions = getAvailableStatusTransitions();

  return (
    <Box maxW="1200px" mx="auto" p={6}>
      {/* Header */}
      <Flex justify="space-between" align="center" mb={6}>
        <Heading as="h2" size="xl">
          Sale Order #{sale.id}
        </Heading>
        <Badge colorScheme={getStatusColor(sale.status)} size="lg" fontSize="md">
          {sale.status}
        </Badge>
      </Flex>

      {/* Order Information */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Order Information
        </Heading>
        <VStack align="start" spacing={3} p={4} border="1px" borderColor="gray.200" borderRadius="md">
          <Text><strong>SKU:</strong> {sale.sku}</Text>
          <Text><strong>Status:</strong> 
            <Badge colorScheme={getStatusColor(sale.status)} ml={2}>
              {sale.status}
            </Badge>
          </Text>
          <Text><strong>Total:</strong> {formatCurrency(sale.total)}</Text>
          <Text><strong>Promotion:</strong> {formatCurrency(sale.promotion)}</Text>
          <Text><strong>Net Total:</strong> {formatCurrency(sale.netTotal)}</Text>
          <Text><strong>Expected Complete Date:</strong> {formatDate(sale.expected_complete_date)}</Text>
          {sale.completed_date && (
            <Text><strong>Completed Date:</strong> {formatDate(sale.completed_date)}</Text>
          )}
          <Text><strong>Customer Name:</strong> {sale.customerName || 'N/A'}</Text>
          <Text><strong>Customer Email:</strong> {sale.customerEmail || 'N/A'}</Text>
          <Text><strong>Customer Phone:</strong> {sale.customerPhone || 'N/A'}</Text>
          <Text><strong>Customer Address:</strong> {sale.customerAddress || 'N/A'}</Text>
          <Text><strong>Created:</strong> {formatDate(sale.createdAt)} by {sale.createdBy}</Text>
          <Text><strong>Updated:</strong> {formatDate(sale.updatedAt)} by {sale.updatedBy || 'N/A'}</Text>
        </VStack>
      </Box>

      <Divider my={6} />

      {/* Sale Details */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Sale Details
        </Heading>
        {sale.details && sale.details.length > 0 ? (
          <TableContainer>
            <Table variant="simple" size="sm">
              <Thead>
                <Tr>
                  <Th>Product Variant SKU</Th>
                  <Th>Product Name</Th>
                  <Th>Quantity</Th>
                  <Th>Unit Price</Th>
                  <Th>Total Price</Th>
                  <Th>Status</Th>
                  <Th>Current Stock</Th>
                  <Th>Allocated Stock</Th>
                  <Th>Future Stock</Th>
                  <Th>Available Stock</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {sale.details.map((detail) => (
                  <Tr key={detail.id}>
                    <Td>
                      <Text fontWeight="medium">{detail.productVariantSku}</Text>
                    </Td>
                    <Td>
                      <VStack align="start" spacing={0}>
                        <Text fontWeight="medium">{detail.productName}</Text>
                        <Text fontSize="sm" color="gray.600">{detail.productVariantName}</Text>
                      </VStack>
                    </Td>
                    <Td>
                      <Badge colorScheme="blue" variant="subtle">
                        {detail.quantity}
                      </Badge>
                    </Td>
                    <Td>
                      <Text fontSize="sm">
                        {formatCurrency(detail.unitPrice)}
                      </Text>
                    </Td>
                    <Td>
                      <Text fontSize="sm" fontWeight="medium">
                        {formatCurrency(detail.totalPrice)}
                      </Text>
                    </Td>
                    <Td>
                      <Badge colorScheme={getStatusColor(detail.status)}>
                        {detail.status}
                      </Badge>
                    </Td>
                    <Td>
                      <Text fontSize="sm" fontWeight="medium" color="blue.600">
                        {detail.currentStock || 0}
                      </Text>
                    </Td>
                    <Td>
                      <Text fontSize="sm" fontWeight="medium" color="orange.600">
                        {detail.allocatedStock || 0}
                      </Text>
                    </Td>
                    <Td>
                      <Text fontSize="sm" fontWeight="medium" color="purple.600">
                        {detail.futureStock || 0}
                      </Text>
                    </Td>
                    <Td>
                      <Text fontSize="sm" fontWeight="medium" color="green.600">
                        {detail.availableStock || 0}
                      </Text>
                    </Td>
                    <Td>
                      {detail.status === 'New' ? (
                        <Text
                          as="button"
                          color="blue.500"
                          textDecoration="underline"
                          cursor="pointer"
                          onClick={() => handleAllocateDetail(detail.id)}
                          _hover={{ color: 'blue.700' }}
                        >
                          Allocated
                        </Text>
                      ) : (
                        <Text fontSize="sm" color="gray.500">
                          -
                        </Text>
                      )}
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        ) : (
          <Alert status="info">
            <AlertIcon />
            No sale details found.
          </Alert>
        )}
      </Box>

      <Divider my={6} />

      {/* Status Logs Section */}
      <Box mb={8}>
        <Heading as="h3" size="md" mb={4}>
          Status Logs
        </Heading>
        {sale.statusLogs && sale.statusLogs.length > 0 ? (
          <TableContainer>
            <Table variant="simple" size="sm">
              <Thead>
                <Tr>
                  <Th>Status Change</Th>
                  <Th>Note</Th>
                  <Th>Created At</Th>
                  <Th>Created By</Th>
                </Tr>
              </Thead>
              <Tbody>
                {sale.statusLogs
                  .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                  .map((log) => (
                  <Tr key={log.id}>
                    <Td>
                      <VStack align="start" spacing={1}>
                        <HStack spacing={2}>
                          {log.fromStatus && (
                            <>
                              <Badge colorScheme={getStatusColor(log.fromStatus)} variant="outline">
                                {log.fromStatus}
                              </Badge>
                              <Text fontSize="xs" color="gray.500">→</Text>
                            </>
                          )}
                          <Badge colorScheme={getStatusColor(log.toStatus)}>
                            {log.toStatus}
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
                        {formatDate(log.createdAt)}
                      </Text>
                    </Td>
                    <Td>
                      <Text fontSize="sm" fontWeight="medium">
                        {log.createdBy}
                      </Text>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        ) : (
          <Alert status="info">
            <AlertIcon />
            No status logs found for this sale.
          </Alert>
        )}
      </Box>

      {/* Action Buttons */}
      <Box textAlign="center">
        <HStack spacing={4} justify="center">
          {availableStatusTransitions.length > 0 && (
            <Button 
              colorScheme="blue" 
              size="lg"
              onClick={openStatusModal}
            >
              {availableStatusTransitions[0]?.allowedTransitions[0]}
            </Button>
          )}
          {sale.status !== 'CANCELLED' && (
            <Button 
              colorScheme="red" 
              size="lg"
              onClick={openCancelModal}
              isLoading={cancelling}
              loadingText="Cancelling..."
            >
              Cancel Sale
            </Button>
          )}
          <Button 
            variant="outline" 
            size="lg"
            onClick={() => router.push(`/sale/${id}/edit`)}
          >
            Edit Sale
          </Button>
          <Button 
            variant="outline" 
            size="lg"
            onClick={() => router.push('/sale')}
          >
            Back to Sale Page
          </Button>
        </HStack>
      </Box>

      {/* Cancel Sale Modal */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Cancel Sale</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl>
              <FormLabel>Reason for cancellation</FormLabel>
              <Input
                value={cancelReason}
                onChange={(e) => setCancelReason(e.target.value)}
                placeholder="Enter reason for cancelling this sale..."
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
              onClick={handleCancelSale}
              isLoading={cancelling}
              loadingText="Cancelling..."
            >
              Confirm Cancel
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Change Status Modal */}
      <Modal isOpen={isStatusModalOpen} onClose={onStatusModalClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Change Sale Status</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack spacing={4}>
              <FormControl>
                <FormLabel>New Status</FormLabel>
                <Select
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value as OrderStatus)}
                  placeholder="Select new status"
                >
                  {availableStatusTransitions.flatMap((transition) => 
                    transition.allowedTransitions.map((status) => (
                      <option key={status} value={status}>
                        {status}
                      </option>
                    ))
                  )}
                </Select>
              </FormControl>
              <FormControl>
                <FormLabel>Note (Optional)</FormLabel>
                <Input
                  value={statusNote}
                  onChange={(e) => setStatusNote(e.target.value)}
                  placeholder="Enter note for status change..."
                  size="md"
                />
              </FormControl>
            </VStack>
          </ModalBody>
          <ModalFooter>
            <Button variant="ghost" mr={3} onClick={onStatusModalClose}>
              Cancel
            </Button>
            <Button 
              colorScheme="blue" 
              onClick={handleStatusChange}
              isDisabled={!newStatus}
            >
              Change Status
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default SaleDetailPage; 