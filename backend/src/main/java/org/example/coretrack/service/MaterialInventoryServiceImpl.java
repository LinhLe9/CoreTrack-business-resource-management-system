package org.example.coretrack.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.coretrack.dto.material.Inventory.AddMaterialInventoryRequest;
import org.example.coretrack.dto.material.Inventory.AddMaterialInventoryResponse;
import org.example.coretrack.dto.material.Inventory.BulkMInitInventoryRequest;
import org.example.coretrack.dto.material.Inventory.BulkMInitInventoryResponse;
import org.example.coretrack.dto.material.Inventory.BulkMInitInventoryResponse.BulkMInitError;
import org.example.coretrack.dto.material.Inventory.MaterialInventoryDetailResponse;
import org.example.coretrack.dto.product.inventory.InventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.SearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.StockModifyRequest;
import org.example.coretrack.dto.product.inventory.StockSetRequest;
import org.example.coretrack.dto.product.inventory.TransactionEnumsResponse;
import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse.BulkTransactionError;
import org.example.coretrack.dto.product.inventory.BulkStockModifyRequest;
import org.example.coretrack.dto.product.inventory.BulkStockSetRequest;
import org.example.coretrack.dto.product.inventory.InventoryEnumsResponse.EnumValue;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.material.MaterialStatus;
import org.example.coretrack.model.material.MaterialVariant;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.example.coretrack.model.material.inventory.MaterialInventoryLog;
import org.example.coretrack.model.material.inventory.materialInventoryReferenceDocumentType;
import org.example.coretrack.model.material.inventory.materialInventoryTransactionSourceType;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.InventoryTransactionType;
import org.example.coretrack.repository.MaterialInventoryLogRepository;
import org.example.coretrack.repository.MaterialInventoryRepository;
import org.example.coretrack.repository.MaterialVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialInventoryServiceImpl implements MaterialInventoryService{
    @Autowired
    private MaterialVariantRepository materialVariantRepository;

    @Autowired
    private MaterialInventoryRepository materialInventoryRepository;

    @Autowired
    private MaterialInventoryLogRepository materialInventoryLogRepository;

    @Override
    @Transactional
    public AddMaterialInventoryResponse createMaterialInventory(AddMaterialInventoryRequest request, User user) {
        MaterialVariant materialVariant = materialVariantRepository.findBySku(request.getMaterialVariantSku())
            .orElseThrow(() -> new RuntimeException("Material not found with SKU: " + request.getMaterialVariantSku()));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material was deleted, cannot initialize the stock quantity");
        }

        String inventoryStatus = request.getCurrentStock().compareTo(BigDecimal.ZERO) == 0
            ? InventoryStatus.OUT_OF_STOCK.toString()
            : InventoryStatus.IN_STOCK.toString();

        InventoryStatus status = handelStatus(request.getCurrentStock(), BigDecimal.ZERO, BigDecimal.ZERO, 
                                                request.getMinAlertStock(), request.getMaxStockLevel());

        MaterialInventory inventory = new MaterialInventory(
            materialVariant,
            request.getCurrentStock(),
            request.getMinAlertStock(),
            request.getMaxStockLevel(),
            new ArrayList<>(),
            status,
            user
        );

        // Save inventory first
        materialInventoryRepository.save(inventory);
        System.out.println("Saved MaterialInventory with ID: " + inventory.getId());

        // Create log after inventory is saved
        MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            InventoryTransactionType.SET,
            materialInventoryTransactionSourceType.MATERIAL_WAREHOUSE_TRANSFER_IN,
            request.getCurrentStock(),
            BigDecimal.valueOf(0),
            request.getCurrentStock(),
            null,
            null,
            null,
            user
        );

        System.out.println("Created MaterialInventoryLog with transaction type: " + inventoryLog.getTransactionType());
        System.out.println("Transaction source type: " + inventoryLog.getTransactionSourceType());

        inventory.getLogs().add(inventoryLog);
        materialInventoryRepository.save(inventory);
        
        System.out.println("Saved ProductInventory with logs");
        System.out.println("=====================================");

        return new AddMaterialInventoryResponse(
            materialVariant.getSku(),
            materialVariant.getName(),
            request.getCurrentStock(),
            request.getMinAlertStock(), 
            request.getMaxStockLevel(),
            materialVariant.getStatus().toString(),
            inventoryStatus
        );
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
    @Transactional
    public InventoryTransactionResponse setStock(Long variantId, StockSetRequest request, User user) {
        MaterialVariant materialVariant = materialVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Material not found with id: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material was deleted, cannot initialize the stock quantity");
        } 
        materialInventoryReferenceDocumentType type = null ;
        try {
            type = materialInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_Id(variantId);
        
        MaterialInventory materialInventory;
        if (optionalInventory.isPresent()) {
            materialInventory = optionalInventory.get();
            BigDecimal result = request.getNewQuantity().subtract(materialInventory.getCurrentStock());

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                materialInventory,
                InventoryTransactionType.SET,
                materialInventoryTransactionSourceType.SET_INVENTORY,
                result,
                materialInventory.getCurrentStock(),
                request.getNewQuantity(),
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );
            materialInventory.getLogs().add(inventoryLog);
            materialInventory.setCurrentStock(request.getNewQuantity());

            InventoryStatus status = handelStatus(materialInventory.getCurrentStock(), materialInventory.getFutureStock(), 
                                                    materialInventory.getAllocatedStock(), materialInventory.getMinAlertStock(), materialInventory.getMaxStockLevel());
            
            materialInventory.setInventoryStatus(status);
            
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
            List<MaterialInventoryLog> inventoryLogList = new ArrayList<>();

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                null,
                InventoryTransactionType.SET,
                materialInventoryTransactionSourceType.MATERIAL_WAREHOUSE_TRANSFER_IN,
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
            materialInventory = new MaterialInventory(
                materialVariant,
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


    @Override
    @Transactional
    public InventoryTransactionResponse addStock(Long variantId, StockModifyRequest request, User user) {
        MaterialVariant materialVariant = materialVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Material varian not found with id: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material variant was deleted, cannot initialize the stock quantity");
        } 
        materialInventoryReferenceDocumentType type = null ;
        try {
            type = materialInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }

        materialInventoryTransactionSourceType source = null;
        try {
            source = materialInventoryTransactionSourceType.valueOf(request.getTransactionSource());
        } catch (IllegalArgumentException e){
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_Id(variantId);
        
        MaterialInventory materialInventory;
        if (optionalInventory.isPresent()) {
            materialInventory = optionalInventory.get();
            BigDecimal result = request.getNewQuantity().add(materialInventory.getCurrentStock());

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                materialInventory,
                InventoryTransactionType.IN,
                source,
                request.getNewQuantity(),
                materialInventory.getCurrentStock(),
                result,
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );
            materialInventory.getLogs().add(inventoryLog);
            materialInventory.setCurrentStock(result);

            InventoryStatus status = handelStatus(result, materialInventory.getFutureStock(), materialInventory.getAllocatedStock()
                                                    , materialInventory.getMinAlertStock(), materialInventory.getMaxStockLevel());

            materialInventory.setInventoryStatus(status);

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
            List<MaterialInventoryLog> inventoryLogList = new ArrayList<>();

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
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
            materialInventory = new MaterialInventory(
                materialVariant,
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
        MaterialVariant materialVariant = materialVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Material variant not found with id: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material variant was deleted, cannot initialize the stock quantity");
        } 
        materialInventoryReferenceDocumentType type = null ;
        try {
            type = materialInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }

        materialInventoryTransactionSourceType source = null;
        try {
            source = materialInventoryTransactionSourceType.valueOf(request.getTransactionSource());
        } catch (IllegalArgumentException e){
            System.err.println("Invalid Reference Document type" + request.getReferenceDocumentType());
        }
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_Id(variantId);
        
        MaterialInventory materialInventory;
        if (optionalInventory.isPresent()) {
            materialInventory = optionalInventory.get();
            BigDecimal afterQuantity = materialInventory.getCurrentStock().subtract(request.getNewQuantity());

            if (afterQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Cannot subtract. Stock would become negative.");
            }
                
            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                materialInventory,
                InventoryTransactionType.OUT,
                source,
                request.getNewQuantity(),
                materialInventory.getCurrentStock(),
                afterQuantity,
                request.getNote(),
                type,
                request.getReferenceDocumentId(),
                user
            );
            materialInventory.getLogs().add(inventoryLog);
            materialInventory.setCurrentStock(afterQuantity);
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
    public Page<SearchInventoryResponse> findMaterial(
        String search, 
        List<String> groupMaterials,
        List<String> inventoryStatus, 
        Pageable pageable) {
        System.out.println("=== FIND MATERIAL INVENTORY DEBUG ===");
        System.out.println("Search: " + search);
        System.out.println("Group Materials (String): " + groupMaterials);
        System.out.println("Inventory Status (String): " + inventoryStatus);
        System.out.println("Pageable: " + pageable);
        
        // Convert List<String> to List<Long> for groupMaterials
        List<Long> groupMaterialIds = null;
        if (groupMaterials != null && !groupMaterials.isEmpty()) {
            groupMaterialIds = groupMaterials.stream()
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
        
        System.out.println("Group Material IDs: " + groupMaterialIds);
        System.out.println("Inventory Status List: " + inventoryStatusList);
        
        Page<SearchInventoryResponse> result = materialInventoryRepository.searchInventoryByCriteria(
            search, groupMaterialIds, inventoryStatusList, pageable);
            
        System.out.println("Result total elements: " + result.getTotalElements());
        System.out.println("Result content size: " + result.getContent().size());
        System.out.println("Result content: " + result.getContent());
        System.out.println("=====================================");
        
        return result;
    }

    @Override
    public List<AllSearchInventoryResponse> getAllForAutocomplete(String search) {
        // handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<AllSearchInventoryResponse> response = materialInventoryRepository.searchInventory(processedSearch);
        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public MaterialInventoryDetailResponse getMaterialInventoryById(Long variantId) {
        MaterialInventory inventory = materialInventoryRepository.findByMaterialVariant_Id(variantId)
            .orElseThrow(() -> new RuntimeException("Material variant inventory not found with variant ID: " + variantId));

        MaterialVariant variant = inventory.getMaterialVariant();
        Material material = variant.getMaterial();
        
        // Get logs for this inventory with null safety
        List<InventoryTransactionResponse> logs = materialInventoryLogRepository
            .findByMaterialVariant_IdOrderByTransactionTimestampDesc(variantId)
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

        return new MaterialInventoryDetailResponse(
            variant.getId(),
            material.getName(),
            material.getSku(),
            variant.getSku(),
            variant.getName(),
            inventory.getCurrentStock(),
            inventory.getMinAlertStock(),
            inventory.getMaxStockLevel(),
            inventory.getInventoryStatus().name(),
            material.getGroup() != null ? material.getGroup().getName() : null,
            variant.getImageUrl(),
            logs
        );
    }


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
    public BulkMInitInventoryResponse bulkInitInventory(BulkMInitInventoryRequest request, User user) {
        List<AddMaterialInventoryResponse> successfulInits = new ArrayList<>();
        List<BulkMInitError> failedInits = new ArrayList<>();
        
        for (String materialVariantSku : request.getMaterialVariantSkus()) {
            try {
                AddMaterialInventoryRequest singleRequest = new AddMaterialInventoryRequest(
                    materialVariantSku,
                    request.getCurrentStock(),
                    request.getMinAlertStock(),
                    request.getMaxStockLevel()
                );
                
                AddMaterialInventoryResponse response = createMaterialInventory(singleRequest, user);
                successfulInits.add(response);
            } catch (Exception e) {
                failedInits.add(new BulkMInitError(
                    materialVariantSku,
                    "INIT_INVENTORY_FAILED",
                    e.getMessage()
                ));
            }
        }
        
        return new BulkMInitInventoryResponse(
            successfulInits,
            failedInits,
            request.getMaterialVariantSkus().size(),
            successfulInits.size(),
            failedInits.size()
        );
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

        List<EnumValue> inventoryReferenceDocumentTypes = new ArrayList<>();
        for (materialInventoryReferenceDocumentType type : materialInventoryReferenceDocumentType.values()) {
            inventoryReferenceDocumentTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getMaterialInventoryReferenceDocumentTypeDescription(type)
            ));
        }

        List<EnumValue> inventoryTransactionSourceTypes = new ArrayList<>();
        for (materialInventoryTransactionSourceType type : materialInventoryTransactionSourceType.values()) {
            inventoryTransactionSourceTypes.add(new EnumValue(
                type.name(),
                type.getDisplayName(),
                getMaterialInventoryTransactionSourceTypeDescription(type)
            ));
        }

        return new TransactionEnumsResponse(
            inventoryTransactionTypes,
            inventoryReferenceDocumentTypes,
            inventoryTransactionSourceTypes
        );
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

    private String getMaterialInventoryReferenceDocumentTypeDescription(materialInventoryReferenceDocumentType type) {
        switch (type) {
            case PURCHASING_TICKET:
                return "Reference to purchasing ticket document";
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

    private String getMaterialInventoryTransactionSourceTypeDescription(materialInventoryTransactionSourceType type) {
        switch (type) {
            case PURCHASE_RECEIPT:
                return "Stock received from purchase order";
            case PRODUCTION_RETURN:
                return "Stock return from cancelled production ticket";
            case MATERIAL_ADJUSTMENT_INCREASE:
                return "Stock increased through inventory adjustment";
            case MATERIAL_WAREHOUSE_TRANSFER_IN:
                return "Stock transferred in from warehouse";
            case PRODUCTION_CONSUMPTION:
                return "Stock reduced for production consumption";
            case SUPPLIER_RETURN:
                return "Stock reduced for supplier return";
            case MATERIAL_ADJUSTMENT_DECREASE:
                return "Stock decreased through inventory adjustment";
            case MATERIAL_WAREHOUSE_TRANSFER_OUT:
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
