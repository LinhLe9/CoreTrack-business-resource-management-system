import React, { useState, useEffect } from 'react';
import {
  Box,
  Input,
  List,
  ListItem,
  Text,
  VStack,
  useDisclosure,
} from '@chakra-ui/react';
import { MaterialAutoComplete } from '@/types/material';
import { getAllMaterialsForAutocomplete } from '@/services/materialService';

interface MaterialSelectBarProps {
  onSelect: (material: MaterialAutoComplete) => void;
  selectedMaterial?: MaterialAutoComplete;
}

const MaterialSelectBar: React.FC<MaterialSelectBarProps> = ({ onSelect, selectedMaterial }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredMaterials, setFilteredMaterials] = useState<MaterialAutoComplete[]>([]);
  const [allMaterials, setAllMaterials] = useState<MaterialAutoComplete[]>([]);
  const { isOpen, onOpen, onClose } = useDisclosure();

  useEffect(() => {
    const fetchMaterials = async () => {
      try {
        const materials = await getAllMaterialsForAutocomplete();
        setAllMaterials(materials);
      } catch (error) {
        console.error('Error fetching materials:', error);
      }
    };
    fetchMaterials();
  }, []);

  useEffect(() => {
    if (searchTerm.trim() === '') {
      setFilteredMaterials([]);
      return;
    }

    const filtered = allMaterials.filter(material =>
      material.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      material.sku.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredMaterials(filtered);
  }, [searchTerm, allMaterials]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    if (e.target.value.trim() !== '') {
      onOpen();
    } else {
      onClose();
    }
  };

  const handleMaterialSelect = (material: MaterialAutoComplete) => {
    onSelect(material);
    setSearchTerm(material.name);
    onClose();
  };

  return (
    <Box position="relative">
      <Input
        value={selectedMaterial ? selectedMaterial.name : searchTerm}
        onChange={handleInputChange}
        placeholder="Search materials..."
        onFocus={() => {
          if (searchTerm.trim() !== '') {
            onOpen();
          }
        }}
      />
      
      {isOpen && filteredMaterials.length > 0 && (
        <Box
          position="absolute"
          top="100%"
          left={0}
          right={0}
          bg="white"
          border="1px solid"
          borderColor="gray.200"
          borderRadius="md"
          boxShadow="lg"
          zIndex={1000}
          maxH="200px"
          overflowY="auto"
        >
          <List>
            {filteredMaterials.map((material) => (
              <ListItem
                key={material.id}
                px={3}
                py={2}
                cursor="pointer"
                _hover={{ bg: "gray.100" }}
                onClick={() => handleMaterialSelect(material)}
              >
                <VStack align="start" spacing={1}>
                  <Text fontWeight="medium">{material.name}</Text>
                  <Text fontSize="sm" color="gray.600">SKU: {material.sku}</Text>
                </VStack>
              </ListItem>
            ))}
          </List>
        </Box>
      )}
    </Box>
  );
};

export default MaterialSelectBar; 