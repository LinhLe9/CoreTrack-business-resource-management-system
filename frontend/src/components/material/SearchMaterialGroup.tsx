import { Input, List, ListItem, Box, Text } from '@chakra-ui/react';
import { useState, useEffect } from 'react';
import api from '@/lib/axios';

import {MaterialGroup} from '@/types/material'

export default function SearchMaterialGroup({
  onSelect,
  value,
}: {
  onSelect: (group: MaterialGroup) => void;
  value?: string;
}) {
  const [allGroups, setAllGroups] = useState<MaterialGroup[]>([]);
  const [query, setQuery] = useState(value || '');
  const [filtered, setFiltered] = useState<MaterialGroup[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchGroups = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await api.get('/materials/material-groups'); // trả về all groups
        setAllGroups(res.data);
      } catch (err: any) {
        console.error('Error fetching material groups:', err);
        setError(err.response?.data?.message || 'Failed to load material groups');
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
        placeholder="Search group..."
        value={query}
        onChange={e => setQuery(e.target.value)}
        isDisabled={loading}
      />
      {error && (
        <Text fontSize="sm" color="red.500" mt={1}>
          {error}
        </Text>
      )}
      {filtered.length > 0 && (
        <List
          position="absolute"
          bg="white"
          border="1px solid #ccc"
          w="full"
          zIndex="10"
          maxH="200px"
          overflowY="auto"
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