import { Input, List, ListItem, Box, Text } from '@chakra-ui/react';
import { useState, useEffect } from 'react';
import api from '@/lib/axios';

import {Group} from '@/types/product'

export default function SearchProductGroup({
  onSelect,
  value,
}: {
  onSelect: (group: Group) => void;
  value?: string;
}) {
  const [allGroups, setAllGroups] = useState<Group[]>([]);
  const [query, setQuery] = useState(value || '');
  const [filtered, setFiltered] = useState<Group[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isFocused, setIsFocused] = useState(false);

  useEffect(() => {
    const fetchGroups = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await api.get('/products/product-groups'); 
        setAllGroups(res.data);
      } catch (err: any) {
        console.error('Error fetching product groups:', err);
        setError(err.response?.data?.message || 'Failed to load product groups');
        // Don't set groups to empty array to preserve previous data
      } finally {
        setLoading(false);
      }
    };
    fetchGroups();
  }, []);

  useEffect(() => {
    const q = query.toLowerCase();
    setFiltered(allGroups.filter(group => group.name.toLowerCase().includes(q)));
  }, [query, allGroups]);

  // Update query when value prop changes
  useEffect(() => {
    if (value !== undefined) {
      setQuery(value);
    }
  }, [value]);

  return (
    <Box position="relative">
      <Input
        placeholder="Search product groups..."
        value={query}
        onChange={e => setQuery(e.target.value)}
        onFocus={() => setIsFocused(true)}
        onBlur={() => {
          // Delay hiding to allow click on dropdown items
          setTimeout(() => setIsFocused(false), 200);
        }}
        isDisabled={loading}
      />
      {error && (
        <Text fontSize="sm" color="red.500" mt={1}>
          {error}
        </Text>
      )}
      {isFocused && query.trim() && filtered.length > 0 && (
        <List
          position="absolute"
          bg="white"
          border="1px solid #ccc"
          w="full"
          zIndex="10"
          maxH="200px"
          overflowY="auto"
          boxShadow="md"
        >
          {filtered.map(group => (
            <ListItem
              key={group.id}
              px={2}
              py={1}
              _hover={{ bg: 'gray.100', cursor: 'pointer' }}
              onClick={() => {
                onSelect(group);
                setQuery(group.name);
                setIsFocused(false);
              }}
            >
              {group.name}
            </ListItem>
          ))}
        </List>
      )}
    </Box>
  );
}