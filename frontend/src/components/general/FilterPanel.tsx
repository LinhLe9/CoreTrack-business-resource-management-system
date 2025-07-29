// components/FilterPanel.tsx
import React from 'react';
import { Box, HStack, Button, Flex } from '@chakra-ui/react';

interface FilterPanelProps {
  onApply: () => void;
  onClear: () => void;
  children: React.ReactNode;
  title?: string;
}

const FilterPanel: React.FC<FilterPanelProps> = ({ onApply, onClear, children, title = "Filters" }) => {
  return (
    <Flex justify="center" width="100%">
      <Box p={4} borderWidth="1px" borderRadius="lg" bg="gray.50" boxShadow="sm" minW="600px">
        <HStack spacing={4} align="center" justify="center">
          {children}
          <Button colorScheme="green" onClick={onApply}>Apply</Button>
          <Button variant="outline" onClick={onClear}>Clear</Button>
        </HStack>
      </Box>
    </Flex>
  );
};

export default FilterPanel;