// components/inventory/InventoryProductSearchBar.tsx
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
  onSelectProductInventory?: (variantId: number) => void;
  initialSearchTerm?: string;
  productInventoryForAutocomplete: AllSearchInventoryResponse[];
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

const InventoryProductSearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectProductInventory,
  initialSearchTerm = '',
  productInventoryForAutocomplete,
  onSearchInputChange,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);
  const router = useRouter();

  const options: OptionType[] = productInventoryForAutocomplete
    .filter(item => item && item.id && item.name) // Filter out null/undefined items
    .map((productInventory) => ({
      value: productInventory.id,
      label: productInventory.name,
      sku: productInventory.sku || '',
      inventoryStatus: productInventory.inventoryStatus || '',
      currentStock: productInventory.currentStock || '',
      imageUrl: productInventory.imageUrl,
    }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option) {
      // Navigate to product inventory detail page
      router.push(`/product-inventory/${option.value}`);
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
          instanceId="inventory-product-search"
          options={options}
          placeholder="Search by product name, SKU, or variant"
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

export default InventoryProductSearchBar; 