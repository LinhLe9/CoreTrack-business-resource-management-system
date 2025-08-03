// components/sale/SaleFilters.tsx
import React, { useState } from 'react';
import { FormControl, FormLabel } from '@chakra-ui/react';
import dynamic from 'next/dynamic';
import FilterPanel from '../general/FilterPanel';
import { OrderStatus } from '../../types/sale';

// Dynamic import Select to disable SSR (fix hydration mismatch issue)
const Select = dynamic(() => import('react-select'), { ssr: false });

interface SaleFiltersProps {
  onFilter: (filters: { ticketStatus?: string[] }) => void;
  initialFilters?: { ticketStatus?: string[] };
}

interface OptionType {
  label: string;
  value: string;
}

const toArray = (v: any) => Array.isArray(v) ? v : v !== undefined ? [v] : [];

const SaleFilters: React.FC<SaleFiltersProps> = ({ onFilter, initialFilters = {} }) => {
  const statusOptions: OptionType[] = Object.values(OrderStatus).map(status => ({
    label: status.replace('_', ' '),
    value: status,
  }));

  const [selectedStatuses, setSelectedStatuses] = useState<OptionType[]>(
    toArray(initialFilters.ticketStatus).map(s => ({ label: s.replace('_', ' '), value: s }))
  );

  const apply = () => {
    onFilter({
      ticketStatus: selectedStatuses.length ? selectedStatuses.map(s => s.value) : undefined,
    });
  };

  const clear = () => {
    setSelectedStatuses([]);
    onFilter({});
  };

  return (
    <FilterPanel onApply={apply} onClear={clear}>
      <FormControl>
        <FormLabel>Sale Status</FormLabel>
        <Select
          isMulti
          options={statusOptions}
          value={selectedStatuses}
          onChange={(v) => setSelectedStatuses(v as OptionType[])}
          placeholder="Select sale statuses"
          styles={{
            control: (provided) => ({
              ...provided,
              cursor: 'pointer',
            }),
            menu: (provided) => ({
              ...provided,
              zIndex: 9999,
            }),
          }}
        />
      </FormControl>
    </FilterPanel>
  );
};

export default SaleFilters; 