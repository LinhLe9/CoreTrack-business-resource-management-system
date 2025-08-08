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
  const [inputValue, setInputValue] = useState(initialSearchTerm);

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
    setInputValue(inputValue);
    if (onSearchInputChange) {
      onSearchInputChange(inputValue);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && inputValue.trim()) {
      onSearch(inputValue.trim());
      setInputValue('');
      setSelectedOption(null);
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
    <Box width="400px" minWidth="300px" maxWidth="600px">
      <Select
        options={options}
        placeholder="Search by sale SKU, customer name, or status"
        onChange={handleChange}
        value={selectedOption}
        inputValue={inputValue}
        onInputChange={handleInputChange}
        onKeyDown={handleKeyDown}
        isClearable
        components={{ SingleValue: customSingleValue, Option: customOption }}
        filterOption={(option, inputValue) => {
          if (!option || !option.data) return false;
          
          const skuMatch = option.label?.toLowerCase().includes(inputValue.toLowerCase()) || false;
          const customerMatch = option.data.customerName?.toLowerCase().includes(inputValue.toLowerCase()) || false;
          const statusMatch = option.data.status?.toLowerCase().includes(inputValue.toLowerCase()) || false;

          return skuMatch || customerMatch || statusMatch;
        }}
        styles={{
          menu: (provided) => ({
            ...provided,
            zIndex: 9999,
          }),
          menuPortal: (provided) => ({
            ...provided,
            zIndex: 9999,
          }),
        }}
        menuPortalTarget={document.body}
        menuPosition="fixed"
      />
    </Box>
  );
};

export default SaleSearchBar; 