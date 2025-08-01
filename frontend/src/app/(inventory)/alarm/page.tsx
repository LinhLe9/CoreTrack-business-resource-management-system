'use client';

import React, { useState, useEffect } from 'react';
import {
  Box,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Heading,
  VStack,
  Text,
  Center,
  Spinner,
  Badge,
  HStack,
  Button,
  useToast,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
  Card,
  CardBody,
  CardHeader,
  Flex,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
} from '@chakra-ui/react';
import { getProductAlarms, getMaterialAlarms, getAlarmStatistics } from '@/services/alarmService';
import { ProductAlarm, MaterialAlarm } from '@/types/alarm';

const AlarmPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState(0);
  const [statistics, setStatistics] = useState<any>(null);
  const [loadingStats, setLoadingStats] = useState(true);
  const toast = useToast();

  useEffect(() => {
    const fetchStatistics = async () => {
      try {
        setLoadingStats(true);
        const stats = await getAlarmStatistics();
        setStatistics(stats);
      } catch (error) {
        console.error('Error fetching alarm statistics:', error);
        toast({
          title: 'Error',
          description: 'Failed to load alarm statistics',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      } finally {
        setLoadingStats(false);
      }
    };

    fetchStatistics();
  }, [toast]);

  return (
    <Box maxW="1200px" mx="auto" p={6}>
      <Heading as="h1" size="xl" mb={6}>
        Inventory Alarms
      </Heading>

      {/* Statistics Cards */}
      {!loadingStats && statistics && (
        <Box mb={6}>
          <HStack spacing={4} mb={4}>
            <Stat>
              <StatLabel>Product Alarms</StatLabel>
              <StatNumber>{statistics.totalProductAlarms}</StatNumber>
              <StatHelpText>Total product alarms</StatHelpText>
            </Stat>
            <Stat>
              <StatLabel>Material Alarms</StatLabel>
              <StatNumber>{statistics.totalMaterialAlarms}</StatNumber>
              <StatHelpText>Total material alarms</StatHelpText>
            </Stat>
            <Stat>
              <StatLabel>Critical Alarms</StatLabel>
              <StatNumber color="red.500">{statistics.criticalAlarms}</StatNumber>
              <StatHelpText>Critical priority alarms</StatHelpText>
            </Stat>
            <Stat>
              <StatLabel>Unresolved</StatLabel>
              <StatNumber color="orange.500">{statistics.unresolvedAlarms}</StatNumber>
              <StatHelpText>Unresolved alarms</StatHelpText>
            </Stat>
          </HStack>
        </Box>
      )}

      <Tabs 
        index={activeTab} 
        onChange={setActiveTab}
        variant="enclosed"
        colorScheme="blue"
      >
        <TabList>
          <Tab>Product Alarms</Tab>
          <Tab>Material Alarms</Tab>
        </TabList>

        <TabPanels>
          <TabPanel>
            <ProductAlarms />
          </TabPanel>
          <TabPanel>
            <MaterialAlarms />
          </TabPanel>
        </TabPanels>
      </Tabs>
    </Box>
  );
};

// Product Alarms Component
const ProductAlarms: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [alarms, setAlarms] = useState<ProductAlarm[]>([]);
  const toast = useToast();

  useEffect(() => {
    const fetchAlarms = async () => {
      try {
        setLoading(true);
        const response = await getProductAlarms();
        setAlarms(response.content);
      } catch (error) {
        console.error('Error fetching product alarms:', error);
        toast({
          title: 'Error',
          description: 'Failed to load product alarms',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      } finally {
        setLoading(false);
      }
    };

    fetchAlarms();
  }, [toast]);

  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'CRITICAL': return 'red';
      case 'HIGH': return 'orange';
      case 'MEDIUM': return 'yellow';
      case 'LOW': return 'green';
      default: return 'gray';
    }
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'OUT_OF_STOCK': return 'red';
      case 'LOW_STOCK': return 'orange';
      case 'OVER_STOCK': return 'blue';
      default: return 'gray';
    }
  };

  if (loading) {
    return (
      <Center minHeight="200px">
        <Spinner size="xl" />
      </Center>
    );
  }

  return (
    <VStack spacing={4} align="stretch">
      <Text fontSize="lg" fontWeight="semibold" mb={4}>
        Product Inventory Alarms
      </Text>
      
      {alarms.length === 0 ? (
        <Center minHeight="200px">
          <Alert status="info">
            <AlertIcon />
            <AlertTitle>No Alarms!</AlertTitle>
            <AlertDescription>
              All product inventory levels are within normal ranges.
            </AlertDescription>
          </Alert>
        </Center>
      ) : (
        <VStack spacing={4} align="stretch">
          {alarms.map((alarm) => (
            <Card key={alarm.id} variant="outline">
              <CardHeader>
                <Flex justify="space-between" align="center">
                  <Box>
                    <Text fontWeight="bold">{alarm.productName}</Text>
                    <Text fontSize="sm" color="gray.500">
                      {alarm.variantName ? `${alarm.variantName} (${alarm.variantSku})` : alarm.productSku}
                    </Text>
                  </Box>
                  <HStack spacing={2}>
                    <Badge colorScheme={getSeverityColor(alarm.severity)}>
                      {alarm.severity}
                    </Badge>
                    <Badge colorScheme={getTypeColor(alarm.type)}>
                      {alarm.type.replace('_', ' ')}
                    </Badge>
                  </HStack>
                </Flex>
              </CardHeader>
              <CardBody>
                <VStack align="stretch" spacing={2}>
                  <Text>{alarm.message}</Text>
                  <HStack justify="space-between">
                    <Text fontSize="sm" color="gray.500">
                      Current Stock: {alarm.currentStock}
                    </Text>
                    <Text fontSize="sm" color="gray.500">
                      Min Alert: {alarm.minAlertStock}
                    </Text>
                    {alarm.maxStockLevel && (
                      <Text fontSize="sm" color="gray.500">
                        Max Level: {alarm.maxStockLevel}
                      </Text>
                    )}
                  </HStack>
                  <Text fontSize="xs" color="gray.400">
                    Created: {new Date(alarm.createdAt).toLocaleString()}
                  </Text>
                </VStack>
              </CardBody>
            </Card>
          ))}
        </VStack>
      )}
    </VStack>
  );
};

// Material Alarms Component
const MaterialAlarms: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [alarms, setAlarms] = useState<MaterialAlarm[]>([]);
  const toast = useToast();

  useEffect(() => {
    const fetchAlarms = async () => {
      try {
        setLoading(true);
        const response = await getMaterialAlarms();
        setAlarms(response.content);
      } catch (error) {
        console.error('Error fetching material alarms:', error);
        toast({
          title: 'Error',
          description: 'Failed to load material alarms',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      } finally {
        setLoading(false);
      }
    };

    fetchAlarms();
  }, [toast]);

  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'CRITICAL': return 'red';
      case 'HIGH': return 'orange';
      case 'MEDIUM': return 'yellow';
      case 'LOW': return 'green';
      default: return 'gray';
    }
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'OUT_OF_STOCK': return 'red';
      case 'LOW_STOCK': return 'orange';
      case 'OVER_STOCK': return 'blue';
      default: return 'gray';
    }
  };

  if (loading) {
    return (
      <Center minHeight="200px">
        <Spinner size="xl" />
      </Center>
    );
  }

  return (
    <VStack spacing={4} align="stretch">
      <Text fontSize="lg" fontWeight="semibold" mb={4}>
        Material Inventory Alarms
      </Text>
      
      {alarms.length === 0 ? (
        <Center minHeight="200px">
          <Alert status="info">
            <AlertIcon />
            <AlertTitle>No Alarms!</AlertTitle>
            <AlertDescription>
              All material inventory levels are within normal ranges.
            </AlertDescription>
          </Alert>
        </Center>
      ) : (
        <VStack spacing={4} align="stretch">
          {alarms.map((alarm) => (
            <Card key={alarm.id} variant="outline">
              <CardHeader>
                <Flex justify="space-between" align="center">
                  <Box>
                    <Text fontWeight="bold">{alarm.materialName}</Text>
                    <Text fontSize="sm" color="gray.500">
                      {alarm.variantName ? `${alarm.variantName} (${alarm.variantSku})` : alarm.materialSku}
                    </Text>
                  </Box>
                  <HStack spacing={2}>
                    <Badge colorScheme={getSeverityColor(alarm.severity)}>
                      {alarm.severity}
                    </Badge>
                    <Badge colorScheme={getTypeColor(alarm.type)}>
                      {alarm.type.replace('_', ' ')}
                    </Badge>
                  </HStack>
                </Flex>
              </CardHeader>
              <CardBody>
                <VStack align="stretch" spacing={2}>
                  <Text>{alarm.message}</Text>
                  <HStack justify="space-between">
                    <Text fontSize="sm" color="gray.500">
                      Current Stock: {alarm.currentStock}
                    </Text>
                    <Text fontSize="sm" color="gray.500">
                      Min Alert: {alarm.minAlertStock}
                    </Text>
                    {alarm.maxStockLevel && (
                      <Text fontSize="sm" color="gray.500">
                        Max Level: {alarm.maxStockLevel}
                      </Text>
                    )}
                  </HStack>
                  <Text fontSize="xs" color="gray.400">
                    Created: {new Date(alarm.createdAt).toLocaleString()}
                  </Text>
                </VStack>
              </CardBody>
            </Card>
          ))}
        </VStack>
      )}
    </VStack>
  );
};

export default AlarmPage;
