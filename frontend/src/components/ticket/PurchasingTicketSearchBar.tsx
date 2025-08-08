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
import { PurchasingTicketCardResponse } from '../../types/purchasingTicket';

interface PurchasingTicketSearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectFromAutocomplete: (ticketId: number) => void;
  autocompleteResults: PurchasingTicketCardResponse[];
  onSearchInputChange: (inputValue: string) => void;
}

const PurchasingTicketSearchBar: React.FC<PurchasingTicketSearchBarProps> = ({
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

  const handleSelectItem = (item: PurchasingTicketCardResponse) => {
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
  };

  return (
    <Box position="relative" w="full">
      <InputGroup>
        <InputLeftElement pointerEvents="none">
          <SearchIcon color="gray.300" />
        </InputLeftElement>
        <Input
          ref={inputRef}
          placeholder="Search purchasing tickets..."
          value={searchTerm}
          onChange={(e) => handleInputChange(e.target.value)}
          onKeyDown={handleKeyDown}
          pr={searchTerm ? '8' : '4'}
        />
        {searchTerm && (
          <IconButton
            position="absolute"
            right="1"
            top="1"
            size="sm"
            aria-label="Clear search"
            icon={<CloseIcon />}
            onClick={handleClear}
            variant="ghost"
            zIndex={1}
          />
        )}
      </InputGroup>

      {/* Autocomplete Dropdown */}
      {isDropdownOpen && autocompleteResults.length > 0 && (
        <Box
          ref={dropdownRef}
          position="absolute"
          top="100%"
          left="0"
          right="0"
          bg="white"
          border="1px"
          borderColor="gray.200"
          borderRadius="md"
          shadow="lg"
          zIndex={9999}
          maxH="300px"
          overflowY="auto"
        >
          <VStack spacing={0} align="stretch">
            {autocompleteResults.map((item, index) => (
              <Box
                key={item.id}
                p={3}
                cursor="pointer"
                bg={selectedIndex === index ? 'blue.50' : 'transparent'}
                _hover={{ bg: 'blue.50' }}
                onClick={() => handleSelectItem(item)}
                borderBottom="1px"
                borderColor="gray.100"
              >
                <VStack align="start" spacing={1}>
                  <HStack justify="space-between" w="full">
                    <Text fontWeight="medium" fontSize="sm">
                      {item.name}
                    </Text>
                    <Text fontSize="xs" color="gray.500">
                      ID: {item.id}
                    </Text>
                  </HStack>
                  <HStack justify="space-between" w="full">
                    <Text fontSize="xs" color="gray.600">
                      Created: {new Date(item.createdAt).toLocaleDateString()}
                    </Text>
                    <Text fontSize="xs" color="gray.600">
                      Status: {item.status}
                    </Text>
                  </HStack>
                </VStack>
              </Box>
            ))}
          </VStack>
        </Box>
      )}

      {/* No Results */}
      {isDropdownOpen && autocompleteResults.length === 0 && searchTerm.length >= 2 && (
        <Box
          position="absolute"
          top="100%"
          left="0"
          right="0"
          bg="white"
          border="1px"
          borderColor="gray.200"
          borderRadius="md"
          shadow="lg"
          zIndex={9999}
          p={3}
        >
          <Text fontSize="sm" color="gray.500" textAlign="center">
            No purchasing tickets found
          </Text>
        </Box>
      )}
    </Box>
  );
};

export default PurchasingTicketSearchBar; 