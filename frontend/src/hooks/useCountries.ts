import { useState, useEffect } from 'react';

export default function useCountries() {
  const [countries, setCountries] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCountries = async () => {
      try {
        // Try the more reliable endpoint
        let response = await fetch('https://restcountries.com/v3.1/independent?status=true&fields=name');
        
        if (!response.ok) {
          // Try alternative endpoint
          response = await fetch('https://restcountries.com/v3.1/all?fields=name');
        }
        
        if (response.ok) {
          const data = await response.json() as Array<{ name: { common: string } }>;
          // Sort countries by name and get unique names
          const countryNames: string[] = [...new Set(
            data
              .map((country) => country.name.common)
              .sort()
          )];
          setCountries(countryNames);
        } else {
          console.warn('Countries API failed, using fallback');
          setCountries(getFallbackCountries());
        }
      } catch (error) {
        console.error('Error fetching countries:', error);
        setCountries(getFallbackCountries());
      } finally {
        setLoading(false);
      }
    };

    fetchCountries();
  }, []);

  return { countries, loading };
}

// Fallback countries if API is not available
const getFallbackCountries = (): string[] => [
  'United States', 'China', 'Germany', 'Japan', 'South Korea', 'India', 
  'United Kingdom', 'France', 'Italy', 'Canada', 'Vietnam', 'Thailand', 
  'Malaysia', 'Singapore', 'Indonesia', 'Australia', 'Brazil', 'Mexico',
  'Spain', 'Netherlands', 'Switzerland', 'Sweden', 'Norway', 'Denmark',
  'Finland', 'Poland', 'Czech Republic', 'Austria', 'Belgium', 'Portugal'
]; 