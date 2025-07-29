package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.product.inventory.AddProductInventoryRequest;
import org.example.coretrack.dto.product.inventory.AddProductInventoryResponse;
import org.example.coretrack.dto.product.inventory.AllSearchProductInventoryResponse;
import org.example.coretrack.dto.product.inventory.InventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.ProductInventoryDetailResponse;
import org.example.coretrack.dto.product.inventory.SearchProductInventoryResponse;
import org.example.coretrack.dto.product.inventory.StockModifyRequest;
import org.example.coretrack.dto.product.inventory.StockSetRequest;
import org.example.coretrack.dto.product.inventory.BulkStockModifyRequest;
import org.example.coretrack.dto.product.inventory.BulkStockSetRequest;
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.BulkInitInventoryRequest;
import org.example.coretrack.dto.product.inventory.BulkInitInventoryResponse;
import org.example.coretrack.dto.product.inventory.InventoryEnumsResponse;
import org.example.coretrack.dto.product.inventory.TransactionEnumsResponse;
import org.example.coretrack.model.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductInventoryService {
    AddProductInventoryResponse createProductInventory(AddProductInventoryRequest request, User user);
    
    InventoryTransactionResponse setStock (Long variantId, StockSetRequest request, User user);

    InventoryTransactionResponse addStock (Long variantId, StockModifyRequest request, User user);

    InventoryTransactionResponse subtractStock (Long variantId, StockModifyRequest request, User user);

    Page<SearchProductInventoryResponse> findProduct(
            String search,
            List<String> groupProducts,
            List<String> inventoryStatus,
            Pageable pageable);

    List<AllSearchProductInventoryResponse> getAllForAutocomplete (String search);

    ProductInventoryDetailResponse getProductInventoryById (Long variantId);

    // Bulk operations
    BulkInventoryTransactionResponse bulkSetStock(BulkStockSetRequest request, User user);
    
    BulkInventoryTransactionResponse bulkAddStock(BulkStockModifyRequest request, User user);
    
    BulkInventoryTransactionResponse bulkSubtractStock(BulkStockModifyRequest request, User user);

    // Bulk init inventory
    BulkInitInventoryResponse bulkInitInventory(BulkInitInventoryRequest request, User user);

    // Get inventory enums
    InventoryEnumsResponse getInventoryEnums();
    
    // Get inventory statuses only (for FilterBar)
    List<InventoryEnumsResponse.EnumValue> getInventoryStatuses();
    
    // Get transaction enums only (for stock operations)
    TransactionEnumsResponse getTransactionEnums();
}
