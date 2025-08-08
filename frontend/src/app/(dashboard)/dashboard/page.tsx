'use client';

import React, { useState, useEffect } from 'react';
import { Box, Flex, Heading, Text, SimpleGrid, Stat, StatLabel, StatNumber, StatHelpText, StatArrow, useColorModeValue, Icon, HStack, VStack, Badge, Progress, Grid, GridItem } from '@chakra-ui/react';
import { FiTrendingUp, FiTrendingDown, FiDollarSign, FiShoppingCart, FiPackage, FiUsers, FiActivity, FiBarChart } from 'react-icons/fi';
import BusinessDashboard from '@/components/Dashboard/BusinessDashboard';
import ExchangeRateWidget from '@/components/Dashboard/ExchangeRateWidget';
import axios from 'axios';

interface DashboardStats {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  totalCustomers: number;
  revenueChange: number;
  ordersChange: number;
  productsChange: number;
  customersChange: number;
}

export default function Dashboard() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('overview');

  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');

  useEffect(() => {
    loadDashboardStats();
  }, []);

  const loadDashboardStats = async () => {
    try {
      const overviewRes = await axios.get('/api/business-analytics/overview');
      const salesRes = await axios.get('/api/business-analytics/sales?timeRange=7d');
      
      // Mock data for changes (in real app, you'd calculate from historical data)
      const mockStats: DashboardStats = {
        totalRevenue: overviewRes.data.monthlyRevenue || 0,
        totalOrders: overviewRes.data.monthlyOrders || 0,
        totalProducts: overviewRes.data.totalProducts || 0,
        totalCustomers: 150, // Mock data
        revenueChange: 12.5,
        ordersChange: 8.3,
        productsChange: 5.2,
        customersChange: 15.7
      };
      
      setStats(mockStats);
    } catch (error) {
      console.error('Error loading dashboard stats:', error);
      // Fallback mock data
      setStats({
        totalRevenue: 45000,
        totalOrders: 125,
        totalProducts: 85,
        totalCustomers: 150,
        revenueChange: 12.5,
        ordersChange: 8.3,
        productsChange: 5.2,
        customersChange: 15.7
      });
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, change, icon, isPositive = true }: any) => (
    <Box
      bg={bgColor}
      p={6}
      borderRadius="lg"
      border="1px"
      borderColor={borderColor}
      boxShadow="sm"
      transition="all 0.2s"
      _hover={{ transform: 'translateY(-2px)', boxShadow: 'md' }}
    >
      <Flex justify="space-between" align="center">
        <VStack align="start" spacing={2}>
          <Text fontSize="sm" color="gray.500" fontWeight="medium">
            {title}
          </Text>
          <Stat>
            <StatNumber fontSize="2xl" fontWeight="bold">
              {typeof value === 'number' && title.includes('Revenue') ? `$${value.toLocaleString()}` : value}
            </StatNumber>
            <HStack spacing={1}>
              <StatArrow type={isPositive ? 'increase' : 'decrease'} />
              <StatHelpText fontSize="sm" color={isPositive ? 'green.500' : 'red.500'}>
                {change}%
              </StatHelpText>
            </HStack>
          </Stat>
        </VStack>
        <Icon as={icon} boxSize={8} color="blue.500" />
      </Flex>
    </Box>
  );

  const TabButton = ({ id, label, icon }: { id: string; label: string; icon: any }) => (
    <Box
      as="button"
      px={4}
      py={2}
      borderRadius="md"
      bg={activeTab === id ? 'blue.500' : 'transparent'}
      color={activeTab === id ? 'white' : 'gray.600'}
      _hover={{ bg: activeTab === id ? 'blue.600' : 'gray.100' }}
      onClick={() => setActiveTab(id)}
      transition="all 0.2s"
    >
      <HStack spacing={2}>
        <Icon as={icon} />
        <Text fontWeight="medium">{label}</Text>
      </HStack>
    </Box>
  );

  if (loading) {
    return (
      <Box p={8}>
        <Flex justify="center" align="center" minH="400px">
          <Box className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></Box>
        </Flex>
      </Box>
    );
  }

  return (
    <Box p={6} maxW="100%" mx="auto">
      {/* Header */}
      <Flex justify="space-between" align="center" mb={8}>
        <VStack align="start" spacing={1}>
          <Heading size="lg" color="gray.800">
            Dashboard
          </Heading>
          <Text color="gray.600">
            Welcome back! Here's what's happening with your business today.
          </Text>
        </VStack>
        <HStack spacing={2}>
          <Badge colorScheme="green" px={3} py={1} borderRadius="full">
            Live
          </Badge>
        </HStack>
      </Flex>

      {/* Quick Stats */}
      <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} spacing={6} mb={8}>
        <StatCard
          title="Total Revenue"
          value={stats?.totalRevenue}
          change={stats?.revenueChange}
          icon={FiDollarSign}
          isPositive={stats?.revenueChange! > 0}
        />
        <StatCard
          title="Total Orders"
          value={stats?.totalOrders}
          change={stats?.ordersChange}
          icon={FiShoppingCart}
          isPositive={stats?.ordersChange! > 0}
        />
        <StatCard
          title="Total Products"
          value={stats?.totalProducts}
          change={stats?.productsChange}
          icon={FiPackage}
          isPositive={stats?.productsChange! > 0}
        />
        <StatCard
          title="Total Customers"
          value={stats?.totalCustomers}
          change={stats?.customersChange}
          icon={FiUsers}
          isPositive={stats?.customersChange! > 0}
        />
      </SimpleGrid>

      {/* Navigation Tabs */}
      <HStack spacing={4} mb={6} p={2} bg={bgColor} borderRadius="lg" border="1px" borderColor={borderColor}>
        <TabButton id="overview" label="Overview" icon={FiActivity} />
        <TabButton id="analytics" label="Business Analytics" icon={FiBarChart} />
        <TabButton id="exchange" label="Currency Exchange" icon={FiDollarSign} />
        <TabButton id="reports" label="Reports" icon={FiTrendingUp} />
      </HStack>

      {/* Content based on active tab */}
      {activeTab === 'overview' && (
        <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={6}>
          <GridItem>
            <Box bg={bgColor} p={6} borderRadius="lg" border="1px" borderColor={borderColor}>
              <Heading size="md" mb={4}>Recent Activity</Heading>
              <VStack spacing={4} align="stretch">
                {[
                  { label: 'New order received', value: 'Order #1234', time: '2 minutes ago', color: 'green' },
                  { label: 'Product inventory updated', value: '15 items restocked', time: '1 hour ago', color: 'blue' },
                  { label: 'Production ticket completed', value: 'Ticket #5678', time: '3 hours ago', color: 'purple' },
                  { label: 'Customer registration', value: 'New customer added', time: '5 hours ago', color: 'orange' }
                ].map((activity, index) => (
                  <Flex key={index} justify="space-between" align="center" p={3} bg="gray.50" borderRadius="md">
                    <VStack align="start" spacing={1}>
                      <Text fontWeight="medium">{activity.label}</Text>
                      <Text fontSize="sm" color="gray.600">{activity.value}</Text>
                    </VStack>
                    <VStack align="end" spacing={1}>
                      <Badge colorScheme={activity.color as any}>{activity.color}</Badge>
                      <Text fontSize="xs" color="gray.500">{activity.time}</Text>
                    </VStack>
                  </Flex>
                ))}
              </VStack>
            </Box>
          </GridItem>
          <GridItem>
            <VStack spacing={6}>
              <Box bg={bgColor} p={6} borderRadius="lg" border="1px" borderColor={borderColor} w="100%">
                <Heading size="md" mb={4}>System Status</Heading>
                <VStack spacing={4} align="stretch">
                  <Flex justify="space-between" align="center">
                    <Text>Database</Text>
                    <Badge colorScheme="green">Online</Badge>
                  </Flex>
                  <Flex justify="space-between" align="center">
                    <Text>API Services</Text>
                    <Badge colorScheme="green">Online</Badge>
                  </Flex>
                  <Flex justify="space-between" align="center">
                    <Text>Email Service</Text>
                    <Badge colorScheme="green">Online</Badge>
                  </Flex>
                  <Flex justify="space-between" align="center">
                    <Text>File Storage</Text>
                    <Badge colorScheme="green">Online</Badge>
                  </Flex>
                </VStack>
              </Box>
              
              <Box bg={bgColor} p={6} borderRadius="lg" border="1px" borderColor={borderColor} w="100%">
                <Heading size="md" mb={4}>Performance</Heading>
                <VStack spacing={4} align="stretch">
                  <Box>
                    <Flex justify="space-between" mb={2}>
                      <Text fontSize="sm">CPU Usage</Text>
                      <Text fontSize="sm">65%</Text>
                    </Flex>
                    <Progress value={65} colorScheme="blue" size="sm" />
                  </Box>
                  <Box>
                    <Flex justify="space-between" mb={2}>
                      <Text fontSize="sm">Memory Usage</Text>
                      <Text fontSize="sm">42%</Text>
                    </Flex>
                    <Progress value={42} colorScheme="green" size="sm" />
                  </Box>
                  <Box>
                    <Flex justify="space-between" mb={2}>
                      <Text fontSize="sm">Disk Usage</Text>
                      <Text fontSize="sm">78%</Text>
                    </Flex>
                    <Progress value={78} colorScheme="orange" size="sm" />
                  </Box>
                </VStack>
              </Box>
            </VStack>
          </GridItem>
        </Grid>
      )}

      {activeTab === 'analytics' && (
        <Box>
          <BusinessDashboard />
        </Box>
      )}

      {activeTab === 'exchange' && (
        <Box>
          <ExchangeRateWidget />
        </Box>
      )}

      {activeTab === 'reports' && (
        <Box bg={bgColor} p={6} borderRadius="lg" border="1px" borderColor={borderColor}>
          <Heading size="md" mb={4}>Reports</Heading>
          <Text color="gray.600">Reports section coming soon...</Text>
        </Box>
      )}
    </Box>
  );
}