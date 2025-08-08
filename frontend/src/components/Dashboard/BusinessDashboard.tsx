import React, { useState, useEffect } from 'react';
import {
  Box,
  Flex,
  Heading,
  Text,
  SimpleGrid,
  useColorModeValue,
  Icon,
  HStack,
  VStack,
  Badge,
  Progress,
  Select,
  Spinner,
  Alert,
  AlertIcon,
} from '@chakra-ui/react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import { FiTrendingUp, FiTrendingDown, FiDollarSign, FiShoppingCart, FiPackage, FiUsers } from 'react-icons/fi';
import axios from 'axios';

interface BusinessData {
  sales: {
    dailyData: Array<{
      date: string;
      orderCount: number;
      totalRevenue: number;
      averageOrderValue: number;
    }>;
    totalOrders: number;
    totalRevenue: number;
    averageOrderValue: number;
  };
  inventory: {
    productInventory: Record<string, number>;
    materialInventory: Record<string, number>;
    totalProducts: number;
    totalMaterials: number;
  };
  production: {
    statusDistribution: Record<string, number>;
    totalTickets: number;
    completedTickets: number;
    inProgressTickets: number;
  };
  overview: {
    todayOrders: number;
    todayRevenue: number;
    monthlyOrders: number;
    monthlyRevenue: number;
    totalProducts: number;
    totalMaterials: number;
    lowStockProducts: number;
    outOfStockProducts: number;
  };
}

const BusinessDashboard: React.FC = () => {
  const [businessData, setBusinessData] = useState<BusinessData | null>(null);
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState('7d');
  const [error, setError] = useState<string | null>(null);

  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

  useEffect(() => {
    loadBusinessData();
  }, [timeRange]);

  const loadBusinessData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [salesRes, inventoryRes, productionRes, overviewRes] = await Promise.all([
        axios.get(`/api/business-analytics/sales?timeRange=${timeRange}`),
        axios.get('/api/business-analytics/inventory'),
        axios.get(`/api/business-analytics/production?timeRange=${timeRange}`),
        axios.get('/api/business-analytics/overview'),
      ]);

      setBusinessData({
        sales: salesRes.data,
        inventory: inventoryRes.data,
        production: productionRes.data,
        overview: overviewRes.data,
      });
    } catch (error) {
      console.error('Error loading business data:', error);
      setError('Failed to load business data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, subtitle, icon, color = 'blue' }: any) => (
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
          <Text fontSize="2xl" fontWeight="bold" color={`${color}.500`}>
            {typeof value === 'number' && title.includes('Revenue') ? `$${value.toLocaleString()}` : value}
          </Text>
          {subtitle && (
            <Text fontSize="sm" color="gray.600">
              {subtitle}
            </Text>
          )}
        </VStack>
        <Icon as={icon} boxSize={8} color={`${color}.500`} />
      </Flex>
    </Box>
  );

  if (loading) {
    return (
      <Flex justify="center" align="center" minH="400px">
        <VStack spacing={4}>
          <Spinner size="xl" color="blue.500" />
          <Text color="gray.600">Loading business data...</Text>
        </VStack>
      </Flex>
    );
  }

  if (error) {
    return (
      <Alert status="error" borderRadius="lg">
        <AlertIcon />
        {error}
      </Alert>
    );
  }

  if (!businessData) {
    return (
      <Alert status="warning" borderRadius="lg">
        <AlertIcon />
        No business data available
      </Alert>
    );
  }

  return (
    <VStack spacing={6} align="stretch">
      {/* Header */}
      <Flex justify="space-between" align="center" mb={6}>
        <VStack align="start" spacing={1}>
          <Heading size="lg" color="gray.800">
            Business Analytics
          </Heading>
          <Text color="gray.600">
            Track your business performance and key metrics
          </Text>
        </VStack>
        <Select
          value={timeRange}
          onChange={(e) => setTimeRange(e.target.value)}
          w="200px"
          size="sm"
        >
          <option value="7d">Last 7 days</option>
          <option value="30d">Last 30 days</option>
          <option value="90d">Last 90 days</option>
        </Select>
      </Flex>

      {/* Business Overview Cards */}
      <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} spacing={6} mb={8}>
        <StatCard
          title="Today's Orders"
          value={businessData.overview.todayOrders}
          subtitle={`Revenue: $${businessData.overview.todayRevenue.toLocaleString()}`}
          icon={FiShoppingCart}
          color="green"
        />
        <StatCard
          title="Monthly Orders"
          value={businessData.overview.monthlyOrders}
          subtitle={`Revenue: $${businessData.overview.monthlyRevenue.toLocaleString()}`}
          icon={FiTrendingUp}
          color="blue"
        />
        <StatCard
          title="Total Products"
          value={businessData.overview.totalProducts}
          subtitle={`${businessData.overview.lowStockProducts} low stock, ${businessData.overview.outOfStockProducts} out of stock`}
          icon={FiPackage}
          color="orange"
        />
        <StatCard
          title="Production Tickets"
          value={businessData.production.totalTickets}
          subtitle={`${businessData.production.completedTickets} completed, ${businessData.production.inProgressTickets} in progress`}
          icon={FiUsers}
          color="purple"
        />
      </SimpleGrid>

      {/* Charts */}
      <SimpleGrid columns={{ base: 1, lg: 2 }} spacing={6} mb={8}>
        {/* Daily Sales Trend */}
        <Box bg={bgColor} p={6} borderRadius="lg" border="1px" borderColor={borderColor}>
          <Heading size="md" mb={4}>Daily Sales Trend</Heading>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={businessData.sales.dailyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Line
                type="monotone"
                dataKey="totalRevenue"
                stroke="#8884d8"
                strokeWidth={2}
                name="Revenue"
              />
              <Line
                type="monotone"
                dataKey="orderCount"
                stroke="#82ca9d"
                strokeWidth={2}
                name="Orders"
              />
            </LineChart>
          </ResponsiveContainer>
        </Box>

        {/* Inventory Status */}
        <Box bg={bgColor} p={6} borderRadius="lg" border="1px" borderColor={borderColor}>
          <Heading size="md" mb={4}>Product Inventory Status</Heading>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={Object.entries(businessData.inventory.productInventory).map(([key, value]) => ({
                  name: key,
                  value: value,
                }))}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value }: { name: string; value: number }) => `${name}: ${value}`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {Object.entries(businessData.inventory.productInventory).map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </Box>
      </SimpleGrid>

      {/* Production Status */}
      <Box bg={bgColor} p={6} borderRadius="lg" border="1px" borderColor={borderColor}>
        <Heading size="md" mb={4}>Production Status Distribution</Heading>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={Object.entries(businessData.production.statusDistribution).map(([key, value]) => ({
            status: key,
            count: value,
          }))}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="status" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="count" fill="#8884d8" />
          </BarChart>
        </ResponsiveContainer>
      </Box>
    </VStack>
  );
};

export default BusinessDashboard;
