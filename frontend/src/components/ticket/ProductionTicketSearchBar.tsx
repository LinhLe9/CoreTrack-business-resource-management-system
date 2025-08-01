'use client';

import React, { useState, useRef, useEffect } from 'react';
import {
  Box,
  Input,
  VStack,
  HStack,
  Text,
  IconButton,
  useOutsideClick,
  InputGroup,
  InputLeftElement,
} from '@chakra-ui/react';
import { SearchIcon, CloseIcon } from '@chakra-ui/icons';
import { ProductionTicketCardResponse } from '../../types/productionTicket';

interface ProductionTicketSearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectFromAutocomplete: (ticketId: number) => void;
  autocompleteResults: ProductionTicketCardResponse[];
  onSearchInputChange: (inputValue: string) => void;
}

const ProductionTicketSearchBar: React.FC<ProductionTicketSearchBarProps> = ({
  onSearch,
  onSelectFromAutocomplete,
  autocompleteResults,
  onSearchInputChange,
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useOutsideClick({
    ref: dropdownRef,
    handler: () => {
      setIsDropdownOpen(false);
      setSelectedIndex(-1);
    },
  });

  useEffect(() => {
    if (searchTerm.length >= 2) {
      onSearchInputChange(searchTerm);
      setIsDropdownOpen(true);
    } else {
      setIsDropdownOpen(false);
      setSelectedIndex(-1);
    }
  }, [searchTerm, onSearchInputChange]);

  const handleInputChange = (value: string) => {
    setSearchTerm(value);
    setSelectedIndex(-1);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex(prev => 
        prev < autocompleteResults.length - 1 ? prev + 1 : prev
      );
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex(prev => prev > 0 ? prev - 1 : -1);
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (selectedIndex >= 0 && selectedIndex < autocompleteResults.length) {
        handleSelectItem(autocompleteResults[selectedIndex]);
      } else {
        handleSearch();
      }
    } else if (e.key === 'Escape') {
      setIsDropdownOpen(false);
      setSelectedIndex(-1);
      inputRef.current?.blur();
    }
  };

  const handleSearch = () => {
    onSearch(searchTerm);
    setIsDropdownOpen(false);
    setSelectedIndex(-1);
  };

  const handleSelectItem = (item: ProductionTicketCardResponse) => {
    onSelectFromAutocomplete(item.id);
    setSearchTerm('');
    setIsDropdownOpen(false);
    setSelectedIndex(-1);
  };

  const handleClear = () => {
    setSearchTerm('');
    setIsDropdownOpen(false);
    setSelectedIndex(-1);
    onSearch('');
    inputRef.current?.focus();
  };

  return (
    <Box position="relative" width="100%">
      <InputGroup>
        <InputLeftElement pointerEvents="none">
          <SearchIcon color="gray.300" />
        </InputLeftElement>
        <Input
          ref={inputRef}
          placeholder="Search production tickets by name, ID, or product variant..."
          value={searchTerm}
          onChange={(e) => handleInputChange(e.target.value)}
          onKeyDown={handleKeyDown}
          onFocus={() => {
            if (searchTerm.length >= 2 && autocompleteResults.length > 0) {
              setIsDropdownOpen(true);
            }
          }}
          pr="40px"
        />
        {searchTerm && (
          <IconButton
            aria-label="Clear search"
            icon={<CloseIcon />}
            size="sm"
            variant="ghost"
            position="absolute"
            right="1"
            top="50%"
            transform="translateY(-50%)"
            zIndex={2}
            onClick={handleClear}
          />
        )}
      </InputGroup>

      {/* Autocomplete Dropdown */}
      {isDropdownOpen && autocompleteResults.length > 0 && (
        <Box
          ref={dropdownRef}
          position="absolute"
          top="100%"
          left={0}
          right={0}
          bg="white"
          border="1px"
          borderColor="gray.200"
          borderRadius="md"
          boxShadow="lg"
          zIndex={1000}
          maxH="300px"
          overflowY="auto"
        >
          <VStack spacing={0} align="stretch">
            {autocompleteResults.map((item, index) => (
              <Box
                key={item.id}
                px={4}
                py={3}
                cursor="pointer"
                bg={selectedIndex === index ? "blue.50" : "transparent"}
                _hover={{ bg: "gray.50" }}
                onClick={() => handleSelectItem(item)}
                borderBottom="1px"
                borderColor="gray.100"
              >
                <HStack justify="space-between">
                  <VStack align="start" spacing={1} flex={1}>
                    <Text fontWeight="medium" fontSize="sm">
                      {item.name}
                    </Text>
                    <Text fontSize="xs" color="gray.600">
                      ID: {item.id} | Status: {item.status}
                    </Text>
                    <Text fontSize="xs" color="gray.500">
                      Created: {new Date(item.createdAt).toLocaleDateString()}
                    </Text>
                  </VStack>
                </HStack>
              </Box>
            ))}
          </VStack>
        </Box>
      )}

      {/* No results message */}
      {isDropdownOpen && searchTerm.length >= 2 && autocompleteResults.length === 0 && (
        <Box
          position="absolute"
          top="100%"
          left={0}
          right={0}
          bg="white"
          border="1px"
          borderColor="gray.200"
          borderRadius="md"
          boxShadow="lg"
          zIndex={1000}
          p={4}
        >
          <Text color="gray.500" textAlign="center">
            No production tickets found
          </Text>
        </Box>
      )}
    </Box>
  );
};

export default ProductionTicketSearchBar; 