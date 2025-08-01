// components/inventory/InventoryMaterialSearchBar.tsx
import React, { useState } from 'react';
import Select from 'react-select';
import { useRouter } from 'next/navigation';
import {
  Box,
  Button,
  Flex,
} from '@chakra-ui/react';
import { AllSearchInventoryResponse } from '../../types/productInventory';

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectMaterialInventory?: (variantId: number) => void;
  initialSearchTerm?: string;
  materialInventoryForAutocomplete: AllSearchInventoryResponse[];
  onSearchInputChange?: (inputValue: string) => void;
}

interface OptionType {
  value: number;
  label: string;
  sku: string;
  inventoryStatus: string;
  currentStock: string;
  imageUrl?: string;
}

const InventoryMaterialSearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectMaterialInventory,
  initialSearchTerm = '',
  materialInventoryForAutocomplete,
  onSearchInputChange,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);
  const router = useRouter();

  const options: OptionType[] = materialInventoryForAutocomplete
    .filter(item => item && item.id && item.name) // Filter out null/undefined items
    .map((materialInventory) => ({
      value: materialInventory.id,
      label: materialInventory.name,
      sku: materialInventory.sku || '',
      inventoryStatus: materialInventory.inventoryStatus || '',
      currentStock: materialInventory.currentStock || '',
      imageUrl: materialInventory.imageUrl,
    }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option) {
      // Navigate to material inventory detail page
      router.push(`/material-inventory/${option.value}`);
    }
  };

  const handleInputChange = (inputValue: string) => {
    if (onSearchInputChange) {
      onSearchInputChange(inputValue);
    }
  };

  const handleSearch = () => {
    if (selectedOption) {
      onSearch(selectedOption.label);
    }
  };

  const customSingleValue = ({ data }: any) => (
    <div>
      <strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.sku})</small>
    </div>
  );

  const customOption = ({ data, innerRef, innerProps }: any) => (
    <div ref={innerRef} {...innerProps} style={{ padding: '8px', cursor: 'pointer' }}>
      <div><strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.sku})</small></div>
      <div style={{ fontSize: '0.8em', color: '#666' }}>
        Stock: {data.currentStock} | Status: {data.inventoryStatus}
      </div>
    </div>
  );

  return (
    <Flex gap={2} align="center">
      <Box width="400px" minWidth="300px" maxWidth="600px">
        <Select
          instanceId="inventory-material-search"
          options={options}
          placeholder="Search by material name, SKU, or variant"
          onChange={handleChange}
          value={selectedOption}
          isClearable
          components={{ SingleValue: customSingleValue, Option: customOption }}
          filterOption={(option, inputValue) => {
            if (!option || !option.data) return false;
            
            const nameMatch = option.label?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const skuMatch = option.data.sku?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const statusMatch = option.data.inventoryStatus?.toLowerCase().includes(inputValue.toLowerCase()) || false;

            return nameMatch || skuMatch || statusMatch;
          }}
          onInputChange={handleInputChange}
        />
      </Box>
      <Button colorScheme="blue" onClick={handleSearch}>Search</Button>
    </Flex>
  );
};

export default InventoryMaterialSearchBar; 