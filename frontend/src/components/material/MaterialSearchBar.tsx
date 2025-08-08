// components/SearchBar.tsx
import React, { useState } from 'react';
import Select from 'react-select';
import {
  Box,
} from '@chakra-ui/react';
import { MaterialAutoComplete } from '../../types/material';

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectMaterial?: (materialId: number) => void;
  initialSearchTerm?: string;
  materialsForAutocomplete: MaterialAutoComplete[];
}

interface OptionType {
  value: number;
  label: string;
  sku: string;
  description?: string;
}

const SearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectMaterial,
  initialSearchTerm = '',
  materialsForAutocomplete,
}) => {
  const [selectedOption, setSelectedOption] = useState<OptionType | null>(null);
  const [inputValue, setInputValue] = useState(initialSearchTerm);

  const options: OptionType[] = materialsForAutocomplete.map((material) => ({
    value: material.id,
    label: material.name,
    sku: material.sku,
    description: material.shortDes,
  }));

  const handleChange = (option: OptionType | null) => {
    setSelectedOption(option);
    if (option) {
      if (onSelectMaterial) {
        onSelectMaterial(option.value);
      } else {
        onSearch(option.label); // fallback search
      }
    }
  };

  const handleInputChange = (newValue: string) => {
    setInputValue(newValue);
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
    <Box width="400px" minWidth="300px" maxWidth="600px">
      <Select
        options={options}
        placeholder="Search by name, SKU, or description"
        onChange={handleChange}
        value={selectedOption}
        inputValue={inputValue}
        onInputChange={handleInputChange}
        onKeyDown={handleKeyDown}
        isClearable
        components={{ SingleValue: customSingleValue, Option: customOption }}
        filterOption={(option, inputValue) => {
          const nameMatch = option.label.toLowerCase().includes(inputValue.toLowerCase());
          const skuMatch = option.data.sku.toLowerCase().includes(inputValue.toLowerCase());
          const descMatch = option.data.description?.toLowerCase().includes(inputValue.toLowerCase()) || false;

          return nameMatch || skuMatch || descMatch;
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

export default SearchBar;
