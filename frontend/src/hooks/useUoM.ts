import { useState, useEffect } from 'react';
import api from '@/lib/axios';

export interface UoM {
  value: string;
  displayName: string;
}

export default function useUoM() {
  const [uomList, setUomList] = useState<UoM[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUoM = async () => {
      try {
        setLoading(true);
        const response = await api.get('/materials/uom');
        setUomList(response.data);
      } catch (error) {
        console.error('Error fetching UoM:', error);
        // Fallback to hardcoded values if API fails
        setUomList([
          { value: 'KG', displayName: 'Kilogram' },
          { value: 'G', displayName: 'Gram' },
          { value: 'LITER', displayName: 'Liter' },
          { value: 'ML', displayName: 'Milliliter' },
          { value: 'PIECE', displayName: 'Piece' },
          { value: 'BOX', displayName: 'Box' },
          { value: 'METER', displayName: 'Meter' },
          { value: 'CM', displayName: 'Centimeter' },
          { value: 'PACK', displayName: 'Pack' }
        ]);
      } finally {
        setLoading(false);
      }
    };

    fetchUoM();
  }, []);

  return { uomList, loading };
} 