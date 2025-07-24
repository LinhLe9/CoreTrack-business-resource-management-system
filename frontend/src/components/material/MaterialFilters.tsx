// components/ProductFilters.tsx
import React, { useState } from 'react';
import { FormControl, FormLabel } from '@chakra-ui/react';
import dynamic from 'next/dynamic';
import { MaterialQueryParams } from '../../types/material';
import useMaterialGroups from '@/hooks/useMaterialGroups';
import FilterPanel from '../general/FilterPanel';
import { Group } from '@/types/material';

// Dynamic import Select to disable SSR (fix hydration mismatch issue)
const Select = dynamic(() => import('react-select'), { ssr: false });

interface MaterialFiltersProps {
  onFilter: (filters: Omit<MaterialQueryParams, 'search' | 'page' | 'size' | 'sort'>) => void;
  initialFilters?: Omit<MaterialQueryParams, 'search' | 'page' | 'size' | 'sort'>;
}

interface OptionType {
  label: string | number;
  value: string | number;
}

const toArray = (v: any) => Array.isArray(v) ? v : v !== undefined ? [v] : [];

const materialStatuses = ["ACTIVE", "INACTIVE", "DISCONTINUED", "PENDING"];

const MaterialFilters: React.FC<MaterialFiltersProps> = ({ onFilter, initialFilters = {} }) => {
  const groupList = useMaterialGroups();
  const groupOptions = groupList.map((g: Group) => ({ label: g.name, value: g.id }));
  const statusOptions = materialStatuses.map(s => ({ label: s, value: s }));

  const [selectedGroups, setSelectedGroups] = useState<OptionType[]>(
    toArray(initialFilters.groupMaterial).map(id => {
      const numId = Number(id);
      const found = groupList.find(g => g.id === numId);
      return found ? { label: found.name, value: found.id } : { label: String(id), value: numId };
    })
  );
  const [selectedStatuses, setSelectedStatuses] = useState<OptionType[]>(
    toArray(initialFilters.status).map(s => ({ label: s, value: s }))
  );

  const apply = () => {
    onFilter({
      groupMaterial: selectedGroups.length ? selectedGroups.map(g => g.value) : undefined,
      status: selectedStatuses.length ? selectedStatuses.map(s => s.value) : undefined,
    });
  };

  const clear = () => {
    setSelectedGroups([]);
    setSelectedStatuses([]);
    onFilter({});
  };

  return (
    <FilterPanel onApply={apply} onClear={clear}>
      <FormControl>
        <FormLabel>Group Material</FormLabel>
        <Select
          isMulti
          options={groupOptions}
          value={selectedGroups}
          onChange={(v) => setSelectedGroups(v as OptionType[])}
          placeholder="Select groups"
          />
      </FormControl>

      <FormControl>
        <FormLabel>Status</FormLabel>
          <Select
            isMulti
            options={statusOptions}
            value={selectedStatuses}
            onChange={(v) => setSelectedStatuses(v as OptionType[])}
            placeholder="Select statuses"
          />
      </FormControl>
    </FilterPanel>
  );
};

export default MaterialFilters;