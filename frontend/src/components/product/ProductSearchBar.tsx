// components/product/ProductSearchBar.tsx
import React, { useState } from 'react';
import Select from 'react-select';
import {
  Box,
  Button,
  Flex,
} from '@chakra-ui/react';
import { ProductAutoComplete } from '../../types/product';

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectProduct?: (productId: number) => void;
  initialSearchTerm?: string;
  productsForAutocomplete: ProductAutoComplete[];
}

interface OptionType {
  value: number;
  label: string;
  sku: string;
  description?: string;
}

const SearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectProduct,
  initialSearchTerm = '',
  productsForAutocomplete,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);

  const options: OptionType[] = productsForAutocomplete.map((product) => ({
    value: product.id,
    label: product.name,
    sku: product.sku,
    description: product.shortDescription,
  }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option) {
      if (onSelectProduct) {
        onSelectProduct(option.value);
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
      <strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.sku})</small>
    </div>
  );

  const customOption = ({ data, innerRef, innerProps }: any) => (
    <div ref={innerRef} {...innerProps} style={{ padding: '8px', cursor: 'pointer' }}>
      <div><strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.sku})</small></div>
      {data.description && <div style={{ fontSize: '0.8em', color: '#666' }}>{data.description}</div>}
    </div>
  );

  return (
    <Flex gap={2} align="center">
      <Box width="400px" minWidth="300px" maxWidth="600px">
        <Select
          options={options}
          placeholder="Search by name, SKU, or description"
          onChange={handleChange}
          value={selectedOption}
          isClearable
          components={{ SingleValue: customSingleValue, Option: customOption }}
          filterOption={(option, inputValue) => {
            const nameMatch = option.label.toLowerCase().includes(inputValue.toLowerCase());
            const skuMatch = option.data.sku.toLowerCase().includes(inputValue.toLowerCase());
            const descMatch = option.data.description?.toLowerCase().includes(inputValue.toLowerCase()) || false;

            return nameMatch || skuMatch || descMatch;
          }}
        />
      </Box>
      <Button colorScheme="blue" onClick={handleSearch}>Search</Button>
    </Flex>
  );
};

export default SearchBar;
