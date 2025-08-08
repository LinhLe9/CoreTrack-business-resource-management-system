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
  Image,
  Link,
  Spinner,
  Alert,
  AlertIcon,
  Button,
  Select,
} from '@chakra-ui/react';
import { FiDollarSign, FiThermometer, FiWind, FiDroplets, FiGlobe, FiTrendingUp, FiTrendingDown } from 'react-icons/fi';
import { externalApiService } from '@/services/externalApiService';

interface ExternalDataWidgetProps {
  showWeather?: boolean;
  showExchangeRates?: boolean;
  showNews?: boolean;
  showStock?: boolean;
}

const ExternalDataWidget: React.FC<ExternalDataWidgetProps> = ({
  showWeather = true,
  showExchangeRates = true,
  showNews = true,
  showStock = true,
}) => {
  const [weatherData, setWeatherData] = useState<any>(null);
  const [exchangeRates, setExchangeRates] = useState<any>(null);
  const [newsData, setNewsData] = useState<any[]>([]);
  const [stockData, setStockData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [selectedCity, setSelectedCity] = useState('Hanoi');
  const [selectedStock, setSelectedStock] = useState('MSFT');

  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');

  useEffect(() => {
    loadExternalData();
  }, [selectedCity, selectedStock]);

  const loadExternalData = async () => {
    setLoading(true);
    try {
      const promises = [];

      if (showWeather) {
        promises.push(externalApiService.getWeatherData(selectedCity));
      }
      if (showExchangeRates) {
        promises.push(externalApiService.getExchangeRates('USD'));
      }
      if (showNews) {
        promises.push(externalApiService.getBusinessNews('us', 'business'));
      }
      if (showStock) {
        promises.push(externalApiService.getStockPrice(selectedStock));
      }

      const results = await Promise.all(promises);
      let index = 0;

      if (showWeather) {
        setWeatherData(results[index++]);
      }
      if (showExchangeRates) {
        setExchangeRates(results[index++]);
      }
      if (showNews) {
        setNewsData(results[index++]);
      }
      if (showStock) {
        setStockData(results[index++]);
      }
    } catch (error) {
      console.error('Error loading external data:', error);
    } finally {
      setLoading(false);
    }
  };

  const WeatherWidget = () => (
    <Box bg={bgColor} p={4} borderRadius="lg" border="1px" borderColor={borderColor}>
      <Flex justify="space-between" align="center" mb={3}>
        <Heading size="sm">Weather</Heading>
        <Select
          size="xs"
          value={selectedCity}
          onChange={(e) => setSelectedCity(e.target.value)}
          w="120px"
        >
          <option value="Hanoi">Hanoi</option>
          <option value="Ho Chi Minh City">HCMC</option>
          <option value="Da Nang">Da Nang</option>
          <option value="Singapore">Singapore</option>
        </Select>
      </Flex>
      
      {weatherData ? (
        <VStack spacing={2} align="stretch">
          <Flex justify="space-between" align="center">
            <HStack>
              <Image
                src={externalApiService.getWeatherIconUrl(weatherData.weather[0].icon)}
                alt="weather"
                boxSize="40px"
              />
              <VStack align="start" spacing={0}>
                <Text fontSize="lg" fontWeight="bold">
                  {Math.round(weatherData.main.temp)}°C
                </Text>
                <Text fontSize="sm" color="gray.600">
                  {weatherData.weather[0].description}
                </Text>
              </VStack>
            </HStack>
          </Flex>
          
          <SimpleGrid columns={3} spacing={2} mt={2}>
            <VStack spacing={1}>
              <Icon as={FiDroplets} color="blue.500" />
              <Text fontSize="xs">{weatherData.main.humidity}%</Text>
              <Text fontSize="xs" color="gray.500">Humidity</Text>
            </VStack>
            <VStack spacing={1}>
              <Icon as={FiWind} color="gray.500" />
              <Text fontSize="xs">{weatherData.wind.speed} m/s</Text>
              <Text fontSize="xs" color="gray.500">Wind</Text>
            </VStack>
            <VStack spacing={1}>
              <Icon as={FiThermometer} color="red.500" />
              <Text fontSize="xs">{weatherData.main.pressure} hPa</Text>
              <Text fontSize="xs" color="gray.500">Pressure</Text>
            </VStack>
          </SimpleGrid>
        </VStack>
      ) : (
        <Spinner size="sm" />
      )}
    </Box>
  );

  const ExchangeRateWidget = () => (
    <Box bg={bgColor} p={4} borderRadius="lg" border="1px" borderColor={borderColor}>
      <Heading size="sm" mb={3}>Exchange Rates</Heading>
      
      {exchangeRates ? (
        <VStack spacing={2} align="stretch">
          {Object.entries(exchangeRates.rates).slice(0, 4).map(([currency, rate]) => (
            <Flex key={currency} justify="space-between" align="center">
              <HStack>
                <Icon as={FiDollarSign} color="green.500" />
                <Text fontSize="sm" fontWeight="medium">
                  {currency}
                </Text>
              </HStack>
              <Text fontSize="sm" color="gray.600">
                {Number(rate).toFixed(4)}
              </Text>
            </Flex>
          ))}
          <Text fontSize="xs" color="gray.500" textAlign="center">
            Base: {exchangeRates.base}
          </Text>
        </VStack>
      ) : (
        <Spinner size="sm" />
      )}
    </Box>
  );

  const NewsWidget = () => (
    <Box bg={bgColor} p={4} borderRadius="lg" border="1px" borderColor={borderColor}>
      <Heading size="sm" mb={3}>Business News</Heading>
      
      {newsData.length > 0 ? (
        <VStack spacing={3} align="stretch">
          {newsData.slice(0, 3).map((article, index) => (
            <Box key={index} p={2} bg="gray.50" borderRadius="md">
              <Text fontSize="sm" fontWeight="medium" mb={1}>
                {article.title}
              </Text>
              <Text fontSize="xs" color="gray.600" mb={2} noOfLines={2}>
                {article.description}
              </Text>
              <Flex justify="space-between" align="center">
                <Text fontSize="xs" color="gray.500">
                  {article.source.name}
                </Text>
                <Link href={article.url} isExternal fontSize="xs" color="blue.500">
                  Read more
                </Link>
              </Flex>
            </Box>
          ))}
        </VStack>
      ) : (
        <Spinner size="sm" />
      )}
    </Box>
  );

  const StockWidget = () => (
    <Box bg={bgColor} p={4} borderRadius="lg" border="1px" borderColor={borderColor}>
      <Flex justify="space-between" align="center" mb={3}>
        <Heading size="sm">Stock Market</Heading>
        <Select
          size="xs"
          value={selectedStock}
          onChange={(e) => setSelectedStock(e.target.value)}
          w="100px"
        >
          <option value="MSFT">MSFT</option>
          <option value="AAPL">AAPL</option>
          <option value="GOOGL">GOOGL</option>
          <option value="TSLA">TSLA</option>
        </Select>
      </Flex>
      
      {stockData ? (
        <VStack spacing={2} align="stretch">
          <Flex justify="space-between" align="center">
            <Text fontSize="lg" fontWeight="bold">
              {stockData['Global Quote']['01. symbol']}
            </Text>
            <Text fontSize="lg" fontWeight="bold">
              ${stockData['Global Quote']['05. price']}
            </Text>
          </Flex>
          
          <Flex justify="space-between" align="center">
            <HStack>
              <Icon 
                as={stockData['Global Quote']['09. change'].startsWith('+') ? FiTrendingUp : FiTrendingDown} 
                color={stockData['Global Quote']['09. change'].startsWith('+') ? 'green.500' : 'red.500'} 
              />
              <Text fontSize="sm" color={stockData['Global Quote']['09. change'].startsWith('+') ? 'green.500' : 'red.500'}>
                {stockData['Global Quote']['09. change']}
              </Text>
            </HStack>
            <Text fontSize="sm" color={stockData['Global Quote']['10. change percent'].startsWith('+') ? 'green.500' : 'red.500'}>
              {stockData['Global Quote']['10. change percent']}
            </Text>
          </Flex>
        </VStack>
      ) : (
        <Spinner size="sm" />
      )}
    </Box>
  );

  if (loading) {
    return (
      <Flex justify="center" align="center" minH="200px">
        <VStack spacing={4}>
          <Spinner size="lg" color="blue.500" />
          <Text color="gray.600">Loading external data...</Text>
        </VStack>
      </Flex>
    );
  }

  return (
    <Box>
      <Heading size="md" mb={4}>External Data</Heading>
      <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} spacing={4}>
        {showWeather && <WeatherWidget />}
        {showExchangeRates && <ExchangeRateWidget />}
        {showNews && <NewsWidget />}
        {showStock && <StockWidget />}
      </SimpleGrid>
    </Box>
  );
};

export default ExternalDataWidget;
