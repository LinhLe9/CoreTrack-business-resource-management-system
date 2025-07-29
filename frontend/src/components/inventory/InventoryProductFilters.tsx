// components/inventory/InventoryProductFilters.tsx
import React, { useState, useEffect } from 'react';
import { FormControl, FormLabel } from '@chakra-ui/react';
import dynamic from 'next/dynamic';
import { ProductInventoryFilterParams, EnumValue } from '../../types/productInventory';
import useProductGroups from '@/hooks/useProductGroups';
import FilterPanel from '../general/FilterPanel';
import { Group } from '@/types/product';
import { getInventoryStatuses } from '@/services/productInventoryService';

// Dynamic import Select to disable SSR (fix hydration mismatch issue)
const Select = dynamic(() => import('react-select'), { ssr: false });

interface InventoryProductFiltersProps {
  onFilter: (filters: ProductInventoryFilterParams) => void;
  initialFilters?: ProductInventoryFilterParams;
}

interface OptionType {
  label: string | number;
  value: string | number;
}

const toArray = (v: any) => Array.isArray(v) ? v : v !== undefined ? [v] : [];

const InventoryProductFilters: React.FC<InventoryProductFiltersProps> = ({ onFilter, initialFilters = {} }) => {
  const productGroups = useProductGroups();
  const [inventoryStatuses, setInventoryStatuses] = useState<EnumValue[]>([]);
  const [loading, setLoading] = useState(true);

  const groupOptions = productGroups.map((g: Group) => ({ label: g.name, value: g.id }));
  const statusOptions = inventoryStatuses.map(s => ({ label: s.displayName, value: s.value }));

  // Fetch inventory statuses
  useEffect(() => {
    const fetchInventoryStatuses = async () => {
      try {
        const statuses = await getInventoryStatuses();
        setInventoryStatuses(statuses);
      } catch (error) {
        console.error('Error fetching inventory statuses:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchInventoryStatuses();
  }, []);

  const [selectedGroups, setSelectedGroups] = useState<OptionType[]>(
    toArray(initialFilters.groupProducts).map(id => {
      const numId = Number(id);
      const found = productGroups.find(g => g.id === numId);
      return found ? { label: found.name, value: found.id } : { label: String(id), value: numId };
    })
  );
  const [selectedStatuses, setSelectedStatuses] = useState<OptionType[]>(
    toArray(initialFilters.inventoryStatus).map(s => ({ label: s, value: s }))
  );

  const apply = () => {
    onFilter({
      groupProducts: selectedGroups.length ? selectedGroups.map(g => String(g.value)) : undefined,
      inventoryStatus: selectedStatuses.length ? selectedStatuses.map(s => String(s.value)) : undefined,
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
        <FormLabel>Product Group</FormLabel>
        <Select
          isMulti
          options={groupOptions}
          value={selectedGroups}
          onChange={(v) => setSelectedGroups(v as OptionType[])}
          placeholder="Click to select product groups"
          isLoading={productGroups.length === 0}
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

      <FormControl>
        <FormLabel>Inventory Status</FormLabel>
        <Select
          isMulti
          options={statusOptions}
          value={selectedStatuses}
          onChange={(v) => setSelectedStatuses(v as OptionType[])}
          placeholder="Select inventory statuses"
          isLoading={loading}
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

export default InventoryProductFilters; 