// components/sale/SaleSearchBar.tsx
import React, { useState } from 'react';
import Select from 'react-select';
import { useRouter } from 'next/navigation';
import {
  Box,
  Button,
  Flex,
} from '@chakra-ui/react';
import { SaleCardResponse } from '../../types/sale';

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectSale?: (saleId: number) => void;
  initialSearchTerm?: string;
  saleForAutocomplete: SaleCardResponse[];
  onSearchInputChange?: (inputValue: string) => void;
}

interface OptionType {
  value: number;
  label: string;
  sku: string;
  status: string;
  customerName: string;
}

const SaleSearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectSale,
  initialSearchTerm = '',
  saleForAutocomplete,
  onSearchInputChange,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);
  const router = useRouter();

  const options: OptionType[] = saleForAutocomplete
    .filter(item => item && item.id && item.sku) // Filter out null/undefined items
    .map((sale) => ({
      value: sale.id,
      label: sale.sku,
      sku: sale.sku,
      status: sale.status,
      customerName: sale.customerName,
    }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option && onSelectSale) {
      onSelectSale(option.value);
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
      <strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.customerName})</small>
    </div>
  );

  const customOption = ({ data, innerRef, innerProps }: any) => (
    <div ref={innerRef} {...innerProps} style={{ padding: '8px', cursor: 'pointer' }}>
      <div><strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.customerName})</small></div>
      <div style={{ fontSize: '0.8em', color: '#666' }}>
        Status: {data.status} | Customer: {data.customerName}
      </div>
    </div>
  );

  return (
    <Flex gap={2} align="center">
      <Box width="400px" minWidth="300px" maxWidth="600px">
        <Select
          instanceId="sale-search"
          options={options}
          placeholder="Search by sale SKU, customer name, or status"
          onChange={handleChange}
          value={selectedOption}
          isClearable
          components={{ SingleValue: customSingleValue, Option: customOption }}
          filterOption={(option, inputValue) => {
            if (!option || !option.data) return false;
            
            const skuMatch = option.label?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const customerMatch = option.data.customerName?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const statusMatch = option.data.status?.toLowerCase().includes(inputValue.toLowerCase()) || false;

            return skuMatch || customerMatch || statusMatch;
          }}
          onInputChange={handleInputChange}
        />
      </Box>
      <Button colorScheme="blue" onClick={handleSearch}>Search</Button>
    </Flex>
  );
};

export default SaleSearchBar; 