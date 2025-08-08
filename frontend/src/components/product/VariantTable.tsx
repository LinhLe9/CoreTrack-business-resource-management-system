import { 
  Table, Thead, Tbody, Tr, Th, Td, Image, Box, Text, 
  Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalCloseButton,
  useDisclosure, Button, VStack, HStack, Badge, Link, Spinner, Center
} from '@chakra-ui/react';
import { ProductVariantInventoryResponse, BOMItemResponse } from '@/types/product';
import { formatBigDecimal } from '@/lib/utils';
import { useState } from 'react';
import { getBomItem } from '@/services/productService';
import { useRouter } from 'next/navigation';
import { ViewIcon, ChevronDownIcon, ChevronUpIcon } from '@chakra-ui/icons';

interface Props {
  variants: ProductVariantInventoryResponse[];
  productId: number;
}

const VariantTable: React.FC<Props> = ({ variants, productId }) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [selectedImage, setSelectedImage] = useState<{url: string, name: string} | null>(null);
  const [expandedVariants, setExpandedVariants] = useState<Set<number>>(new Set());
  const [bomItemsMap, setBomItemsMap] = useState<Map<number, BOMItemResponse[]>>(new Map());
  const [loadingBomMap, setLoadingBomMap] = useState<Map<number, boolean>>(new Map());
  const router = useRouter();

  const handleImageClick = (imageUrl: string, variantName: string) => {
    setSelectedImage({ url: imageUrl, name: variantName });
    onOpen();
  };

  const handleViewBom = async (variant: ProductVariantInventoryResponse) => {
    const variantId = variant.variant.id;
    
    if (expandedVariants.has(variantId)) {
      // Collapse
      const newExpanded = new Set(expandedVariants);
      newExpanded.delete(variantId);
      setExpandedVariants(newExpanded);
    } else {
      // Expand
      const newExpanded = new Set(expandedVariants);
      newExpanded.add(variantId);
      setExpandedVariants(newExpanded);
      
      // Load BOM items if not already loaded
      if (!bomItemsMap.has(variantId)) {
        const newLoadingMap = new Map(loadingBomMap);
        newLoadingMap.set(variantId, true);
        setLoadingBomMap(newLoadingMap);
        
        try {
          const bomData = await getBomItem(productId, variantId);
          const newBomMap = new Map(bomItemsMap);
          newBomMap.set(variantId, bomData);
          setBomItemsMap(newBomMap);
        } catch (error) {
          console.error('Error loading BOM items:', error);
        } finally {
          const newLoadingMap = new Map(loadingBomMap);
          newLoadingMap.set(variantId, false);
          setLoadingBomMap(newLoadingMap);
        }
      }
    }
  };

  const handleMaterialClick = (materialSku: string) => {
    // Navigate to material detail page
    router.push(`/material/${materialSku}`);
  };

  const formatStockValue = (value: string | null | undefined): string => {
    if (!value || value === 'null' || value === 'undefined') return 'N/A';
    const parsed = parseFloat(value);
    return isNaN(parsed) ? 'N/A' : parsed.toFixed(2);
  };

  if (!variants.length) return <p>No variant data available.</p>;

  return (
    <>
      <Table variant="striped" size="sm">
        <Thead>
          <Tr>
            <Th>Image</Th>
            <Th>Variant</Th>
            <Th>Sku</Th>
            <Th>Description</Th>
            <Th>Current Stock</Th>
            <Th>Minimum Stock</Th>
            <Th>Maximum Stock</Th>
            <Th>Action</Th>
          </Tr>
        </Thead>
        <Tbody>
          {variants.map((item, index) => {
            const variantId = item.variant.id;
            const isExpanded = expandedVariants.has(variantId);
            const isLoading = loadingBomMap.get(variantId) || false;
            const bomItems = bomItemsMap.get(variantId) || [];
            
            return (
              <>
                <Tr key={`variant-${index}`}>
                  <Td>
                    {item.variant.imageUrl ? (
                      <Box>
                        <Image 
                          src={item.variant.imageUrl} 
                          alt={`${item.variant.name} variant`}
                          boxSize="60px"
                          objectFit="cover"
                          borderRadius="md"
                          fallbackSrc="/default-product.jpg"
                          cursor="pointer"
                          _hover={{ opacity: 0.8, transform: 'scale(1.05)' }}
                          transition="all 0.2s"
                          onClick={() => handleImageClick(item.variant.imageUrl, item.variant.name)}
                          border="1px solid"
                          borderColor="gray.200"
                        />
                      </Box>
                    ) : (
                      <Box 
                        boxSize="60px" 
                        bg="gray.100" 
                        borderRadius="md"
                        display="flex"
                        alignItems="center"
                        justifyContent="center"
                        border="1px solid"
                        borderColor="gray.200"
                      >
                        <Text fontSize="xs" color="gray.500">No Image</Text>
                      </Box>
                    )}
                  </Td>
                  <Td>{item.variant.name}</Td>
                  <Td>{item.variant.sku}</Td>
                  <Td>{item.variant.description}</Td>
                  <Td>{formatStockValue(item.inventory?.currentStock)}</Td>
                  <Td>{formatStockValue(item.inventory?.minAlertStock)}</Td>
                  <Td>{formatStockValue(item.inventory?.maxStockLevel)}</Td>
                  <Td>
                    <Button
                      leftIcon={isExpanded ? <ChevronUpIcon /> : <ChevronDownIcon />}
                      size="sm"
                      colorScheme="blue"
                      variant="outline"
                      onClick={() => handleViewBom(item)}
                      isLoading={isLoading}
                      loadingText="Loading..."
                    >
                      {isExpanded ? 'Hide BOM' : 'View BOM'}
                    </Button>
                  </Td>
                </Tr>
                
                {/* Expanded BOM Items Row */}
                {isExpanded && (
                  <Tr key={`bom-${index}`}>
                    <Td colSpan={8} p={0}>
                      <Box bg="gray.50" p={4} borderTop="1px solid" borderColor="gray.200">
                        <VStack spacing={4} align="stretch">
                          <Text fontWeight="bold" fontSize="md" color="blue.600">
                            BOM Items - {item.variant.name}
                          </Text>
                          
                          {isLoading ? (
                            <Center py={8}>
                              <Spinner size="xl" />
                            </Center>
                          ) : bomItems.length > 0 ? (
                            <Table variant="simple" size="sm" bg="white">
                              <Thead>
                                <Tr>
                                  <Th>Material SKU</Th>
                                  <Th>Material Name</Th>
                                  <Th>Quantity</Th>
                                  <Th>Unit of Measure</Th>
                                </Tr>
                              </Thead>
                              <Tbody>
                                {bomItems.map((bomItem, bomIndex) => (
                                  <Tr key={bomIndex}>
                                    <Td>
                                      <Link
                                        color="blue.500"
                                        textDecoration="underline"
                                        cursor="pointer"
                                        onClick={() => handleMaterialClick(bomItem.materialSku)}
                                        _hover={{ color: 'blue.700' }}
                                      >
                                        {bomItem.materialSku}
                                      </Link>
                                    </Td>
                                    <Td>{bomItem.materialName}</Td>
                                    <Td>
                                      <Badge colorScheme="blue" variant="subtle">
                                        {bomItem.quantity}
                                      </Badge>
                                    </Td>
                                    <Td>{bomItem.uomDisplayName}</Td>
                                  </Tr>
                                ))}
                              </Tbody>
                            </Table>
                          ) : (
                            <Center py={8}>
                              <Text color="gray.500">No BOM items found for this variant.</Text>
                            </Center>
                          )}
                        </VStack>
                      </Box>
                    </Td>
                  </Tr>
                )}
              </>
            );
          })}
        </Tbody>
      </Table>

      {/* Image Modal */}
      <Modal isOpen={isOpen} onClose={onClose} size="xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{selectedImage?.name} - Variant Image</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            {selectedImage && (
              <Box textAlign="center">
                <Image 
                  src={selectedImage.url} 
                  alt={selectedImage.name}
                  maxH="500px"
                  objectFit="contain"
                  borderRadius="md"
                  fallbackSrc="/default-product.jpg"
                  border="1px solid"
                  borderColor="gray.200"
                />
              </Box>
            )}
          </ModalBody>
        </ModalContent>
      </Modal>
    </>
  );
};

export default VariantTable;