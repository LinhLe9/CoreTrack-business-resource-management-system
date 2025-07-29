import { useEffect, useState } from 'react'
import api from '@/lib/axios'

export default function useSupplierCountries() {
  const [countries, setCountries] = useState<string[]>([])

  useEffect(() => {
    const fetchCountries = async () => {
      try {
        const response = await api.get('/suppliers/available-country')
        setCountries(response.data)
      } catch (error) {
        console.error('Error fetching supplier countries:', error)
        // Fallback to common countries if API fails
        setCountries([
          'United States',
          'China',
          'Germany',
          'Japan',
          'South Korea',
          'India',
          'United Kingdom',
          'France',
          'Italy',
          'Canada',
          'Vietnam',
          'Thailand',
          'Malaysia',
          'Singapore',
          'Indonesia'
        ])
      }
    }

    fetchCountries()
  }, [])

  return countries
}