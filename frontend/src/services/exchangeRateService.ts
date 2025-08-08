import axios from 'axios';

// Exchange Rate API Configuration
const EXCHANGE_RATE_API = {
  baseUrl: 'https://api.exchangerate-api.com/v4/latest',
  // Free API - no key required
};

// Types
interface ExchangeRate {
  base: string;
  date: string;
  rates: Record<string, number>;
}

interface CurrencyConversion {
  fromCurrency: string;
  toCurrency: string;
  amount: number;
  convertedAmount: number;
  rate: number;
}

class ExchangeRateService {
  // Get current exchange rates
  async getExchangeRates(baseCurrency: string = 'USD'): Promise<ExchangeRate> {
    try {
      const response = await axios.get(`${EXCHANGE_RATE_API.baseUrl}/${baseCurrency}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching exchange rates:', error);
      // Fallback data
      return {
        base: 'USD',
        date: new Date().toISOString().split('T')[0],
        rates: {
          EUR: 0.85,
          GBP: 0.73,
          JPY: 110.5,
          VND: 23000,
          CNY: 6.45,
          KRW: 1150,
          SGD: 1.35,
          THB: 33.5,
        },
      };
    }
  }

  // Convert currency
  convertCurrency(
    amount: number, 
    fromCurrency: string, 
    toCurrency: string, 
    rates: Record<string, number>
  ): CurrencyConversion {
    if (fromCurrency === toCurrency) {
      return {
        fromCurrency,
        toCurrency,
        amount,
        convertedAmount: amount,
        rate: 1,
      };
    }
    
    const fromRate = rates[fromCurrency] || 1;
    const toRate = rates[toCurrency] || 1;
    const convertedAmount = (amount / fromRate) * toRate;
    const rate = toRate / fromRate;
    
    return {
      fromCurrency,
      toCurrency,
      amount,
      convertedAmount,
      rate,
    };
  }

  // Format currency
  formatCurrency(amount: number, currency: string = 'USD'): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
    }).format(amount);
  }

  // Get popular currencies
  getPopularCurrencies(): string[] {
    return ['USD', 'EUR', 'GBP', 'JPY', 'VND', 'CNY', 'KRW', 'SGD', 'THB'];
  }

  // Get currency symbol
  getCurrencySymbol(currency: string): string {
    const symbols: Record<string, string> = {
      USD: '$',
      EUR: '€',
      GBP: '£',
      JPY: '¥',
      VND: '₫',
      CNY: '¥',
      KRW: '₩',
      SGD: 'S$',
      THB: '฿',
    };
    return symbols[currency] || currency;
  }
}

export const exchangeRateService = new ExchangeRateService();
export default exchangeRateService;
