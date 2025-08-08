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
  Spinner,
  Alert,
  AlertIcon,
  Select,
  Input,
  Button,
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
} from '@chakra-ui/react';
import { FiDollarSign, FiRefreshCw, FiTrendingUp, FiTrendingDown } from 'react-icons/fi';
import { exchangeRateService } from '@/services/exchangeRateService';

interface ExchangeRateWidgetProps {
  showConverter?: boolean;
  showRates?: boolean;
  baseCurrency?: string;
}

const ExchangeRateWidget: React.FC<ExchangeRateWidgetProps> = ({
  showConverter = true,
  showRates = true,
  baseCurrency = 'USD',
}) => {
  const [exchangeRates, setExchangeRates] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [selectedBaseCurrency, setSelectedBaseCurrency] = useState(baseCurrency);
  const [converterAmount, setConverterAmount] = useState(100);
  const [fromCurrency, setFromCurrency] = useState('USD');
  const [toCurrency, setToCurrency] = useState('VND');
  const [conversionResult, setConversionResult] = useState<any>(null);

  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');

  useEffect(() => {
    loadExchangeRates();
  }, [selectedBaseCurrency]);

  useEffect(() => {
    if (exchangeRates && fromCurrency && toCurrency) {
      const result = exchangeRateService.convertCurrency(
        converterAmount,
        fromCurrency,
        toCurrency,
        exchangeRates.rates
      );
      setConversionResult(result);
    }
  }, [exchangeRates, converterAmount, fromCurrency, toCurrency]);

  const loadExchangeRates = async () => {
    setLoading(true);
    try {
      const data = await exchangeRateService.getExchangeRates(selectedBaseCurrency);
      setExchangeRates(data);
    } catch (error) {
      console.error('Error loading exchange rates:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    loadExchangeRates();
  };

  const popularCurrencies = exchangeRateService.getPopularCurrencies();

  const ExchangeRatesDisplay = () => (
    <Box bg={bgColor} p={4} borderRadius="lg" border="1px" borderColor={borderColor}>
      <Flex justify="space-between" align="center" mb={3}>
        <Heading size="sm">Exchange Rates</Heading>
        <HStack spacing={2}>
          <Select
            size="xs"
            value={selectedBaseCurrency}
            onChange={(e) => setSelectedBaseCurrency(e.target.value)}
            w="80px"
          >
            {popularCurrencies.map(currency => (
              <option key={currency} value={currency}>{currency}</option>
            ))}
          </Select>
          <Button size="xs" onClick={handleRefresh} isLoading={loading}>
            <Icon as={FiRefreshCw} />
          </Button>
        </HStack>
      </Flex>
      
      {exchangeRates ? (
        <VStack spacing={2} align="stretch">
          {Object.entries(exchangeRates.rates)
            .filter(([currency]) => currency !== selectedBaseCurrency)
            .slice(0, 6)
            .map(([currency, rate]) => (
              <Flex key={currency} justify="space-between" align="center" p={2} bg="gray.50" borderRadius="md">
                <HStack>
                  <Icon as={FiDollarSign} color="green.500" />
                  <Text fontSize="sm" fontWeight="medium">
                    {currency} ({exchangeRateService.getCurrencySymbol(currency)})
                  </Text>
                </HStack>
                <Text fontSize="sm" color="gray.600" fontWeight="medium">
                  {Number(rate).toFixed(4)}
                </Text>
              </Flex>
            ))}
          <Text fontSize="xs" color="gray.500" textAlign="center">
            Base: {exchangeRates.base} | Date: {exchangeRates.date}
          </Text>
        </VStack>
      ) : (
        <Flex justify="center" align="center" minH="100px">
          <Spinner size="sm" />
        </Flex>
      )}
    </Box>
  );

  const CurrencyConverter = () => (
    <Box bg={bgColor} p={4} borderRadius="lg" border="1px" borderColor={borderColor}>
      <Heading size="sm" mb={3}>Currency Converter</Heading>
      
      <VStack spacing={4} align="stretch">
        {/* Amount Input */}
        <Box>
          <Text fontSize="sm" mb={2}>Amount</Text>
          <NumberInput
            value={converterAmount}
            onChange={(value) => setConverterAmount(Number(value))}
            min={0}
            precision={2}
          >
            <NumberInputField />
            <NumberInputStepper>
              <NumberIncrementStepper />
              <NumberDecrementStepper />
            </NumberInputStepper>
          </NumberInput>
        </Box>

        {/* Currency Selection */}
        <SimpleGrid columns={2} spacing={3}>
          <Box>
            <Text fontSize="sm" mb={2}>From</Text>
            <Select
              value={fromCurrency}
              onChange={(e) => setFromCurrency(e.target.value)}
              size="sm"
            >
              {popularCurrencies.map(currency => (
                <option key={currency} value={currency}>{currency}</option>
              ))}
            </Select>
          </Box>
          <Box>
            <Text fontSize="sm" mb={2}>To</Text>
            <Select
              value={toCurrency}
              onChange={(e) => setToCurrency(e.target.value)}
              size="sm"
            >
              {popularCurrencies.map(currency => (
                <option key={currency} value={currency}>{currency}</option>
              ))}
            </Select>
          </Box>
        </SimpleGrid>

        {/* Conversion Result */}
        {conversionResult && (
          <Box p={3} bg="blue.50" borderRadius="md" border="1px" borderColor="blue.200">
            <VStack spacing={1} align="stretch">
              <Flex justify="space-between" align="center">
                <Text fontSize="sm" color="gray.600">
                  {conversionResult.amount} {conversionResult.fromCurrency}
                </Text>
                <Icon as={FiTrendingUp} color="blue.500" />
                <Text fontSize="sm" color="gray.600">
                  {conversionResult.convertedAmount.toFixed(2)} {conversionResult.toCurrency}
                </Text>
              </Flex>
              <Text fontSize="xs" color="gray.500" textAlign="center">
                Rate: 1 {conversionResult.fromCurrency} = {conversionResult.rate.toFixed(4)} {conversionResult.toCurrency}
              </Text>
            </VStack>
          </Box>
        )}
      </VStack>
    </Box>
  );

  if (loading && !exchangeRates) {
    return (
      <Flex justify="center" align="center" minH="200px">
        <VStack spacing={4}>
          <Spinner size="lg" color="blue.500" />
          <Text color="gray.600">Loading exchange rates...</Text>
        </VStack>
      </Flex>
    );
  }

  return (
    <Box>
      <Heading size="md" mb={4}>Currency Exchange</Heading>
      <SimpleGrid columns={{ base: 1, lg: 2 }} spacing={4}>
        {showRates && <ExchangeRatesDisplay />}
        {showConverter && <CurrencyConverter />}
      </SimpleGrid>
    </Box>
  );
};

export default ExchangeRateWidget;
