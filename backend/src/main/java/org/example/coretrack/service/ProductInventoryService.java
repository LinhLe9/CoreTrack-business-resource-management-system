package org.example.coretrack.service;

import java.math.BigDecimal;
import java.util.List;

import org.example.coretrack.dto.product.inventory.AddProductInventoryRequest;
import org.example.coretrack.dto.product.inventory.AddProductInventoryResponse;
import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.InventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.ProductInventoryDetailResponse;
import org.example.coretrack.dto.product.inventory.SearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.StockModifyRequest;
import org.example.coretrack.dto.product.inventory.StockSetRequest;
import org.example.coretrack.dto.product.inventory.SetMinMaxRequest;
import org.example.coretrack.dto.product.inventory.SetMinMaxResponse;
import org.example.coretrack.dto.product.inventory.BulkStockModifyRequest;
import org.example.coretrack.dto.product.inventory.BulkStockSetRequest;
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.BulkInitInventoryRequest;
import org.example.coretrack.dto.product.inventory.BulkInitInventoryResponse;
import org.example.coretrack.dto.product.inventory.InventoryEnumsResponse;
import org.example.coretrack.dto.product.inventory.TransactionEnumsResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductInventoryService {
    AddProductInventoryResponse createProductInventory(AddProductInventoryRequest request, User user);
    
    InventoryTransactionResponse setStock (Long variantId, StockSetRequest request, User user);

    InventoryTransactionResponse addStock (Long variantId, StockModifyRequest request, User user);

    InventoryTransactionResponse subtractStock (Long variantId, StockModifyRequest request, User user);
    

    Page<SearchInventoryResponse> findProduct(
            String search,
            List<String> groupProducts,
            List<String> inventoryStatus,
            Pageable pageable,
            User user);

    List<AllSearchInventoryResponse> getAllForAutocomplete (String search, User user);

    ProductInventoryDetailResponse getProductInventoryById (Long variantId, User user);

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

    // Production ticket inventory integration methods
    ProductInventory addToFutureStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory moveFromFutureToCurrentStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory removeFromFutureStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory removeFromCurrentStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory removeFromAllocatedAndCurrentStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory addToAllocatedStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory removeFromAllocatedStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory addToCurrentStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
    ProductInventory getByProductVariantId(Long variantId, User user);
    Page<SearchInventoryResponse> getAlarmProduct(String search,
            List<String> groupProducts,
            List<String> status,
            boolean sortByOldest,
            Pageable pageable,
            User user);

    // Set minimum and maximum stock levels
    SetMinMaxResponse setMinimumAlertStock(Long variantId, SetMinMaxRequest request, User user);
    
    SetMinMaxResponse setMaximumStockLevel(Long variantId, SetMinMaxRequest request, User user);
}
