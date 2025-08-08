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
import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.BulkInventoryTransactionResponse.BulkTransactionError;
import org.example.coretrack.dto.product.inventory.BulkStockModifyRequest;
import org.example.coretrack.dto.product.inventory.BulkStockSetRequest;
import org.example.coretrack.dto.product.inventory.InventoryEnumsResponse.EnumValue;
import org.example.coretrack.dto.product.inventory.InventoryTransactionResponse;
import org.example.coretrack.dto.product.inventory.SearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.StockModifyRequest;
import org.example.coretrack.dto.product.inventory.StockSetRequest;
import org.example.coretrack.dto.product.inventory.TransactionEnumsResponse;
import org.example.coretrack.dto.product.inventory.SetMinMaxRequest;
import org.example.coretrack.dto.product.inventory.SetMinMaxResponse;
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
import org.example.coretrack.model.product.inventory.StockType;
import org.example.coretrack.repository.MaterialInventoryLogRepository;
import org.example.coretrack.repository.MaterialInventoryRepository;
import org.example.coretrack.repository.MaterialRepository;
import org.example.coretrack.repository.MaterialVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.coretrack.exception.MaterialInventoryAlreadyExistsException;
import org.example.coretrack.service.NotificationTargetService;

@Service
public class MaterialInventoryServiceImpl implements MaterialInventoryService{
    @Autowired
    private MaterialVariantRepository materialVariantRepository;

    @Autowired
    private MaterialInventoryRepository materialInventoryRepository;

    @Autowired
    private MaterialInventoryLogRepository materialInventoryLogRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationTargetService notificationTargetService;

    @Autowired
    private EmailSendingService emailSendingService;

    @Override
    @Transactional
    public AddMaterialInventoryResponse createMaterialInventory(AddMaterialInventoryRequest request, User user) {
        MaterialVariant materialVariant = materialVariantRepository.findBySkuAndCompany(request.getMaterialVariantSku(), user.getCompany())
            .orElseThrow(() -> new RuntimeException("Material not found with SKU: " + request.getMaterialVariantSku()));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material was deleted, cannot initialize the stock quantity");
        }

        // Check if MaterialInventory already exists for this material variant
        Optional<MaterialInventory> existingInventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(materialVariant.getId(), user.getCompany());
        if (existingInventory.isPresent()) {
            throw new MaterialInventoryAlreadyExistsException("Material inventory already exists for SKU: " + request.getMaterialVariantSku() + 
                ". Cannot create duplicate inventory.");
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
            StockType.CURRENT,
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
        
        // Debug logs
        System.out.println("=== handelStatus DEBUG ===");
        System.out.println("currentQ: " + currentQ);
        System.out.println("futureQ: " + futureQ);
        System.out.println("allocatedQ: " + allocatedQ);
        System.out.println("minQ: " + minQ);
        System.out.println("maxQ: " + maxQ);
        System.out.println("available: " + available);
        System.out.println("projected: " + projected);
        System.out.println("==========================");
    
        if (available.compareTo(BigDecimal.ZERO) == 0) {
            return InventoryStatus.OUT_OF_STOCK;
        } else if (available.compareTo(minQ) < 0) {
            return InventoryStatus.LOW_STOCK;
        } else if (projected.compareTo(maxQ) > 0){
            return InventoryStatus.OVER_STOCK;
        }
        return InventoryStatus.IN_STOCK;
    }


    @Override
    @Transactional
    public InventoryTransactionResponse setStock(Long variantId, StockSetRequest request, User user) {
        MaterialVariant materialVariant = materialVariantRepository.findByIdAndCompany(variantId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Material not found with id: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material was deleted, cannot initialize the stock quantity");
        } 
        materialInventoryReferenceDocumentType type = null ;
        try {
            if (request.getReferenceDocumentType() != null && !request.getReferenceDocumentType().trim().isEmpty()) {
                type = materialInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type: " + request.getReferenceDocumentType());
        }
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantId, user.getCompany());
        
        MaterialInventory materialInventory;
        if (optionalInventory.isPresent()) {
            materialInventory = optionalInventory.get();
            BigDecimal result = request.getNewQuantity().subtract(materialInventory.getCurrentStock());

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                materialInventory,
                StockType.CURRENT,
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

            InventoryStatus oldStatus = materialInventory.getInventoryStatus();
            InventoryStatus status = handelStatus(materialInventory.getCurrentStock(), materialInventory.getFutureStock(), 
                                                    materialInventory.getAllocatedStock(), materialInventory.getMinAlertStock(), materialInventory.getMaxStockLevel());
            
            materialInventory.setInventoryStatus(status);
            
            // Save the inventory to persist the log - handle log errors gracefully
            try {
                materialInventoryRepository.save(materialInventory);
            } catch (Exception e) {
                // If log saving fails, still save the inventory without the log
                System.err.println("Failed to save log, but stock operation succeeded: " + e.getMessage());
                materialInventory.getLogs().clear(); // Remove the problematic log
                materialInventoryRepository.save(materialInventory);
            }
            
            // Create notification if status changed
            if (!oldStatus.equals(status)) {
                notificationService.createMaterialInventoryNotification(user, materialInventory, oldStatus, status);
                // Send email alerts for inventory status changes
                emailSendingService.sendMaterialInventoryStatusEmailAlert(materialInventory, oldStatus, status);
            }
            
            return new InventoryTransactionResponse(
                inventoryLog.getId(),
                inventoryLog.getTransactionType() != null ? inventoryLog.getTransactionType().toString() : "UNKNOWN",
                inventoryLog.getTransactionSourceType() != null ? inventoryLog.getTransactionSourceType().toString() : "UNKNOWN",
                result,
                inventoryLog.getBeforeQuantity(),
                inventoryLog.getAfterQuantity(),
                inventoryLog.getNote(),
                inventoryLog.getReferenceDocumentType() != null ? inventoryLog.getReferenceDocumentType().toString() : null,
                inventoryLog.getReferenceDocumentId(),
                inventoryLog.getStockType() != null ? inventoryLog.getStockType().getDisplayName() : "UNKNOWN",
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        } else {
            List<MaterialInventoryLog> inventoryLogList = new ArrayList<>();

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                null,
                StockType.CURRENT,
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

            InventoryStatus status = handelStatus(request.getNewQuantity(), null,null, null, null);
            

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
                inventoryLog.getStockType().getDisplayName(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        }
    }


    @Override
    @Transactional
    public InventoryTransactionResponse addStock(Long variantId, StockModifyRequest request, User user) {
        MaterialVariant materialVariant = materialVariantRepository.findByIdAndCompany(variantId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Material varian not found with id: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material variant was deleted, cannot initialize the stock quantity");
        } 
        materialInventoryReferenceDocumentType type = null ;
        try {
            if (request.getReferenceDocumentType() != null && !request.getReferenceDocumentType().trim().isEmpty()) {
                type = materialInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type: " + request.getReferenceDocumentType());
        }

        materialInventoryTransactionSourceType source = null;
        try {
            if (request.getTransactionSource() != null && !request.getTransactionSource().trim().isEmpty()) {
                source = materialInventoryTransactionSourceType.valueOf(request.getTransactionSource());
            }
        } catch (IllegalArgumentException e){
            System.err.println("Invalid Transaction Source: " + request.getTransactionSource());
        }
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantId, user.getCompany());
        
        MaterialInventory materialInventory;
        if (optionalInventory.isPresent()) {
            materialInventory = optionalInventory.get();
            BigDecimal result = request.getNewQuantity().add(materialInventory.getCurrentStock());

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                materialInventory,
                StockType.CURRENT,
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

            InventoryStatus oldStatus = materialInventory.getInventoryStatus();
            InventoryStatus status = handelStatus(result, materialInventory.getFutureStock(), materialInventory.getAllocatedStock()
                                                    , materialInventory.getMinAlertStock(), materialInventory.getMaxStockLevel());

            materialInventory.setInventoryStatus(status);
            
            // Save the inventory to persist the log - handle log errors gracefully
            try {
                materialInventoryRepository.save(materialInventory);
            } catch (Exception e) {
                // If log saving fails, still save the inventory without the log
                System.err.println("Failed to save log, but stock operation succeeded: " + e.getMessage());
                materialInventory.getLogs().clear(); // Remove the problematic log
                materialInventoryRepository.save(materialInventory);
            }
            
            // Create notification if status changed
            if (!oldStatus.equals(status)) {
                notificationService.createMaterialInventoryNotification(user, materialInventory, oldStatus, status);
                // Send email alerts for inventory status changes
                emailSendingService.sendMaterialInventoryStatusEmailAlert(materialInventory, oldStatus, status);
            }

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
                inventoryLog.getStockType().getDisplayName(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        } else {
            List<MaterialInventoryLog> inventoryLogList = new ArrayList<>();

            MaterialInventoryLog inventoryLog = new MaterialInventoryLog(
                LocalDateTime.now(),
                null,
                StockType.CURRENT,
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
            
            // Explicitly set futureStock and allocatedStock to zero
            materialInventory.setFutureStock(BigDecimal.ZERO);
            materialInventory.setAllocatedStock(BigDecimal.ZERO);
            
            // Save the inventory - logs will be saved automatically due to cascade
            materialInventoryRepository.save(materialInventory);

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
                inventoryLog.getStockType().getDisplayName(),
                LocalDateTime.now(),
                user.getUsername(),
                user.getRole().toString()
            );
        }
    }


    @Override
    @Transactional
    public InventoryTransactionResponse subtractStock(Long variantId, StockModifyRequest request, User user) {
        MaterialVariant materialVariant = materialVariantRepository.findByIdAndCompany(variantId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Material variant not found with id: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material variant was deleted, cannot initialize the stock quantity");
        } 
        materialInventoryReferenceDocumentType type = null ;
        try {
            if (request.getReferenceDocumentType() != null && !request.getReferenceDocumentType().trim().isEmpty()) {
                type = materialInventoryReferenceDocumentType.valueOf(request.getReferenceDocumentType().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Reference Document type: " + request.getReferenceDocumentType());
        }

        materialInventoryTransactionSourceType source = null;
        try {
            if (request.getTransactionSource() != null && !request.getTransactionSource().trim().isEmpty()) {
                source = materialInventoryTransactionSourceType.valueOf(request.getTransactionSource());
            }
        } catch (IllegalArgumentException e){
            System.err.println("Invalid Transaction Source: " + request.getTransactionSource());
        }
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantId, user.getCompany());
        
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
                StockType.CURRENT,
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
            
            // Update inventory status and create notification if needed
            InventoryStatus oldStatus = materialInventory.getInventoryStatus();
            InventoryStatus status = handelStatus(afterQuantity, materialInventory.getFutureStock(), materialInventory.getAllocatedStock()
                                                    , materialInventory.getMinAlertStock(), materialInventory.getMaxStockLevel());
            materialInventory.setInventoryStatus(status);
            
            // Save the inventory to persist the log - handle log errors gracefully
            try {
                materialInventoryRepository.save(materialInventory);
            } catch (Exception e) {
                // If log saving fails, still save the inventory without the log
                System.err.println("Failed to save log, but stock operation succeeded: " + e.getMessage());
                materialInventory.getLogs().clear(); // Remove the problematic log
                materialInventoryRepository.save(materialInventory);
            }
            
            // Create notification if status changed
            if (!oldStatus.equals(status)) {
                notificationService.createMaterialInventoryNotification(user, materialInventory, oldStatus, status);
                // Send email alerts for inventory status changes
                emailSendingService.sendMaterialInventoryStatusEmailAlert(materialInventory, oldStatus, status);
            }
            return new InventoryTransactionResponse(
                inventoryLog.getId(),
                inventoryLog.getTransactionType() != null ? inventoryLog.getTransactionType().toString() : "UNKNOWN",
                inventoryLog.getTransactionSourceType() != null ? inventoryLog.getTransactionSourceType().toString() : "UNKNOWN",
                inventoryLog.getQuantity(),
                inventoryLog.getBeforeQuantity(),
                inventoryLog.getAfterQuantity(),
                inventoryLog.getNote(),
                inventoryLog.getReferenceDocumentType() != null ? inventoryLog.getReferenceDocumentType().toString() : null,
                inventoryLog.getReferenceDocumentId(),
                inventoryLog.getStockType() != null ? inventoryLog.getStockType().getDisplayName() : "UNKNOWN",
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
        Pageable pageable,
        User user){
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
            search, groupMaterialIds, inventoryStatusList, user.getCompany(), pageable);
            
        System.out.println("Result total elements: " + result.getTotalElements());
        System.out.println("Result content size: " + result.getContent().size());
        System.out.println("Result content: " + result.getContent());
        
        // Debug: Check if there are any material inventories in database
        long totalMaterialInventories = materialInventoryRepository.count();
        System.out.println("Total MaterialInventories in database: " + totalMaterialInventories);
        
        // Debug: Check if there are any material variants in database
        long totalMaterialVariants = materialVariantRepository.count();
        System.out.println("Total MaterialVariants in database: " + totalMaterialVariants);
        
        // Debug: Check if there are any materials in database
        long totalMaterials = materialRepository.count();
        System.out.println("Total Materials in database: " + totalMaterials);
        
        // Debug: Check materials with groups
        List<Material> materialsWithGroups = materialRepository.findAll().stream()
            .filter(m -> m.getGroup() != null)
            .collect(Collectors.toList());
        System.out.println("Materials with groups: " + materialsWithGroups.size());
        
        // Debug: Check materials without groups
        List<Material> materialsWithoutGroups = materialRepository.findAll().stream()
            .filter(m -> m.getGroup() == null)
            .collect(Collectors.toList());
        System.out.println("Materials without groups: " + materialsWithoutGroups.size());
        
        // Debug: Check material status
        List<Material> activeMaterials = materialRepository.findAll().stream()
            .filter(m -> m.isActive())
            .collect(Collectors.toList());
        System.out.println("Active materials: " + activeMaterials.size());
        
        List<Material> deletedMaterials = materialRepository.findAll().stream()
            .filter(m -> m.getStatus() == MaterialStatus.DELETED)
            .collect(Collectors.toList());
        System.out.println("Deleted materials: " + deletedMaterials.size());
        
        System.out.println("=====================================");
        
        return result;
    }

    @Override
    public List<AllSearchInventoryResponse> getAllForAutocomplete(String search, User user){
        // handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<AllSearchInventoryResponse> response = materialInventoryRepository.searchInventory(processedSearch,user.getCompany());
        return response;
    }

    @Transactional
    @Override
    public MaterialInventoryDetailResponse getMaterialInventoryById(Long variantId, User user) {
        MaterialInventory inventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Material variant inventory not found with variant ID: " + variantId));

        MaterialVariant variant = inventory.getMaterialVariant();
        Material material = variant.getMaterial();
        
        // Debug: Log the inventory ID
        System.out.println("DEBUG: MaterialInventory ID: " + inventory.getId());
        System.out.println("DEBUG: MaterialVariant ID: " + variantId);
        
        // Debug: Check currentStock value
        System.out.println("DEBUG: Current Stock from DB: " + inventory.getCurrentStock());
        System.out.println("DEBUG: Current Stock type: " + (inventory.getCurrentStock() != null ? inventory.getCurrentStock().getClass().getName() : "NULL"));
        System.out.println("DEBUG: Current Stock equals zero: " + (inventory.getCurrentStock() != null ? inventory.getCurrentStock().equals(BigDecimal.ZERO) : "NULL"));
        
        // Debug: Check total logs in database
        long totalLogs = materialInventoryLogRepository.count();
        System.out.println("DEBUG: Total logs in database: " + totalLogs);
        
        // Debug: Check logs for this specific material inventory
        List<MaterialInventoryLog> allLogs = materialInventoryLogRepository.findAll();
        long logsForThisInventory = allLogs.stream()
            .filter(log -> log.getMaterialInventory() != null && log.getMaterialInventory().getId().equals(inventory.getId()))
            .count();
        System.out.println("DEBUG: Logs for this inventory (ID " + inventory.getId() + "): " + logsForThisInventory);
        
        // Get logs for this inventory with null safety - try JPA query
        List<MaterialInventoryLog> rawLogs = materialInventoryLogRepository
            .findByMaterialInventory_IdOrderByTransactionTimestampDesc(inventory.getId());
        
        System.out.println("DEBUG: Raw logs count (JPA query): " + rawLogs.size());
        
        // Debug: Print each log details
        for (MaterialInventoryLog log : rawLogs) {
            System.out.println("DEBUG: Log ID: " + log.getId() + 
                             ", MaterialInventory ID: " + (log.getMaterialInventory() != null ? log.getMaterialInventory().getId() : "NULL") +
                             ", Transaction Type: " + log.getTransactionType() +
                             ", Quantity: " + log.getQuantity());
        }
        
        List<InventoryTransactionResponse> logs = rawLogs
            .stream()
            .filter(log -> log != null) // Filter out null logs
            .map(log -> {
                try {
                    System.out.println("DEBUG: Processing log ID: " + log.getId());
                    return new InventoryTransactionResponse(log);
                } catch (Exception e) {
                    // Log the error but continue processing other logs
                    System.err.println("Error processing inventory log: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(response -> response != null) // Filter out null responses
            .collect(Collectors.toList());
        
        System.out.println("DEBUG: Final logs count: " + logs.size());

        MaterialInventoryDetailResponse response = new MaterialInventoryDetailResponse(
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
        
        // Debug: Log the response values
        System.out.println("DEBUG: Response currentStock: " + response.getCurrentStock());
        System.out.println("DEBUG: Response currentStock type: " + (response.getCurrentStock() != null ? response.getCurrentStock().getClass().getName() : "NULL"));
        
        return response;
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


    @Override
    public MaterialInventory addToFutureStock(Long variantId, BigDecimal quantity, User user, Long ticketId) {
        MaterialInventory inventory = getByMaterialVariantId(variantId, user);
        if (inventory == null) {
            throw new RuntimeException("MaterialInventory not found for variant ID: " + variantId);
        }

        // Store old values for logging
        BigDecimal oldFutureStock = inventory.getFutureStock() != null ? inventory.getFutureStock() : BigDecimal.ZERO;
        BigDecimal newFutureStock = oldFutureStock.add(quantity);

        // Add to future stock
        inventory.setFutureStock(newFutureStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Create log for this operation
        MaterialInventoryLog log = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.FUTURE,
            InventoryTransactionType.IN,
            materialInventoryTransactionSourceType.PURCHASE_RECEIPT,
            quantity,
            oldFutureStock,
            newFutureStock,
            "Future stock added for purchasing ticket",
            materialInventoryReferenceDocumentType.PURCHASING_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log);
        return materialInventoryRepository.save(inventory);
    }

    @Override
    public MaterialInventory moveFromFutureToCurrentStock(Long variantId, BigDecimal quantity, User user,
            Long ticketId) {
        MaterialInventory inventory = getByMaterialVariantId(variantId, user);
        if (inventory == null) {
            throw new RuntimeException("MaterialInventory not found for variant ID: " + variantId);
        }

        // Validate future stock is sufficient
        BigDecimal currentFutureStock = inventory.getFutureStock() != null ? inventory.getFutureStock() : BigDecimal.ZERO;
        BigDecimal newFutureStock;
        if (currentFutureStock.compareTo(quantity) < 0) {
            newFutureStock = BigDecimal.ZERO;
        } else {
            newFutureStock = currentFutureStock.subtract(quantity);
        }

        // Store old values for logging
        BigDecimal oldFutureStock = currentFutureStock;
        BigDecimal oldCurrentStock = inventory.getCurrentStock() != null ? inventory.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal newCurrentStock = oldCurrentStock.add(quantity);

        // Move from future to current stock
        inventory.setFutureStock(newFutureStock);
        inventory.setCurrentStock(newCurrentStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Create log of future for this operation
        MaterialInventoryLog log = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.FUTURE,
            InventoryTransactionType.IN,
            materialInventoryTransactionSourceType.PURCHASE_RECEIPT,
            quantity,
            oldFutureStock,
            newFutureStock,
            "Stock moved from future to current for purchasing ticket",
            materialInventoryReferenceDocumentType.PURCHASING_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log);

        // Create log of current for this operation
        MaterialInventoryLog log2 = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.CURRENT,
            InventoryTransactionType.IN,
            materialInventoryTransactionSourceType.PURCHASE_RECEIPT,
            quantity,
            oldCurrentStock,
            newCurrentStock,
            "Stock moved from future to current for purchasing ticket",
            materialInventoryReferenceDocumentType.PURCHASING_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log2);
        return materialInventoryRepository.save(inventory);
    }


    @Override
    public MaterialInventory addToAllocatedStock(Long variantId, BigDecimal quantity, User user, Long ticketId) {
        MaterialInventory inventory = getByMaterialVariantId(variantId, user);
        if (inventory == null) {
            throw new RuntimeException("MaterialInventory not found for variant ID: " + variantId);
        }

       BigDecimal currentAllocatedStock = inventory.getAllocatedStock() != null ? inventory.getAllocatedStock() : BigDecimal.ZERO;

        // Store old values for logging
        BigDecimal oldAllocatedStock = currentAllocatedStock;
        BigDecimal newAllocatedStock = quantity;

        // Add to allocated stock
        inventory.setAllocatedStock(oldAllocatedStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Create log for this operation
        MaterialInventoryLog log = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.ALLOCATED,
            InventoryTransactionType.OUT,
            materialInventoryTransactionSourceType.PRODUCTION_CONSUMPTION,
            quantity,
            oldAllocatedStock,
            newAllocatedStock,
            "Stock allocated for the production",
            materialInventoryReferenceDocumentType.PRODUCTION_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log);
        return materialInventoryRepository.save(inventory);    
    }


    @Override
    public MaterialInventory removeFromCurrentStock(Long variantId, BigDecimal quantity, User user, Long ticketId) {
        MaterialInventory inventory = getByMaterialVariantId(variantId, user);
        if (inventory == null) {
            throw new RuntimeException("MaterialInventory not found for variant ID: " + variantId);
        }

        // Validate current stock is sufficient
        BigDecimal oldCurrentStock = inventory.getCurrentStock() != null ? inventory.getCurrentStock() : BigDecimal.ZERO;
        if (oldCurrentStock.compareTo(quantity) < 0) {
            inventory.setCurrentStock(BigDecimal.ZERO);
        } else{
            BigDecimal newCurrentStock = oldCurrentStock.subtract(quantity);
            inventory.setCurrentStock(newCurrentStock);
        }
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Create log for this operation
        MaterialInventoryLog log = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.CURRENT,
            InventoryTransactionType.OUT,
            materialInventoryTransactionSourceType.PURCHASE_RECEIPT,
            quantity,
            oldCurrentStock,
            inventory.getCurrentStock(),
            "Current stock removed for purchasing ticket cancellation",
            materialInventoryReferenceDocumentType.PURCHASING_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log);
        return materialInventoryRepository.save(inventory);    
    }

    @Transactional(readOnly = true)
    public MaterialInventory getByMaterialVariantId(Long variantId, User user) {
        return materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantId, user.getCompany()).orElse(null);
    }

    
    @Override
    public boolean isEnough(MaterialVariant variant, BigDecimal plannedQuantity, User user){
        boolean isEnough = true;
        
        // Try to find the material inventory
        MaterialInventory inventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variant.getId(), user.getCompany())
                                    .orElse(null);
        
        // If inventory doesn't exist, create it with default values
        if (inventory == null) {
            System.out.println("Material inventory not found for variant ID: " + variant.getId() + ", creating default inventory");
            inventory = new MaterialInventory();
            inventory.setMaterialVariant(variant);
            inventory.setCurrentStock(BigDecimal.ZERO);
            inventory.setFutureStock(BigDecimal.ZERO);
            inventory.setAllocatedStock(BigDecimal.ZERO);
            inventory.setInventoryStatus(InventoryStatus.OUT_OF_STOCK);
            inventory.setActive(true);
            inventory.setCreatedAt(LocalDateTime.now());
            inventory.setCreated_by(user);
            inventory.setUpdatedAt(LocalDateTime.now());
            inventory.setUpdated_by(user);
            
            // Save the new inventory
            inventory = materialInventoryRepository.save(inventory);
            System.out.println("Created default material inventory with ID: " + inventory.getId());
        }
        
        // Handle null values safely
        BigDecimal futureStock = inventory.getFutureStock() != null ? inventory.getFutureStock() : BigDecimal.ZERO;
        BigDecimal allocatedStock = inventory.getAllocatedStock() != null ? inventory.getAllocatedStock() : BigDecimal.ZERO;
        
        BigDecimal savedStock = futureStock.subtract(allocatedStock);
        if(savedStock.compareTo(plannedQuantity) < 0){
            isEnough = false;
        }
        
        return isEnough;
    }

    @Override
    public BigDecimal getAvailableStock(MaterialVariant variant, User user) {
        // Try to find the material inventory
        MaterialInventory inventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variant.getId(), user.getCompany())
                                    .orElse(null);
        
        // If inventory doesn't exist, return zero
        if (inventory == null) {
            return BigDecimal.ZERO;
        }
        
        // Handle null values safely
        BigDecimal futureStock = inventory.getFutureStock() != null ? inventory.getFutureStock() : BigDecimal.ZERO;
        BigDecimal allocatedStock = inventory.getAllocatedStock() != null ? inventory.getAllocatedStock() : BigDecimal.ZERO;
        
        // Available stock = future stock - allocated stock
        return futureStock.subtract(allocatedStock);
    }

    @Override
    public MaterialInventory removeFromFutureStock (Long variantId, BigDecimal quantity, User user, Long ticketId){
        MaterialInventory inventory = getByMaterialVariantId(variantId, user);
        if (inventory == null) {
            throw new RuntimeException("MaterialInventory not found for variant ID: " + variantId);
        }

        // Validate current stock is sufficient
        BigDecimal oldCurrentFutureStock = inventory.getFutureStock() != null ? inventory.getFutureStock() : BigDecimal.ZERO;
        if (oldCurrentFutureStock.compareTo(quantity) < 0) {
            inventory.setFutureStock(BigDecimal.ZERO);
        } else {
            BigDecimal newFutureStock = oldCurrentFutureStock.subtract(quantity);
            inventory.setFutureStock(newFutureStock);
        }
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Create log for this operation
        MaterialInventoryLog log = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.FUTURE,
            InventoryTransactionType.OUT,
            materialInventoryTransactionSourceType.PURCHASE_RECEIPT,
            quantity,
            oldCurrentFutureStock,
            inventory.getFutureStock(),
            "Future stock removed for purchasing ticket cancellation",
            materialInventoryReferenceDocumentType.PURCHASING_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log);
        return materialInventoryRepository.save(inventory);  
    }

    @Override
    public MaterialInventory removeFromCurrentAndAllocatedStock(Long variantId, BigDecimal quantity, User user, Long ticketId) {
        MaterialInventory inventory = getByMaterialVariantId(variantId, user);
        if (inventory == null) {
            throw new RuntimeException("MaterialInventory not found for variant ID: " + variantId);
        }
        // Store old values for logging
        BigDecimal oldCurrentStock = inventory.getCurrentStock() != null ? inventory.getCurrentStock() : BigDecimal.ZERO;
        

        if (oldCurrentStock.compareTo(quantity) < 0) {
            inventory.setCurrentStock(BigDecimal.ZERO);
        } else {
            BigDecimal newCurrentStock = oldCurrentStock.subtract(quantity);
            inventory.setCurrentStock(newCurrentStock);
        }
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Create log for this operation
        MaterialInventoryLog log = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.CURRENT,
            InventoryTransactionType.OUT,
            materialInventoryTransactionSourceType.PRODUCTION_CONSUMPTION,
            quantity,
            oldCurrentStock,
            inventory.getCurrentStock(),
            "Current stock removed for production ticket ",
            materialInventoryReferenceDocumentType.PRODUCTION_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log);

        // Store old Allocated Stock
        BigDecimal oldAllocatedStock = inventory.getAllocatedStock() != null ? inventory.getAllocatedStock() : BigDecimal.ZERO;
        

        if (oldAllocatedStock.compareTo(quantity) < 0) {
            inventory.setAllocatedStock(BigDecimal.ZERO);
        } else {
            BigDecimal newAllocatedStock = oldAllocatedStock.subtract(quantity);
            inventory.setAllocatedStock(newAllocatedStock);
        }

        // Create log for this operation
        MaterialInventoryLog log2 = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.ALLOCATED,
            InventoryTransactionType.OUT,
            materialInventoryTransactionSourceType.PRODUCTION_CONSUMPTION,
            quantity,
            oldAllocatedStock,
            inventory.getAllocatedStock(),
            "Allocated stock removed for production ticket complete ",
            materialInventoryReferenceDocumentType.PRODUCTION_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log2);
        return materialInventoryRepository.save(inventory);    
    }

    @Override
    public MaterialInventory removeFromAllocatedStock (Long variantId, BigDecimal quantity, User user, Long ticketId){
        MaterialInventory inventory = getByMaterialVariantId(variantId, user);
        if (inventory == null) {
            throw new RuntimeException("MaterialInventory not found for variant ID: " + variantId);
        }

        // Validate current stock is sufficient
        BigDecimal currentAllocatedStock = inventory.getAllocatedStock() != null ? inventory.getAllocatedStock() : BigDecimal.ZERO;
        if (currentAllocatedStock.compareTo(quantity) < 0) {
            inventory.setAllocatedStock(BigDecimal.ZERO);
        } else {
            BigDecimal newAllocatedStock = currentAllocatedStock.subtract(quantity);
            inventory.setAllocatedStock(newAllocatedStock);
        }
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Create log for this operation
        MaterialInventoryLog log = new MaterialInventoryLog(
            LocalDateTime.now(),
            inventory,
            StockType.ALLOCATED,
            InventoryTransactionType.IN,
            materialInventoryTransactionSourceType.PRODUCTION_RETURN,
            quantity,
            currentAllocatedStock,
            inventory.getAllocatedStock(),
            "Allocated stock removed for production ticket cancellation",
            materialInventoryReferenceDocumentType.PRODUCTION_TICKET,
            ticketId, // Use the provided ticketId
            user
        );

        inventory.getLogs().add(log);
        return materialInventoryRepository.save(inventory);  
    }

    @Override
    public Page<SearchInventoryResponse> getAlarmMaterial(String search, List<Long> groupMaterials, List<String> status, boolean sortByOldest, Pageable pageable, User user){
        System.out.println("=== GET ALARM MATERIALS DEBUG ===");
        System.out.println("Search: " + search);
        System.out.println("Group Materials: " + groupMaterials);
        System.out.println("Status: " + status);
        System.out.println("Sort by oldest: " + sortByOldest);
        System.out.println("Pageable: " + pageable);

        // Convert status strings to InventoryStatus enums
        List<InventoryStatus> statusEnums = null;
        if (status != null && !status.isEmpty()) {
            statusEnums = status.stream()
                .map(s -> {
                    try {
                        return InventoryStatus.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid inventory status: " + s);
                        return null;
                    }
                })
                .filter(s -> s != null)
                .collect(Collectors.toList());
        }

        // Filter to only alarm statuses if no specific status provided
        if (statusEnums == null || statusEnums.isEmpty()) {
            statusEnums = List.of(
                InventoryStatus.OUT_OF_STOCK,
                InventoryStatus.LOW_STOCK,
                InventoryStatus.OVER_STOCK
            );
        }

        Page<Object[]> rawResults = materialInventoryRepository.searchAlarmInventoryWithUpdatedAt(
            search, groupMaterials, statusEnums, sortByOldest, user.getCompany(),pageable);

        Page<SearchInventoryResponse> result = rawResults.map(row -> {
            MaterialInventory inventory = (MaterialInventory) row[0];
            MaterialVariant variant = inventory.getMaterialVariant();
            Material material = variant.getMaterial();

            return new SearchInventoryResponse(
                variant.getId(),
                variant.getSku(),
                variant.getName(),
                material.getGroup() != null ? material.getGroup().getName() : "",
                inventory.getInventoryStatus(),
                inventory.getCurrentStock(),
                inventory.getMinAlertStock(),
                inventory.getMaxStockLevel(),
                variant.getImageUrl(),
                inventory.getUpdatedAt()
            );
        });

        System.out.println("Alarm Result total elements: " + result.getTotalElements());
        System.out.println("Alarm Result content size: " + result.getContent().size());
        System.out.println("Alarm Result content: " + result.getContent());

        return result;
    }

    @Override
    @Transactional
    public SetMinMaxResponse setMinimumAlertStock(Long variantId, SetMinMaxRequest request, User user) {
        // First check if MaterialVariant exists
        MaterialVariant materialVariant = materialVariantRepository.findByIdAndCompany(variantId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Material variant not found with ID: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material variant was deleted, cannot set minimum alert stock");
        }

        // Check if MaterialInventory exists for this variant
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantId, user.getCompany());
        MaterialInventory inventory;
        BigDecimal oldMinAlertStock = null;

        if (optionalInventory.isPresent()) {
            // MaterialInventory exists, update it
            inventory = optionalInventory.get();
            oldMinAlertStock = inventory.getMinAlertStock();
        } else {
            // MaterialInventory doesn't exist, create a new empty one
            inventory = new MaterialInventory(
                materialVariant,
                BigDecimal.ZERO, // currentStock
                request.getValue(), // minAlertStock
                null, // maxStockLevel
                new ArrayList<>(), // logs
                InventoryStatus.OUT_OF_STOCK, // status
                user
            );
            // Explicitly set currentStock to ensure it's saved
            inventory.setCurrentStock(BigDecimal.ZERO);
            oldMinAlertStock = null;
        }

        // Update minimum alert stock
        inventory.setMinAlertStock(request.getValue());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Save inventory
        MaterialInventory savedInventory = materialInventoryRepository.save(inventory);

        return new SetMinMaxResponse(
            savedInventory.getId(),
            savedInventory.getMaterialVariant().getSku(),
            savedInventory.getMaterialVariant().getName(),
            oldMinAlertStock,
            request.getValue(),
            "Minimum alert stock updated successfully",
            user.getUsername(),
            LocalDateTime.now()
        );
    }

    @Override
    @Transactional
    public SetMinMaxResponse setMaximumStockLevel(Long variantId, SetMinMaxRequest request, User user) {
        // First check if MaterialVariant exists
        MaterialVariant materialVariant = materialVariantRepository.findByIdAndCompany(variantId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Material variant not found with ID: " + variantId));

        if (materialVariant.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material variant was deleted, cannot set maximum stock level");
        }

        // Check if MaterialInventory exists for this variant
        Optional<MaterialInventory> optionalInventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantId, user.getCompany());
        MaterialInventory inventory;
        BigDecimal oldMaxStockLevel = null;

        if (optionalInventory.isPresent()) {
            // MaterialInventory exists, update it
            inventory = optionalInventory.get();
            oldMaxStockLevel = inventory.getMaxStockLevel();
        } else {
            // MaterialInventory doesn't exist, create a new empty one
            inventory = new MaterialInventory(
                materialVariant,
                BigDecimal.ZERO, // currentStock
                null, // minAlertStock
                request.getValue(), // maxStockLevel
                new ArrayList<>(), // logs
                InventoryStatus.OUT_OF_STOCK, // status
                user
            );
            // Explicitly set currentStock to ensure it's saved
            inventory.setCurrentStock(BigDecimal.ZERO);
            oldMaxStockLevel = null;
        }

        // Update maximum stock level
        inventory.setMaxStockLevel(request.getValue());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setUpdated_by(user);

        // Save inventory
        MaterialInventory savedInventory = materialInventoryRepository.save(inventory);

        return new SetMinMaxResponse(
            savedInventory.getId(),
            savedInventory.getMaterialVariant().getSku(),
            savedInventory.getMaterialVariant().getName(),
            oldMaxStockLevel,
            request.getValue(),
            "Maximum stock level updated successfully",
            user.getUsername(),
            LocalDateTime.now()
        );
    }

    private boolean isAlarmStatus(InventoryStatus status) {
        return status == InventoryStatus.OUT_OF_STOCK || 
               status == InventoryStatus.LOW_STOCK || 
               status == InventoryStatus.OVER_STOCK;
    }


}
