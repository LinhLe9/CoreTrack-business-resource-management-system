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
  onSearch?: (searchTerm: string) => void;
  onSelectProductVariant?: (variantId: number) => void;
  onSelectProductVariantObject?: (variant: ProductVariantAutoComplete) => void;
  initialSearchTerm?: string;
  productVariantsForAutocomplete: ProductVariantAutoComplete[];
  selectedProductVariant?: ProductVariantAutoComplete | null;
}

interface OptionType {
  value: number;
  label: string;
  productSku: string;
  variantSku: string;
  variantName: string;
  productGroup?: string;
  data: ProductVariantAutoComplete;
}

const ProductVariantSearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectProductVariant,
  onSelectProductVariantObject,
  initialSearchTerm = '',
  productVariantsForAutocomplete,
  selectedProductVariant,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);
  const [inputValue, setInputValue] = useState('');
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const options: OptionType[] = productVariantsForAutocomplete.map((variant) => ({
    value: variant.variantId,
    label: variant.productName,
    productSku: variant.productSku,
    variantSku: variant.variantSku,
    variantName: variant.variantName,
    productGroup: variant.productGroup,
    data: variant,
  }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    setIsMenuOpen(false); // Close menu when option is selected
    if (option) {
      if (onSelectProductVariantObject) {
        onSelectProductVariantObject(option.data);
      } else if (onSelectProductVariant) {
        onSelectProductVariant(option.value);
      } else if (onSearch) {
        onSearch(option.label); // fallback search
      }
    }
  };

  const handleInputChange = (newValue: string) => {
    setInputValue(newValue);
    if (newValue.length >= 2 && onSearch) {
      onSearch(newValue);
      setIsMenuOpen(true); // Open menu when user types
    } else {
      setIsMenuOpen(false); // Close menu when input is too short
    }
  };

  const handleSearch = () => {
    if (selectedOption && onSearch) {
      onSearch(selectedOption.label);
    } else if (inputValue && onSearch) {
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
          components={{ SingleValue: customSingleValue, Option: customOption }}
          filterOption={(option, inputValue) => {
            if (!option || !option.data) return false;
            
            const nameMatch = option.label?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const skuMatch = option.data.productSku?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const variantSkuMatch = option.data.variantSku?.toLowerCase().includes(inputValue.toLowerCase()) || false;
            const variantNameMatch = option.data.variantName?.toLowerCase().includes(inputValue.toLowerCase()) || false;

            return nameMatch || skuMatch || variantSkuMatch || variantNameMatch;
          }}
          onInputChange={handleInputChange}
          menuIsOpen={isMenuOpen}
          onMenuOpen={() => setIsMenuOpen(true)}
          onMenuClose={() => setIsMenuOpen(false)}
        />
      </Box>
      {onSearch && (
        <Button colorScheme="blue" onClick={handleSearch}>Search</Button>
      )}
    </Flex>
  );
};

export default ProductVariantSearchBar; 