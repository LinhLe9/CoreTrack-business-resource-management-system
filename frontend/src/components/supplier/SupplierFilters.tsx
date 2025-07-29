// components/SupplierFilters.tsx
import React, { useState } from 'react';
import { FormControl, FormLabel } from '@chakra-ui/react';
import dynamic from 'next/dynamic';
import { SupplierQueryParams } from '../../types/supplier';
import useSupplierCountries from '@/hooks/useSupplierCountries';
import FilterPanel from '../general/FilterPanel';

// Dynamic import Select to disable SSR (fix hydration mismatch issue)
const Select = dynamic(() => import('react-select'), { ssr: false });

interface SupplierFiltersProps {
  onFilter: (filters: Omit<SupplierQueryParams, 'search' | 'page' | 'size' | 'sort'>) => void;
  initialFilters?: Omit<SupplierQueryParams, 'search' | 'page' | 'size' | 'sort'>;
}

interface OptionType {
  label: string | number;
  value: string | number;
}

const toArray = (v: any) => Array.isArray(v) ? v : v !== undefined ? [v] : [];

const SupplierFilters: React.FC<SupplierFiltersProps> = ({ onFilter, initialFilters = {} }) => {
  const countryList = useSupplierCountries();
  const countryOptions = countryList.map((country: string) => ({ label: country, value: country }));

  const [selectedCountries, setSelectedCountries] = useState<OptionType[]>(
    toArray(initialFilters.country).map(country => ({ label: country, value: country }))
  );

  const apply = () => {
    onFilter({
      country: selectedCountries.length ? selectedCountries.map(c => c.value) : undefined,
    });
  };

  const clear = () => {
    setSelectedCountries([]);
    onFilter({});
  };

  return (
    <FilterPanel onApply={apply} onClear={clear}>
      <FormControl>
        <FormLabel>Country</FormLabel>
        <Select
          isMulti
          options={countryOptions}
          value={selectedCountries}
          onChange={(v) => setSelectedCountries(v as OptionType[])}
          placeholder="Select countries"
        />
      </FormControl>
    </FilterPanel>
  );
};

export default SupplierFilters;