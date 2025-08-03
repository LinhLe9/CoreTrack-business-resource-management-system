package org.example.coretrack.service;

import java.math.BigDecimal;
import java.util.List;

import org.example.coretrack.dto.material.Inventory.AddMaterialInventoryRequest;
import org.example.coretrack.dto.material.Inventory.AddMaterialInventoryResponse;
import org.example.coretrack.dto.material.Inventory.BulkMInitInventoryRequest;
import org.example.coretrack.dto.material.Inventory.BulkMInitInventoryResponse;
import org.example.coretrack.dto.material.Inventory.MaterialInventoryDetailResponse;
import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.BulkStockModifyRequest;
import org.example.coretrack.dto.product.inventory.BulkStockSetRequest;
import org.example.coretrack.dto.product.inventory.InventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.SearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.StockModifyRequest;
import org.example.coretrack.dto.product.inventory.StockSetRequest;
import org.example.coretrack.dto.product.inventory.TransactionEnumsResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.MaterialVariant;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialInventoryService {
    AddMaterialInventoryResponse createMaterialInventory (AddMaterialInventoryRequest request, User user);

    InventoryTransactionResponse setStock (Long variantId, StockSetRequest request, User user);

    InventoryTransactionResponse addStock (Long variantId, StockModifyRequest request, User user);

    InventoryTransactionResponse subtractStock (Long variantId, StockModifyRequest request, User user);

    Page<SearchInventoryResponse> findMaterial (String search, List<String> groupMaterials, List<String> inventoryStatus, Pageable pageable);
    
    List<AllSearchInventoryResponse> getAllForAutocomplete (String search);

    MaterialInventoryDetailResponse getMaterialInventoryById (Long variantId);

    BulkInventoryTransactionResponse bulkSetStock (BulkStockSetRequest request, User user);

    BulkInventoryTransactionResponse bulkAddStock (BulkStockModifyRequest request, User user);

    BulkInventoryTransactionResponse bulkSubtractStock (BulkStockModifyRequest request, User user);

    BulkMInitInventoryResponse bulkInitInventory (BulkMInitInventoryRequest request, User user);

    TransactionEnumsResponse getTransactionEnums();

    MaterialInventory addToFutureStock(Long variantId, BigDecimal quantity, User user, Long ticketId);

    MaterialInventory moveFromFutureToCurrentStock(Long variantId, BigDecimal quantity, User user, Long ticketId);

    MaterialInventory removeFromFutureStock(Long variantId, BigDecimal quantity, User user, Long ticketId);

    MaterialInventory removeFromCurrentStock(Long variantId, BigDecimal quantity, User user, Long ticketId);

    MaterialInventory removeFromCurrentAndAllocatedStock(Long variantId, BigDecimal quantity, User user, Long ticketId);
 
    boolean isEnough(MaterialVariant variant, BigDecimal plannedQuantity);

    MaterialInventory addToAllocatedStock (Long variantId, BigDecimal quantity, User user, Long ticketId);

    MaterialInventory removeFromAllocatedStock (Long variantId, BigDecimal quantity, User user, Long ticketId);

    Page<SearchInventoryResponse> getAlarmMaterial(String search, List<Long> groupMaterials, List<String> status, boolean sortByOldest, Pageable pageable);
}
