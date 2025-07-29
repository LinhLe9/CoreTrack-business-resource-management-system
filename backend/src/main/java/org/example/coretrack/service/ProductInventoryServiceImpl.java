package org.example.coretrack.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse.BulkTransactionError;
import org.example.coretrack.dto.product.inventory.BulkInitInventoryRequest;
import org.example.coretrack.dto.product.inventory.BulkInitInventoryResponse;
import org.example.coretrack.dto.product.inventory.BulkInitInventoryResponse.BulkInitError;
import org.example.coretrack.dto.product.inventory.InventoryEnumsResponse;
import org.example.coretrack.dto.product.inventory.InventoryEnumsResponse.EnumValue;
import org.example.coretrack.dto.product.inventory.TransactionEnumsResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.product.Product;
import org.example.coretrack.model.product.ProductStatus;
import org.example.coretrack.model.product.ProductVariant;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.InventoryTransactionType;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.model.product.inventory.ProductInventoryReferenceDocumentType;
import org.example.coretrack.model.product.inventory.ProductInventoryTransactionSourceType;
import org.example.coretrack.model.product.inventory.ProductInventoryLog;
import org.example.coretrack.repository.ProductInventoryLogRepository;
import org.example.coretrack.repository.ProductInventoryRepository;
import org.example.coretrack.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductInventoryServiceImpl implements ProductInventoryService{

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private ProductInventoryLogRepository productInventoryLogRepository;

    @Override
    @Transactional
    public AddProductInventoryResponse createProductInventory(AddProductInventoryRequest request, User user) {
        System.out.println("=== CREATE PRODUCT INVENTORY DEBUG ===");
        System.out.println("Request SKU: " + request.getProductVariantSku());
        System.out.println("Current Stock: " + request.getCurrentStock());
        System.out.println("Min Alert Stock: " + request.getMinAlertStock());
        System.out.println("Max Stock Level: " + request.getMaxStockLevel());
        System.out.println("User: " + user.getUsername());
        System.out.println("=====================================");
        
        ProductVariant productVariant = productVariantRepository.findBySku(request.getProductVariantSku())
            .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + request.getProductVariantSku()));

        if (productVariant.getStatus() == ProductStatus.DELETED) {
            throw new RuntimeException("Product was deleted, cannot initialize the stock quantity");
        }

        String inventoryStatus = request.getCurrentStock().compareTo(BigDecimal.ZERO) == 0
            ? InventoryStatus.OUT_OF_STOCK.toString()
            : InventoryStatus.IN_STOCK.toString();

        InventoryStatus status = handelStatus(request.getCurrentStock(), BigDecimal.ZERO, BigDecimal.ZERO, 
                                                request.getMinAlertStock(), request.getMaxStockLevel());

        ProductInventory inventory = new ProductInventory(
            productVariant,
            request.getCurrentStock(),
            request.getMinAlertStock(),
            request.getMaxStockLevel(),
            new ArrayList<>(),
            status,
            user
        );

        // Save inventory first
        productInventoryRepository.save(inventory);
        System.out.println("Saved ProductInventory with ID: " + inventory.getId());

        // Create log after inventory is saved
        ProductInventoryLog inventoryLog = new ProductInventoryLog(
            LocalDateTime.now(),
            inventory,
            InventoryTransactionType.SET,
            ProductInventoryTransactionSourceType.PRODUCT_WAREHOUSE_TRANSFER_IN,
            request.getCurrentStock(),
            BigDecimal.valueOf(0),
            request.getCurrentStock(),
            null,
            null,
            null,
            user
        );

        System.out.println("Created ProductInventoryLog with transaction type: " + inventoryLog.getTransactionType());
        System.out.println("Transaction source type: " + inventoryLog.getTransactionSourceType());

        inventory.getLogs().add(inventoryLog);
        productInventoryRepository.save(inventory);
        
        System.out.println("Saved ProductInventory with logs");
        System.out.println("=====================================");

        return new AddProductInventoryResponse(
            productVariant.getSku(),
            productVariant.getName(),
            request.getCurrentStock(),
            request.getMinAlertStock(), 
            request.getMaxStockLevel(),
            productVariant.getStatus().toString(),
            inventoryStatus
        );
    }

    @Override
    @Transactional
    public InventoryTransactionResponse setStock(Long variantId, StockSetRequest request, User user) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + variantId));

        if (productVariant.getStatus() == ProductStatus.DELETED) {
            throw new RuntimeException("Product was deleted, cannot initialize the stock quantity");
        } 
        ProductInventoryReferenceDocumentType type = null ;
        try {
            type = ProductInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }
        Optional<ProductInventory> optionalInventory = productInventoryRepository.findByProductVariant_Id(variantId);
        
        ProductInventory productInventory;
        if (optionalInventory.isPresent()) {
            productInventory = optionalInventory.get();
            BigDecimal result = request.getNewQuantity().subtract(productInventory.getCurrentStock());

            ProductInventoryLog inventoryLog = new ProductInventoryLog(
                LocalDateTime.now(),
                productInventory,
                InventoryTransactionType.SET,
                ProductInventoryTransactionSourceType.SET_INVENTORY,
                result,
                productInventory.getCurrentStock(),
                request.getNewQuantity(),
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );
            productInventory.getLogs().add(inventoryLog);
            productInventory.setCurrentStock(request.getNewQuantity());

            InventoryStatus status = handelStatus(productInventory.getCurrentStock(), productInventory.getFutureStock(), 
                                                    productInventory.getAllocatedStock(), productInventory.getMinAlertStock(), productInventory.getMaxStockLevel());
            
            productInventory.setInventoryStatus(status);
            
            return new InventoryTransactionResponse(
                inventoryLog.getId(),
                inventoryLog.getTransactionType().toString(),
                inventoryLog.getTransactionSourceType().toString(),
                result,
                inventoryLog.getBeforeQuantity(),
                inventoryLog.getAfterQuantity(),
                inventoryLog.getNote(),
                inventoryLog.getReferenceDocumentType().toString(),
                inventoryLog.getReferenceDocumentId(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        } else {
            List<ProductInventoryLog> inventoryLogList = new ArrayList<>();

            ProductInventoryLog inventoryLog = new ProductInventoryLog(
                LocalDateTime.now(),
                null,
                InventoryTransactionType.SET,
                ProductInventoryTransactionSourceType.PRODUCT_WAREHOUSE_TRANSFER_IN,
                request.getNewQuantity(),
                BigDecimal.valueOf(0),
                request.getNewQuantity(),
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );

            inventoryLogList.add(inventoryLog);

            InventoryStatus status;
            if (request.getNewQuantity().compareTo(BigDecimal.ZERO)==0){
                status = InventoryStatus.OUT_OF_STOCK;
            } else {
                status = InventoryStatus.IN_STOCK;
            }
            productInventory = new ProductInventory(
                productVariant,
                request.getNewQuantity(),
                null,
                null,
                inventoryLogList,
                status,
                user
            );

            return new InventoryTransactionResponse(
                inventoryLog.getId(),
                inventoryLog.getTransactionType().toString(),
                inventoryLog.getTransactionSourceType().toString(),
                inventoryLog.getQuantity(),
                inventoryLog.getBeforeQuantity(),
                inventoryLog.getAfterQuantity(),
                inventoryLog.getNote(),
                inventoryLog.getReferenceDocumentType().toString(),
                inventoryLog.getReferenceDocumentId(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        }
    }

    /*
     * Method to add a quantity to the stock inventory
     */
    @Override
    @Transactional
    public InventoryTransactionResponse addStock(Long variantId, StockModifyRequest request, User user) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + variantId));

        if (productVariant.getStatus() == ProductStatus.DELETED) {
            throw new RuntimeException("Product was deleted, cannot initialize the stock quantity");
        } 
        ProductInventoryReferenceDocumentType type = null ;
        try {
            type = ProductInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }

        ProductInventoryTransactionSourceType source = null;
        try {
            source = ProductInventoryTransactionSourceType.valueOf(request.getTransactionSource());
        } catch (IllegalArgumentException e){
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }
        Optional<ProductInventory> optionalInventory = productInventoryRepository.findByProductVariant_Id(variantId);
        
        ProductInventory productInventory;
        if (optionalInventory.isPresent()) {
            productInventory = optionalInventory.get();
            BigDecimal result = request.getNewQuantity().add(productInventory.getCurrentStock());

            ProductInventoryLog inventoryLog = new ProductInventoryLog(
                LocalDateTime.now(),
                productInventory,
                InventoryTransactionType.IN,
                source,
                request.getNewQuantity(),
                productInventory.getCurrentStock(),
                result,
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );
            productInventory.getLogs().add(inventoryLog);
            productInventory.setCurrentStock(result);

            InventoryStatus status = handelStatus(result, productInventory.getFutureStock(), productInventory.getAllocatedStock()
                                                    , productInventory.getMinAlertStock(), productInventory.getMaxStockLevel());

            productInventory.setInventoryStatus(status);

            return new InventoryTransactionResponse(
                inventoryLog.getId(),
                inventoryLog.getTransactionType().toString(),
                inventoryLog.getTransactionSourceType().toString(),
                inventoryLog.getQuantity(),
                inventoryLog.getBeforeQuantity(),
                inventoryLog.getAfterQuantity(),
                inventoryLog.getNote(),
                inventoryLog.getReferenceDocumentType().toString(),
                inventoryLog.getReferenceDocumentId(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        } else {
            List<ProductInventoryLog> inventoryLogList = new ArrayList<>();

            ProductInventoryLog inventoryLog = new ProductInventoryLog(
                LocalDateTime.now(),
                null,
                InventoryTransactionType.IN,
                source,
                request.getNewQuantity(),
                BigDecimal.valueOf(0),
                request.getNewQuantity(),
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );

            inventoryLogList.add(inventoryLog);
            productInventory = new ProductInventory(
                productVariant,
                request.getNewQuantity(),
                null,
                null,
                inventoryLogList,
                InventoryStatus.IN_STOCK,
                user
            );

            return new InventoryTransactionResponse(
                inventoryLog.getId(),
                inventoryLog.getTransactionType().toString(),
                inventoryLog.getTransactionSourceType().toString(),
                inventoryLog.getQuantity(),
                inventoryLog.getBeforeQuantity(),
                inventoryLog.getAfterQuantity(),
                inventoryLog.getNote(),
                inventoryLog.getReferenceDocumentType().toString(),
                inventoryLog.getReferenceDocumentId(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        }
    }

    @Override
    @Transactional
    public InventoryTransactionResponse subtractStock(Long variantId, StockModifyRequest request, User user) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + variantId));

        if (productVariant.getStatus() == ProductStatus.DELETED) {
            throw new RuntimeException("Product was deleted, cannot initialize the stock quantity");
        } 
        ProductInventoryReferenceDocumentType type = null ;
        try {
            type = ProductInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }

        ProductInventoryTransactionSourceType source = null;
        try {
            source = ProductInventoryTransactionSourceType.valueOf(request.getTransactionSource());
        } catch (IllegalArgumentException e){
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }
        Optional<ProductInventory> optionalInventory = productInventoryRepository.findByProductVariant_Id(variantId);
        
        ProductInventory productInventory;
        if (optionalInventory.isPresent()) {
            productInventory = optionalInventory.get();
            BigDecimal afterQuantity = productInventory.getCurrentStock().subtract(request.getNewQuantity());

            if (afterQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Cannot subtract. Stock would become negative.");
            }
                
            ProductInventoryLog inventoryLog = new ProductInventoryLog(
                LocalDateTime.now(),
                productInventory,
                InventoryTransactionType.OUT,
                source,
                request.getNewQuantity(),
                productInventory.getCurrentStock(),
                afterQuantity,
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );
            productInventory.getLogs().add(inventoryLog);
            productInventory.setCurrentStock(afterQuantity);
            return new InventoryTransactionResponse(
                inventoryLog.getId(),
                inventoryLog.getTransactionType().toString(),
                inventoryLog.getTransactionSourceType().toString(),
                inventoryLog.getQuantity(),
                inventoryLog.getBeforeQuantity(),
                inventoryLog.getAfterQuantity(),
                inventoryLog.getNote(),
                inventoryLog.getReferenceDocumentType().toString(),
                inventoryLog.getReferenceDocumentId(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        } else {
            throw new IllegalArgumentException("Cannot subtract. Product inventory does not exist. Please initialize inventory first.");
        }
    }

    @Override
    public Page<SearchProductInventoryResponse> findProduct(
        String search, 
        List<String> groupProducts,
        List<String> inventoryStatus, 
        Pageable pageable) {
        
        System.out.println("=== FIND PRODUCT INVENTORY DEBUG ===");
        System.out.println("Search: " + search);
        System.out.println("Group Products (String): " + groupProducts);
        System.out.println("Inventory Status (String): " + inventoryStatus);
        System.out.println("Pageable: " + pageable);
        
        // Convert List<String> to List<Long> for groupProducts
        List<Long> groupProductIds = null;
        if (groupProducts != null && !groupProducts.isEmpty()) {
            groupProductIds = groupProducts.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        }
        
        // Convert List<String> to List<InventoryStatus> for inventoryStatus
        List<InventoryStatus> inventoryStatusList = null;
        if (inventoryStatus != null && !inventoryStatus.isEmpty()) {
            inventoryStatusList = inventoryStatus.stream()
                .map(status -> {
                    try {
                        return InventoryStatus.valueOf(status.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid inventory status: " + status);
                        return null;
                    }
                })
                .filter(status -> status != null)
                .collect(Collectors.toList());
        }
        
        System.out.println("Group Product IDs: " + groupProductIds);
        System.out.println("Inventory Status List: " + inventoryStatusList);
        
        Page<SearchProductInventoryResponse> result = productInventoryRepository.searchInventoryByCriteria(
            search, groupProductIds, inventoryStatusList, pageable);
            
        System.out.println("Result total elements: " + result.getTotalElements());
        System.out.println("Result content size: " + result.getContent().size());
        System.out.println("Result content: " + result.getContent());
        System.out.println("=====================================");
        
        return result;
    }

    // helper method for inventory status
    private InventoryStatus handelStatus(
        BigDecimal currentQ, BigDecimal futureQ, BigDecimal allocatedQ,
        BigDecimal minQ, BigDecimal maxQ){
            
        // Handle null values
        currentQ = currentQ != null ? currentQ : BigDecimal.ZERO;
        futureQ = futureQ != null ? futureQ : BigDecimal.ZERO;
        allocatedQ = allocatedQ != null ? allocatedQ : BigDecimal.ZERO;
        minQ = minQ != null ? minQ : BigDecimal.ZERO;
        maxQ = maxQ != null ? maxQ : BigDecimal.valueOf(Long.MAX_VALUE);
            
        BigDecimal available = currentQ.subtract(allocatedQ);
        BigDecimal projected = currentQ.add(futureQ);
    
        if (available.compareTo(minQ) < 0) {
            return InventoryStatus.LOW_STOCK;
        } else if (available.compareTo(BigDecimal.ZERO) == 0) {
            return InventoryStatus.OUT_OF_STOCK;
        } else if (projected.compareTo(maxQ) > 0){
            return InventoryStatus.OVER_STOCK;
        }
        return InventoryStatus.IN_STOCK;
    }

    @Override
    public List<AllSearchProductInventoryResponse> getAllForAutocomplete(String search) {
        // handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<AllSearchProductInventoryResponse> response = productInventoryRepository.searchInventory(processedSearch);
        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductInventoryDetailResponse getProductInventoryById(Long variantId) {
        ProductInventory inventory = productInventoryRepository.findByProductVariant_Id(variantId)
            .orElseThrow(() -> new RuntimeException("Product inventory not found with variant ID: " + variantId));

        ProductVariant variant = inventory.getProductVariant();
        Product product = variant.getProduct();
        
        // Get logs for this inventory with null safety
        List<InventoryTransactionResponse> logs = productInventoryLogRepository
            .findByProductVariant_IdOrderByTransactionTimestampDesc(variantId)
            .stream()
            .filter(log -> log != null) // Filter out null logs
            .map(log -> {
                try {
                    return new InventoryTransactionResponse(log);
                } catch (Exception e) {
                    // Log the error but continue processing other logs
                    System.err.println("Error processing inventory log: " + e.getMessage());
                    return null;
                }
            })
            .filter(response -> response != null) // Filter out null responses
            .collect(Collectors.toList());

        return new ProductInventoryDetailResponse(
            variant.getId(),
            product.getName(),
            product.getSku(),
            variant.getSku(),
            variant.getName(),
            inventory.getCurrentStock(),
            inventory.getMinAlertStock(),
            inventory.getMaxStockLevel(),
            inventory.getInventoryStatus().name(),
            product.getGroup() != null ? product.getGroup().getName() : null,
            variant.getImageUrl(),
            logs
        );
    }

    // Bulk operations implementation
    @Override
    @Transactional
    public BulkInventoryTransactionResponse bulkSetStock(BulkStockSetRequest request, User user) {
        List<InventoryTransactionResponse> successfulTransactions = new ArrayList<>();
        List<BulkTransactionError> failedTransactions = new ArrayList<>();
        
        for (Long variantId : request.getVariantIds()) {
            try {
                StockSetRequest singleRequest = new StockSetRequest(
                    request.getQuantity(),
                    request.getNote(),
                    request.getReferenceDocumentType(),
                    request.getReferenceDocumentId()
                );
                
                InventoryTransactionResponse response = setStock(variantId, singleRequest, user);
                successfulTransactions.add(response);
            } catch (Exception e) {
                failedTransactions.add(new BulkTransactionError(
                    variantId,
                    "SET_STOCK_FAILED",
                    e.getMessage()
                ));
            }
        }
        
        return new BulkInventoryTransactionResponse(
            successfulTransactions,
            failedTransactions,
            request.getVariantIds().size(),
            successfulTransactions.size(),
            failedTransactions.size()
        );
    }

    @Override
    @Transactional
    public BulkInventoryTransactionResponse bulkAddStock(BulkStockModifyRequest request, User user) {
        List<InventoryTransactionResponse> successfulTransactions = new ArrayList<>();
        List<BulkTransactionError> failedTransactions = new ArrayList<>();
        
        for (Long variantId : request.getVariantIds()) {
            try {
                StockModifyRequest singleRequest = new StockModifyRequest(
                    request.getQuantity(),
                    request.getNote(),
                    request.getReferenceDocumentType(),
                    request.getReferenceDocumentId(),
                    request.getTransactionSource()
                );
                
                InventoryTransactionResponse response = addStock(variantId, singleRequest, user);
                successfulTransactions.add(response);
            } catch (Exception e) {
                failedTransactions.add(new BulkTransactionError(
                    variantId,
                    "ADD_STOCK_FAILED",
                    e.getMessage()
                ));
            }
        }
        
        return new BulkInventoryTransactionResponse(
            successfulTransactions,
            failedTransactions,
            request.getVariantIds().size(),
            successfulTransactions.size(),
            failedTransactions.size()
        );
    }

    @Override
    @Transactional
    public BulkInventoryTransactionResponse bulkSubtractStock(BulkStockModifyRequest request, User user) {
        List<InventoryTransactionResponse> successfulTransactions = new ArrayList<>();
        List<BulkTransactionError> failedTransactions = new ArrayList<>();
        
        for (Long variantId : request.getVariantIds()) {
            try {
                StockModifyRequest singleRequest = new StockModifyRequest(
                    request.getQuantity(),
                    request.getNote(),
                    request.getReferenceDocumentType(),
                    request.getReferenceDocumentId(),
                    request.getTransactionSource()
                );
                
                InventoryTransactionResponse response = subtractStock(variantId, singleRequest, user);
                successfulTransactions.add(response);
            } catch (Exception e) {
                failedTransactions.add(new BulkTransactionError(
                    variantId,
                    "SUBTRACT_STOCK_FAILED",
                    e.getMessage()
                ));
            }
        }
        
        return new BulkInventoryTransactionResponse(
            successfulTransactions,
            failedTransactions,
            request.getVariantIds().size(),
            successfulTransactions.size(),
            failedTransactions.size()
        );
    }

    @Override
    @Transactional
    public BulkInitInventoryResponse bulkInitInventory(BulkInitInventoryRequest request, User user) {
        List<AddProductInventoryResponse> successfulInits = new ArrayList<>();
        List<BulkInitError> failedInits = new ArrayList<>();
        
        for (String productVariantSku : request.getProductVariantSkus()) {
            try {
                AddProductInventoryRequest singleRequest = new AddProductInventoryRequest(
                    productVariantSku,
                    request.getCurrentStock(),
                    request.getMinAlertStock(),
                    request.getMaxStockLevel()
                );
                
                AddProductInventoryResponse response = createProductInventory(singleRequest, user);
                successfulInits.add(response);
            } catch (Exception e) {
                failedInits.add(new BulkInitError(
                    productVariantSku,
                    "INIT_INVENTORY_FAILED",
                    e.getMessage()
                ));
            }
        }
        
        return new BulkInitInventoryResponse(
            successfulInits,
            failedInits,
            request.getProductVariantSkus().size(),
            successfulInits.size(),
            failedInits.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryEnumsResponse getInventoryEnums() {
        List<EnumValue> inventoryStatuses = new ArrayList<>();
        for (InventoryStatus status : InventoryStatus.values()) {
            inventoryStatuses.add(new EnumValue(
                status.name(),
                status.getDisplayName(),
                getInventoryStatusDescription(status)
            ));
        }

        List<EnumValue> inventoryTransactionTypes = new ArrayList<>();
        for (InventoryTransactionType type : InventoryTransactionType.values()) {
            inventoryTransactionTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getInventoryTransactionTypeDescription(type)
            ));
        }

        List<EnumValue> productInventoryReferenceDocumentTypes = new ArrayList<>();
        for (ProductInventoryReferenceDocumentType type : ProductInventoryReferenceDocumentType.values()) {
            productInventoryReferenceDocumentTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getProductInventoryReferenceDocumentTypeDescription(type)
            ));
        }

        List<EnumValue> productInventoryTransactionSourceTypes = new ArrayList<>();
        for (ProductInventoryTransactionSourceType type : ProductInventoryTransactionSourceType.values()) {
            productInventoryTransactionSourceTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getProductInventoryTransactionSourceTypeDescription(type)
            ));
        }

        return new InventoryEnumsResponse(
            inventoryStatuses,
            inventoryTransactionTypes,
            productInventoryReferenceDocumentTypes,
            productInventoryTransactionSourceTypes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnumValue> getInventoryStatuses() {
        List<EnumValue> inventoryStatuses = new ArrayList<>();
        for (InventoryStatus status : InventoryStatus.values()) {
            inventoryStatuses.add(new EnumValue(
                status.name(),
                status.getDisplayName(),
                getInventoryStatusDescription(status)
            ));
        }
        return inventoryStatuses;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionEnumsResponse getTransactionEnums() {
        List<EnumValue> inventoryTransactionTypes = new ArrayList<>();
        for (InventoryTransactionType type : InventoryTransactionType.values()) {
            inventoryTransactionTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getInventoryTransactionTypeDescription(type)
            ));
        }

        List<EnumValue> productInventoryReferenceDocumentTypes = new ArrayList<>();
        for (ProductInventoryReferenceDocumentType type : ProductInventoryReferenceDocumentType.values()) {
            productInventoryReferenceDocumentTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getProductInventoryReferenceDocumentTypeDescription(type)
            ));
        }

        List<EnumValue> productInventoryTransactionSourceTypes = new ArrayList<>();
        for (ProductInventoryTransactionSourceType type : ProductInventoryTransactionSourceType.values()) {
            productInventoryTransactionSourceTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getProductInventoryTransactionSourceTypeDescription(type)
            ));
        }

        return new TransactionEnumsResponse(
            inventoryTransactionTypes,
            productInventoryReferenceDocumentTypes,
            productInventoryTransactionSourceTypes
        );
    }

    // Helper methods for enum descriptions
    private String getInventoryStatusDescription(InventoryStatus status) {
        switch (status) {
            case IN_STOCK:
                return "Product is available in stock";
            case OUT_OF_STOCK:
                return "Product is not available in stock";
            case LOW_STOCK:
                return "Product stock is below minimum alert level";
            case OVER_STOCK:
                return "Product stock exceeds maximum level";
            default:
                return "Unknown inventory status";
        }
    }

    private String getInventoryTransactionTypeDescription(InventoryTransactionType type) {
        switch (type) {
            case IN:
                return "Stock addition transaction";
            case OUT:
                return "Stock reduction transaction";
            case SET:
                return "Stock setting transaction";
            default:
                return "Unknown transaction type";
        }
    }

    private String getProductInventoryReferenceDocumentTypeDescription(ProductInventoryReferenceDocumentType type) {
        switch (type) {
            case PURCHASE_ORDER:
                return "Reference to purchase order document";
            case SALES_ORDER:
                return "Reference to sales order document";
            case PRODUCTION_ORDER:
                return "Reference to production order document";
            case PRODUCTION_TICKET:
                return "Reference to production ticket document";
            case INVENTORY_ADJUSTMENT:
                return "Reference to inventory adjustment document";
            case WAREHOUSE_TRANSFER:
                return "Reference to warehouse transfer document";
            default:
                return "Unknown reference document type";
        }
    }

    private String getProductInventoryTransactionSourceTypeDescription(ProductInventoryTransactionSourceType type) {
        switch (type) {
            case PURCHASE_ORDER_RECEIPT:
                return "Stock received from purchase order";
            case PRODUCTION_COMPLETION:
                return "Stock added from production completion";
            case CUSTOMER_RETURN:
                return "Stock added from customer return";
            case PRODUCT_ADJUSTMENT_INCREASE:
                return "Stock increased through inventory adjustment";
            case PRODUCT_WAREHOUSE_TRANSFER_IN:
                return "Stock transferred in from warehouse";
            case SALES_ORDER_FULFILLMENT:
                return "Stock reduced for sales order fulfillment";
            case PRODUCTION_CONSUMPTION:
                return "Stock reduced for production consumption";
            case SUPPLIER_RETURN:
                return "Stock reduced for supplier return";
            case INVENTORY_ADJUSTMENT_DECREASE:
                return "Stock decreased through inventory adjustment";
            case WAREHOUSE_TRANSFER_OUT:
                return "Stock transferred out to warehouse";
            case SCRAP:
                return "Stock reduced due to product scrap";
            case SET_INVENTORY:
                return "Stock set to specific quantity";
            default:
                return "Unknown transaction source type";
        }
    }

}
