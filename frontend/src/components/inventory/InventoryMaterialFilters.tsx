// components/inventory/InventoryMaterialFilters.tsx
import React, { useState, useEffect } from 'react';
import Select from 'react-select';
import { FormControl, FormLabel } from '@chakra-ui/react';
import FilterPanel from '../general/FilterPanel';
import useMaterialGroups from '../../hooks/useMaterialGroups';
import { getInventoryStatuses } from '../../services/productInventoryService';
import { EnumValue } from '../../types/productInventory';
import { MaterialInventoryFilterParams } from '../../types/materialInventory';

interface InventoryMaterialFiltersProps {
  onFilter: (filters: MaterialInventoryFilterParams) => void;
  initialFilters?: MaterialInventoryFilterParams;
}

interface OptionType {
  label: string | number;
  value: string | number;
}

interface MaterialGroup {
  id: number;
  name: string;
}

const toArray = (v: any) => Array.isArray(v) ? v : v !== undefined ? [v] : [];

const InventoryMaterialFilters: React.FC<InventoryMaterialFiltersProps> = ({ onFilter, initialFilters = {} }) => {
  const { materialGroups, loading: materialGroupsLoading } = useMaterialGroups();
  const [inventoryStatuses, setInventoryStatuses] = useState<EnumValue[]>([]);
  const [loading, setLoading] = useState(true);

  const groupOptions = materialGroups.map((g: MaterialGroup) => ({ label: g.name, value: g.id }));
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
    toArray(initialFilters.groupMaterials).map(id => {
      const numId = Number(id);
      const found = materialGroups.find((g: MaterialGroup) => g.id === numId);
      return found ? { label: found.name, value: found.id } : { label: String(id), value: numId };
    })
  );
  const [selectedStatuses, setSelectedStatuses] = useState<OptionType[]>(
    toArray(initialFilters.inventoryStatus).map(s => ({ label: s, value: s }))
  );

  const apply = () => {
    onFilter({
      groupMaterials: selectedGroups.length ? selectedGroups.map(g => String(g.value)) : undefined,
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
        <FormLabel>Material Group</FormLabel>
        <Select
          isMulti
          options={groupOptions}
          value={selectedGroups}
          onChange={(v) => setSelectedGroups(v as OptionType[])}
          placeholder="Click to select material groups"
          isLoading={materialGroupsLoading}
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

export default InventoryMaterialFilters; 