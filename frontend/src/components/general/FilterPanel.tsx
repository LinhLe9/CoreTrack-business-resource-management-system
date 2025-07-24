// components/FilterPanel.tsx
import React from 'react';
import { Box, Heading, VStack, Button } from '@chakra-ui/react';

interface FilterPanelProps {
  onApply: () => void;
  onClear: () => void;
  children: React.ReactNode;
  title?: string;
}

const FilterPanel: React.FC<FilterPanelProps> = ({ onApply, onClear, children, title = "Filters" }) => {
  return (
    <Box p={4} borderWidth="1px" borderRadius="lg" bg="gray.50" boxShadow="sm" width="300px">
      <Heading as="h3" size="md" mb={4} textAlign="center">{title}</Heading>
      <VStack spacing={4} align="stretch">
        {children}
        <Button colorScheme="green" onClick={onApply}>Apply Filters</Button>
        <Button variant="outline" onClick={onClear}>Clear Filters</Button>
      </VStack>
    </Box>
  );
};

export default FilterPanel;