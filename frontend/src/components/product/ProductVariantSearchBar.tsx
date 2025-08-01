// components/product/ProductVariantSearchBar.tsx
import React, { useState } from 'react';
import Select from 'react-select';
import {
  Box,
  Button,
  Flex,
} from '@chakra-ui/react';
import { ProductVariantAutoComplete } from '../../types/product';

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectProductVariant?: (variantId: number) => void;
  initialSearchTerm?: string;
  productVariantsForAutocomplete: ProductVariantAutoComplete[];
}

interface OptionType {
  value: number;
  label: string;
  productSku: string;
  variantSku: string;
  variantName: string;
  productGroup?: string;
}

const ProductVariantSearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectProductVariant,
  initialSearchTerm = '',
  productVariantsForAutocomplete,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);
  const [inputValue, setInputValue] = useState('');

  const options: OptionType[] = productVariantsForAutocomplete.map((variant) => ({
    value: variant.variantId,
    label: variant.productName,
    productSku: variant.productSku,
    variantSku: variant.variantSku,
    variantName: variant.variantName,
    productGroup: variant.productGroup,
  }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option) {
      if (onSelectProductVariant) {
        onSelectProductVariant(option.value);
      } else {
        onSearch(option.label); // fallback search
      }
    }
  };

  const handleInputChange = (newValue: string) => {
    setInputValue(newValue);
    if (newValue.length >= 2) {
      onSearch(newValue);
    }
  };

  const handleSearch = () => {
    if (selectedOption) {
      onSearch(selectedOption.label);
    } else if (inputValue) {
      onSearch(inputValue);
    }
  };

  const customSingleValue = ({ data }: any) => (
    <div>
      <strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.variantSku})</small>
    </div>
  );

  const customOption = ({ data, innerRef, innerProps }: any) => (
    <div ref={innerRef} {...innerProps} style={{ padding: '8px', cursor: 'pointer' }}>
      <div><strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.variantSku})</small></div>
      <div style={{ fontSize: '0.8em', color: '#666' }}>
        Product: {data.productSku} | Variant: {data.variantName}
      </div>
      {data.productGroup && (
        <div style={{ fontSize: '0.7em', color: '#888' }}>
          Group: {data.productGroup}
        </div>
      )}
    </div>
  );

  return (
    <Flex gap={2} align="center">
      <Box width="400px" minWidth="300px" maxWidth="600px">
        <Select
          options={options}
          placeholder="Search by product name, SKU, or variant"
          onChange={handleChange}
          value={selectedOption}
          isClearable
          isSearchable
          menuIsOpen={productVariantsForAutocomplete.length > 0}
          components={{ SingleValue: customSingleValue, Option: customOption }}
          filterOption={(option, inputValue) => {
            const nameMatch = option.label.toLowerCase().includes(inputValue.toLowerCase());
            const productSkuMatch = option.data.productSku.toLowerCase().includes(inputValue.toLowerCase());
            const variantSkuMatch = option.data.variantSku.toLowerCase().includes(inputValue.toLowerCase());
            const variantNameMatch = option.data.variantName.toLowerCase().includes(inputValue.toLowerCase());
            const groupMatch = option.data.productGroup?.toLowerCase().includes(inputValue.toLowerCase()) || false;

            return nameMatch || productSkuMatch || variantSkuMatch || variantNameMatch || groupMatch;
          }}
          onInputChange={handleInputChange}
        />
      </Box>
      <Button colorScheme="blue" onClick={handleSearch}>Search</Button>
    </Flex>
  );
};

export default ProductVariantSearchBar; 