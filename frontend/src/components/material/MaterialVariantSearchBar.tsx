'use client';

import React, { useState } from 'react';
import Select from 'react-select';
import {
  Box,
  Button,
  Flex,
} from '@chakra-ui/react';
import { MaterialVariantAutoComplete } from '../../types/material';

interface MaterialVariantSearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectMaterialVariant?: (variantId: number) => void;
  materialVariantsForAutocomplete: MaterialVariantAutoComplete[];
}

interface OptionType {
  value: number;
  label: string;
  materialSku: string;
  variantSku: string;
  variantName: string;
  materialGroup?: string;
}

const MaterialVariantSearchBar: React.FC<MaterialVariantSearchBarProps> = ({
  onSearch,
  onSelectMaterialVariant,
  materialVariantsForAutocomplete,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);

  const options: OptionType[] = materialVariantsForAutocomplete.map((variant) => ({
    value: variant.variantId,
    label: variant.materialName,
    materialSku: variant.materialSku,
    variantSku: variant.variantSku,
    variantName: variant.variantName,
    materialGroup: variant.materialGroup,
  }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option) {
      if (onSelectMaterialVariant) {
        onSelectMaterialVariant(option.value);
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
      <strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.variantSku})</small>
    </div>
  );

  const customOption = ({ data, innerRef, innerProps }: any) => (
    <div ref={innerRef} {...innerProps} style={{ padding: '8px', cursor: 'pointer' }}>
      <div><strong>{data.label}</strong> <small style={{ color: 'gray' }}>({data.variantSku})</small></div>
      <div style={{ fontSize: '0.8em', color: '#666' }}>
        Material: {data.materialSku} | Variant: {data.variantName}
      </div>
      {data.materialGroup && (
        <div style={{ fontSize: '0.7em', color: '#888' }}>
          Group: {data.materialGroup}
        </div>
      )}
    </div>
  );

  return (
    <Flex gap={2} align="center">
      <Box width="400px" minWidth="300px" maxWidth="600px">
        <Select
          options={options}
          placeholder="Search by material name, SKU, or variant"
          onChange={handleChange}
          value={selectedOption}
          isClearable
          components={{ SingleValue: customSingleValue, Option: customOption }}
          filterOption={(option, inputValue) => {
            const nameMatch = option.label.toLowerCase().includes(inputValue.toLowerCase());
            const materialSkuMatch = option.data.materialSku.toLowerCase().includes(inputValue.toLowerCase());
            const variantSkuMatch = option.data.variantSku.toLowerCase().includes(inputValue.toLowerCase());
            const variantNameMatch = option.data.variantName.toLowerCase().includes(inputValue.toLowerCase());
            const groupMatch = option.data.materialGroup?.toLowerCase().includes(inputValue.toLowerCase()) || false;

            return nameMatch || materialSkuMatch || variantSkuMatch || variantNameMatch || groupMatch;
          }}
        />
      </Box>
      <Button colorScheme="blue" onClick={handleSearch}>Search</Button>
    </Flex>
  );
};

export default MaterialVariantSearchBar; 