import { 
  Table, Thead, Tbody, Tr, Th, Td, Image, Box, Text, 
  Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalCloseButton,
  useDisclosure
} from '@chakra-ui/react';
import { ProductVariantInventoryResponse } from '@/types/product';
import { formatBigDecimal } from '@/lib/utils';
import { useState } from 'react';

interface Props {
  variants: ProductVariantInventoryResponse[];
}

const VariantTable: React.FC<Props> = ({ variants }) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [selectedImage, setSelectedImage] = useState<{url: string, name: string} | null>(null);

  const handleImageClick = (imageUrl: string, variantName: string) => {
    setSelectedImage({ url: imageUrl, name: variantName });
    onOpen();
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
          </Tr>
        </Thead>
        <Tbody>
          {variants.map((item, index) => (
            <Tr key={index}>
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
            </Tr>
          ))}
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