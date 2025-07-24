import { Input, List, ListItem, Box } from '@chakra-ui/react';
import { useState, useEffect } from 'react';
import axios from 'axios';

import {Group} from '@/types/product'

export default function SearchProductGroup({
  onSelect,
}: {
  onSelect: (group: Group) => void;
}) {
  const [allGroups, setAllGroups] = useState<Group[]>([]);
  const [query, setQuery] = useState('');
  const [filtered, setFiltered] = useState<Group[]>([]);

  useEffect(() => {
    const fetchGroups = async () => {
      const res = await axios.get('/api/product-groups'); // trả về all groups
      setAllGroups(res.data);
    };
    fetchGroups();
  }, []);

  useEffect(() => {
    const q = query.toLowerCase();
    setFiltered(allGroups.filter(group => group.name.toLowerCase().includes(q)));
  }, [query, allGroups]);

  return (
    <Box position="relative">
      <Input
        placeholder="Search group..."
        value={query}
        onChange={e => setQuery(e.target.value)}
      />
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