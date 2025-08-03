'use client';

import React, { useState } from 'react';
import { FormControl, FormLabel } from '@chakra-ui/react';
import dynamic from 'next/dynamic';
import { PurchasingTicketFilter } from '../../types/purchasingTicket';
import FilterPanel from '../general/FilterPanel';
import { PurchasingTicketStatus } from '../../types/purchasingTicket';

// Dynamic import Select to disable SSR (fix hydration mismatch issue)
const Select = dynamic(() => import('react-select'), { ssr: false });

interface PurchasingTicketFiltersProps {
  onFilter: (filters: Omit<PurchasingTicketFilter, 'search' | 'page' | 'size'>) => void;
  initialFilters?: Omit<PurchasingTicketFilter, 'search' | 'page' | 'size'>;
}

interface OptionType {
  label: string;
  value: string;
}

const toArray = (v: any) => Array.isArray(v) ? v : v !== undefined ? [v] : [];

const purchasingTicketStatuses = [
  { label: 'New', value: PurchasingTicketStatus.NEW },
  { label: 'Partial Approval', value: PurchasingTicketStatus.PARTIAL_APPROVAL },
  { label: 'Approval', value: PurchasingTicketStatus.APPROVAL },
  { label: 'Partial Successful', value: PurchasingTicketStatus.PARTIAL_SUCCESSFUL },
  { label: 'Successful', value: PurchasingTicketStatus.SUCCESSFUL },
  { label: 'Partial Shipping', value: PurchasingTicketStatus.PARTIAL_SHIPPING },
  { label: 'Shipping', value: PurchasingTicketStatus.SHIPPING },
  { label: 'Partial Ready', value: PurchasingTicketStatus.PARTIAL_READY },
  { label: 'Ready', value: PurchasingTicketStatus.READY },
  { label: 'Closed', value: PurchasingTicketStatus.CLOSED },
  { label: 'Partial Cancelled', value: PurchasingTicketStatus.PARTIAL_CANCELLED },
  { label: 'Cancelled', value: PurchasingTicketStatus.CANCELLED },
];

const PurchasingTicketFilters: React.FC<PurchasingTicketFiltersProps> = ({ onFilter, initialFilters = {} }) => {
  const statusOptions = purchasingTicketStatuses;

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

export default PurchasingTicketFilters; 