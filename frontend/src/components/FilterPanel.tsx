// components/FilterPanel.tsx
import React, { useState } from 'react';
import {
  Box,
  Heading,
  FormControl,
  FormLabel,
  Select,
  Button,
  VStack,
  CheckboxGroup,
  Checkbox,
  Stack,
} from '@chakra-ui/react';
import { ProductQueryParams } from '../types/product';
import useProductGroups from '@/hooks/useProductGroups';

interface FilterPanelProps {
  onFilter: (filters: Omit<ProductQueryParams, 'search' | 'page' | 'size' | 'sort'>) => void;
  initialFilters?: Omit<ProductQueryParams, 'search' | 'page' | 'size' | 'sort'>;
}
const productStatuses = ["Active", "Inactive", "Discontinued", "Pending"]; 

const FilterPanel: React.FC<FilterPanelProps> = ({ onFilter, initialFilters = {} }) => {
  const productGroups = useProductGroups(); 
  const [selectedGroups, setSelectedGroups] = useState<string[]>(
    Array.isArray(initialFilters.groupProduct) ? initialFilters.groupProduct :
    (initialFilters.groupProduct ? [initialFilters.groupProduct] : [])
  );
  const [selectedStatuses, setSelectedStatuses] = useState<string[]>(
    Array.isArray(initialFilters.status) ? initialFilters.status :
    (initialFilters.status ? [initialFilters.status] : [])
  );

  const applyFilters = () => {
    onFilter({
      groupProduct: selectedGroups.length > 0 ? selectedGroups : undefined,
      status: selectedStatuses.length > 0 ? selectedStatuses : undefined,
    });
  };

  const clearFilters = () => {
    setSelectedGroups([]);
    setSelectedStatuses([]);
    onFilter({}); // Xóa tất cả các bộ lọc (A3)
  };

  return (
    <Box p={4} borderWidth="1px" borderRadius="lg" bg="gray.50" boxShadow="sm" width="300px">
      <Heading as="h3" size="md" mb={4} textAlign="center">
        Filters
      </Heading>
      <VStack spacing={4} align="stretch">
        <FormControl>
          <FormLabel>Group Product:</FormLabel>
          <CheckboxGroup value={selectedGroups} onChange={(value: string[]) => setSelectedGroups(value)}>
            <Stack direction="column">
              {productGroups.map((group) => (
                <Checkbox key={group} value={group}>
                  {group}
                </Checkbox>
              ))}
            </Stack>
          </CheckboxGroup>
          {/* Hoặc dùng Select nếu bạn chỉ muốn chọn 1 */}
          {/* <Select value={selectedGroup || ''} onChange={(e) => setSelectedGroup(e.target.value)}>
            <option value="">All Groups</option>
            {materialGroups.map(group => (
              <option key={group} value={group}>{group}</option>
            ))}
          </Select> */}
        </FormControl>

        <FormControl>
          <FormLabel>Status:</FormLabel>
          <CheckboxGroup value={selectedStatuses} onChange={(value: string[]) => setSelectedStatuses(value)}>
            <Stack direction="column">
              {productStatuses.map((status) => (
                <Checkbox key={status} value={status}>
                  {status}
                </Checkbox>
              ))}
            </Stack>
          </CheckboxGroup>
        </FormControl>

        <Button colorScheme="green" onClick={applyFilters}>
          Apply Filters
        </Button>
        <Button variant="outline" onClick={clearFilters}>
          Clear Filters
        </Button>
      </VStack>
    </Box>
  );
};

export default FilterPanel;