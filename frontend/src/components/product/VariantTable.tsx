import { Table, Thead, Tbody, Tr, Th, Td } from '@chakra-ui/react';
import { ProductVariantInventoryResponse } from '@/types/product';

interface Props {
  variants: ProductVariantInventoryResponse[];
}

const VariantTable: React.FC<Props> = ({ variants }) => {
  if (!variants.length) return <p>No variant data available.</p>;

  return (
    <Table variant="striped" size="sm">
      <Thead>
        <Tr>
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
            <Td>{item.variant.name}</Td>
            <Td>{item.variant.sku}</Td>
            <Td>{item.variant.description}</Td>
            <Td>{item.inventory?.currentStock ?? 0}</Td>
            <Td>{item.inventory?.minAlertStock ?? 'N/A'}</Td>
            <Td>{item.inventory?.maxStockLevel ?? 'N/A'}</Td>
          </Tr>
        ))}
      </Tbody>
    </Table>
  );
};

export default VariantTable;