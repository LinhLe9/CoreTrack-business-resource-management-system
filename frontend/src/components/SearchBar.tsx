// components/SearchBar.tsx
import React, { useState, useEffect, useRef } from 'react';
import {
  Input,
  Button,
  Flex,
  IconButton,
  InputGroup,
  InputRightElement,
  Box,
  ListItem,
  UnorderedList,
  Text,
} from '@chakra-ui/react';
import { SearchIcon, CloseIcon } from '@chakra-ui/icons';
import { Product, ProductAutoComplete} from '../types/product'; // Import Material type

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  onSelectProduct?: (productId: number) => void; // Dùng cho A1: điều hướng đến chi tiết
  initialSearchTerm?: string;
  productsForAutocomplete: ProductAutoComplete[]; // Danh sách vật liệu cho gợi ý
}

const SearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectProduct,
  initialSearchTerm = '',
  productsForAutocomplete,
}) => {
  const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const searchBarRef = useRef<HTMLDivElement>(null); // Ref cho component để xử lý click bên ngoài

  // Lọc gợi ý dựa trên searchTerm
  const filteredSuggestions = productsForAutocomplete.filter(
    (Product) =>
      Product.sku.toLowerCase().includes(searchTerm.toLowerCase()) ||
      Product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (Product.shortDescription &&
        Product.shortDescription.toLowerCase().includes(searchTerm.toLowerCase()))
  ).slice(0, 5); // limit 5 recommend 

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    setShowSuggestions(e.target.value.length > 0);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch(searchTerm);
    setShowSuggestions(false); // Ẩn gợi ý sau khi tìm kiếm
  };

  const handleClear = () => {
    setSearchTerm('');
    onSearch(''); // Kích hoạt tìm kiếm rỗng để xóa kết quả (A3)
    setShowSuggestions(false);
  };

  const handleSelectSuggestion = (productId: number) => {
    if (onSelectProduct) {
      onSelectProduct(productId); // Điều hướng đến chi tiết (A1)
    } else {
      // Nếu không có onSelectMaterial, có thể lọc danh sách để chỉ hiển thị vật liệu đó
      const selectedProduct = productsForAutocomplete.find(m => m.id === productId);
      if (selectedProduct) {
          setSearchTerm(selectedProduct.name); // Cập nhật search term
          onSearch(selectedProduct.name); // Hoặc một trường duy nhất như SKU
      }
    }
    setShowSuggestions(false);
  };

  // Ẩn gợi ý khi click bên ngoài SearchBar
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchBarRef.current && !searchBarRef.current.contains(event.target as Node)) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <Box position="relative" width="full" ref={searchBarRef}>
      <Flex as="form" onSubmit={handleSubmit} width="full">
        <InputGroup flex={1}>
          <Input
            placeholder="Search by SKU, Name, or Description..."
            value={searchTerm}
            onChange={handleChange}
            pr={searchTerm ? "4.5rem" : "0"} // Khoảng trống cho nút clear
          />
          {searchTerm && (
            <InputRightElement width="4.5rem">
              <IconButton
                aria-label="Clear search"
                icon={<CloseIcon />}
                size="sm"
                onClick={handleClear}
                mr={1} // Khoảng cách nhỏ với nút Search
              />
            </InputRightElement>
          )}
        </InputGroup>
        <Button type="submit" leftIcon={<SearchIcon />} ml={2} colorScheme="blue">
          Search
        </Button>
      </Flex>

      {showSuggestions && filteredSuggestions.length > 0 && (
        <Box
          position="absolute"
          top="100%"
          left={0}
          right={0}
          zIndex={10}
          bg="white"
          borderWidth="1px"
          borderColor="gray.200"
          borderRadius="md"
          mt={1}
          boxShadow="lg"
        >
          <UnorderedList listStyleType="none" m={0} p={0}>
            {filteredSuggestions.map((material) => (
              <ListItem
                key={material.id}
                p={2}
                _hover={{ bg: 'gray.100', cursor: 'pointer' }}
                onClick={() => handleSelectSuggestion(material.id)}
              >
                <Text fontWeight="bold">{material.name}</Text>
                <Text fontSize="sm" color="gray.600">{material.sku} - {material.shortDescription}</Text>
              </ListItem>
            ))}
          </UnorderedList>
        </Box>
      )}
    </Box>
  );
};

export default SearchBar;