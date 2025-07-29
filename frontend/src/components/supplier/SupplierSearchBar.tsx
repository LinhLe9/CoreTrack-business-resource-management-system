// components/supplier/SupplierSearchBar.tsx
import React, { useState } from 'react';
import Select from 'react-select';
import {
  Box,
  Button,
  Flex,
} from '@chakra-ui/react';
import { SupplierAutoComplete } from '../../types/supplier';

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectSupplier?: (supplierId: number) => void;
  initialSearchTerm?: string;
  SuppliersForAutocomplete?: SupplierAutoComplete[];
}

interface OptionType {
  value: number;
  label: string;
  address?: string;
  country?: string;
  contactPerson?: string;
  email?: string;
  phone?: string;
}

const SearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectSupplier,
  initialSearchTerm = '',
  SuppliersForAutocomplete = [],
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);

  const options: OptionType[] = (SuppliersForAutocomplete || []).map((supplier) => ({
    value: supplier.id,
    label: supplier.name,
    address: supplier.address,
    country: supplier.country,
    contactPerson: supplier.contactPerson,
    email: supplier.email,
    phone: supplier.phone,
  }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option) {
      if (onSelectSupplier) {
        onSelectSupplier(option.value);
      } else {
        onSearch(option.label); // fallback search
      }
    }
  };

  const handleSearch = () => {
    if (selectedOption) {
      onSearch(selectedOption.label);
    }
  };

  const customSingleValue = ({ data }: any) => (
    <div>
      <strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.address || 'No address'}, {data.country || 'No country'})</small>
    </div>
  );

  const customOption = ({ data, innerRef, innerProps }: any) => (
    <div ref={innerRef} {...innerProps} style={{ padding: '8px', cursor: 'pointer' }}>
      <div><strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.address || 'No address'}, {data.country || 'No country'})</small></div>
    </div>
  );

  return (
    <Flex gap={2}>
      <Box flex="1">
        <Select
          options={options}
          placeholder="Search by name, phone, email, address, contact person"
          onChange={handleChange}
          value={selectedOption}
          isClearable
          components={{ SingleValue: customSingleValue, Option: customOption }}
          filterOption={(option, inputValue) => {
            const nameMatch = option.label.toLowerCase().includes(inputValue.toLowerCase());
            const addressMatch = option.data.address?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const countryMatch = option.data.country?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const contactNameMatch = option.data.contactPerson?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const emailMatch = option.data.email?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const phoneMatch = option.data.phone?.toLowerCase().includes(inputValue.toLowerCase()) || false;

            return nameMatch || addressMatch || countryMatch || contactNameMatch || emailMatch || phoneMatch;
          }}
        />
      </Box>
      <Button colorScheme="blue" onClick={handleSearch}>Search</Button>
    </Flex>
  );
};

export default SearchBar;
