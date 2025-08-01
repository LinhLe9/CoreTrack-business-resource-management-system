'use client';

import React, { useState } from 'react';
import { FormControl, FormLabel } from '@chakra-ui/react';
import dynamic from 'next/dynamic';
import { ProductionTicketFilter } from '../../types/productionTicket';
import FilterPanel from '../general/FilterPanel';
import { ProductionTicketStatus } from '../../types/productionTicket';

// Dynamic import Select to disable SSR (fix hydration mismatch issue)
const Select = dynamic(() => import('react-select'), { ssr: false });

interface ProductionTicketFiltersProps {
  onFilter: (filters: Omit<ProductionTicketFilter, 'search' | 'page' | 'size'>) => void;
  initialFilters?: Omit<ProductionTicketFilter, 'search' | 'page' | 'size'>;
}

interface OptionType {
  label: string;
  value: string;
}

const toArray = (v: any) => Array.isArray(v) ? v : v !== undefined ? [v] : [];

const productionTicketStatuses = [
  { label: 'New', value: ProductionTicketStatus.NEW },
  { label: 'In Progress', value: ProductionTicketStatus.IN_PROGRESS },
  { label: 'Partial Complete', value: ProductionTicketStatus.PARTIAL_COMPLETE },
  { label: 'Complete', value: ProductionTicketStatus.COMPLETE },
  { label: 'Partial Cancelled', value: ProductionTicketStatus.PARTIAL_CANCELLED },
  { label: 'Cancelled', value: ProductionTicketStatus.CANCELLED },
];

const ProductionTicketFilters: React.FC<ProductionTicketFiltersProps> = ({ onFilter, initialFilters = {} }) => {
  const statusOptions = productionTicketStatuses;

  const [selectedStatuses, setSelectedStatuses] = useState<OptionType[]>(
    toArray(initialFilters.ticketStatus).map(s => ({ label: s, value: s }))
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
        <FormLabel>Status</FormLabel>
        <Select
          isMulti
          options={statusOptions}
          value={selectedStatuses}
          onChange={(v) => setSelectedStatuses(v as OptionType[])}
          placeholder="Select statuses"
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

export default ProductionTicketFilters; 