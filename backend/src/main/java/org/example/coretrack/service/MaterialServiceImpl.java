package org.example.coretrack.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.Map;

import org.example.coretrack.dto.material.AddMaterialRequest;
import org.example.coretrack.dto.material.AddMaterialResponse;
import org.example.coretrack.dto.material.AllMaterialSearchResponse;
import org.example.coretrack.dto.material.MaterialDetailResponse;
import org.example.coretrack.dto.material.MaterialGroupResponse;
import org.example.coretrack.dto.material.MaterialSupplierResponse;
import org.example.coretrack.dto.material.MaterialVariantInventoryResponse;
import org.example.coretrack.dto.material.MaterialVariantResponse;
import org.example.coretrack.dto.material.SearchMaterialResponse;
import org.example.coretrack.dto.material.UpdateMaterialRequest;
import org.example.coretrack.dto.material.UpdateMaterialResponse;
import org.example.coretrack.dto.material.ChangeMaterialStatusRequest;
import org.example.coretrack.dto.material.ChangeMaterialStatusResponse;
import org.example.coretrack.dto.material.MaterialStatusTransitionResponse;
import org.example.coretrack.dto.product.InventoryResponse;
import org.example.coretrack.dto.material.MaterialVariantAutoCompleteResponse;
import org.example.coretrack.dto.material.UpdateMaterialVariantRequest;
import org.example.coretrack.dto.material.DeleteMaterialResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;
import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.material.MaterialGroup;
import org.example.coretrack.model.material.MaterialStatus;
import org.example.coretrack.model.material.MaterialStatusAuditLog;
import org.example.coretrack.model.material.MaterialVariant;
import org.example.coretrack.model.material.UoM;
import org.example.coretrack.model.supplier.MaterialSupplier;
import org.example.coretrack.model.supplier.MaterialSupplierId;
import org.example.coretrack.model.supplier.Supplier;
import org.example.coretrack.repository.MaterialGroupRepository;
import org.example.coretrack.repository.MaterialRepository;
import org.example.coretrack.repository.MaterialStatusAuditLogRepository;
import org.example.coretrack.repository.MaterialSupplierRepository;
import org.example.coretrack.repository.MaterialVariantRepository;
import org.example.coretrack.repository.SupplierRepository;
import org.example.coretrack.repository.MaterialInventoryRepository;
import org.example.coretrack.repository.MaterialInventoryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.transaction.Transactional;

@Service
public class MaterialServiceImpl implements MaterialService{

    @Autowired 
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialGroupRepository materialGroupRepository;

    @Autowired
    private MaterialVariantRepository materialVariantRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private MaterialSupplierRepository materialSupplierRepository;

    @Autowired
    private MaterialStatusAuditLogRepository materialStatusAuditLogRepository;

    @Autowired
    private MaterialStatusValidator materialStatusValidator;

    @Autowired
    private MaterialInventoryRepository materialInventoryRepository;

    @Autowired
    private MaterialInventoryLogRepository materialInventoryLogRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")  
    public AddMaterialResponse createMaterial(AddMaterialRequest request, User createdByUser) {
        
        // 1. Handle Material Group (Main Flow - Step 4, A5)
        // Initialize as null
        MaterialGroup materialGroup = null;
        if (request.getMaterialGroupId() != null) {
            // Choose Existing Material Group (A5.1)
            Long materialGroupId = Long.parseLong(request.getMaterialGroupId());
            materialGroup = materialGroupRepository.findByIdAndCompanyAndIsActiveTrue(materialGroupId, createdByUser.getCompany())
                    .orElseThrow(() -> new RuntimeException("Material Group not found with ID: " + request.getMaterialGroupId()));
        } else {
            // Create New Material Group (A5.2)
            if (StringUtils.hasText(request.getNewMaterialGroupName())) {
                // Check if group name already exists in the same company
                if (materialGroupRepository.findByNameAndCompany(request.getNewMaterialGroupName(), createdByUser.getCompany()).isPresent()) {
                    throw new RuntimeException("Material Group name already exists: " + request.getNewMaterialGroupName());
                }
                materialGroup = new MaterialGroup(request.getNewMaterialGroupName(), createdByUser, createdByUser.getCompany());
                materialGroupRepository.save(materialGroup);
            }
        }

        // 2. Handle SKU Generation (Main Flow - Step 1)
        String sku;
        if (StringUtils.hasText(request.getSku())) {
            // Manual SKU Input (A2)
            String manualSku = request.getSku().trim();
            // E2: Duplicate or Invalid SKU - uniqueness
            if (materialRepository.findBySkuAndCompany(manualSku, createdByUser.getCompany()).isPresent()) {
                throw new IllegalArgumentException("Material SKU already exists: " + manualSku);
            }
            // E2: Invalid SKU format - length (8-12 chars for tidy SKU)
            if (manualSku.length() < 8 || manualSku.length() > 12) {
                 throw new IllegalArgumentException("Manual SKU must be between 8 and 12 characters.");
            }
            sku = manualSku;
        } else {
            // Automatic SKU Generation (A1)
            // Generate a 12-unit SKU
            sku = generateUniqueMaterialSku(createdByUser.getCompany()); 
        }
    
        // 3. Create Material Entity (Main Flow - Step 2)        
        Material material = new Material(
            sku,
            request.getName(),
            request.getShortDes(),
            materialGroup,
            createdByUser,
            createdByUser.getCompany()
        );
        String uomStr = request.getUom(); 
        if (!StringUtils.hasText(uomStr)) {
            throw new IllegalArgumentException("UOM cannot be empty");
        }
        UoM uom;
        try {
            uom = UoM.valueOf(uomStr.toUpperCase()); 
            material.setUom(uom);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid unit of measure: " + uomStr);
        }
        
        if(StringUtils.hasText(request.getImageUrl())){
            material.setImageUrl(request.getImageUrl());
        }

        materialRepository.save(material); // Save material to get its ID for relationships

        // 4. Define Variant(s) (Main Flow - Step 4)
    List<MaterialVariant> allMaterialVariants = new java.util.ArrayList<>();

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            // Material has variants
            AtomicInteger skuCounter = new AtomicInteger(1);
            List<MaterialVariant> variants = request.getVariants().stream()
                .map(variantRequest -> {
                    int index = skuCounter.getAndIncrement();
                    MaterialVariant variant = new MaterialVariant(
                        null,// SKU will be generated later
                        variantRequest.getName(),
                        variantRequest.getShortDescription(),
                        material.getUom(), // Link to the base material
                        material,
                        createdByUser
                    );
                    if(StringUtils.hasText(variantRequest.getImageUrl())){
                        variant.setImageUrl(variantRequest.getImageUrl());
                    }
                    // Generate SKU for variant (e.g., adding "-1", "-2" suffixes)
                    variant.setSku(generateVariantSku(material.getSku(), index, createdByUser.getCompany()));
                    return variant;
                }).collect(Collectors.toList());

            material.setVariants(variants); // Set variants on the product
            materialVariantRepository.saveAll(variants); // Save all variants
            allMaterialVariants.addAll(variants);
        } else {
            // Product has no explicit variants, create a default variant for the base product
            MaterialVariant defaultVariant = new MaterialVariant(
                material.getSku(),
                material.getName(),
                material.getShortDes(),
                material.getUom(),
                material,
                createdByUser
            );
            if(StringUtils.hasText(material.getImageUrl())){
                defaultVariant.setImageUrl(material.getImageUrl());
            }
            materialVariantRepository.save(defaultVariant);
            List<MaterialVariant> variants = new ArrayList<>();
            variants.add(defaultVariant);
            material.setVariants(variants);// Add to product's variants list
            allMaterialVariants.add(defaultVariant);
        }

        // 4. Define Supplier(s) (Main Flow - Step 5)
        if (request.getSuppliers() != null && !request.getSuppliers().isEmpty()) {
            
            // Material has suppliers - now material has ID
            Set<MaterialSupplier> suppliers = request.getSuppliers().stream()
                .map(supplierRequest -> {
                    Supplier supplier = supplierRepository.findById(supplierRequest.getSupplierId())
                                                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierRequest.getSupplierId()));
                    if (supplierRequest.getPrice() == null || supplierRequest.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Supplier price must be a positive value");
                    }

                    // Validate currency
                    String currencyCode = supplierRequest.getCurrency();
                    if (!StringUtils.hasText(currencyCode)) {
                        throw new IllegalArgumentException("Currency code cannot be empty");
                    }
                    try {
                        Currency.getInstance(currencyCode.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid currency code: " + currencyCode);
                    }

                    MaterialSupplier materialSupplier = new MaterialSupplier(
                        material,
                        supplier,
                        supplierRequest.getPrice(),
                        supplierRequest.getCurrency(),
                        createdByUser
                    );
                    
                    // Set the ID manually since we have the IDs now
                    materialSupplier.setId(new MaterialSupplierId(material.getId(), supplier.getId()));
                    
                    System.out.println("Created MaterialSupplier: " + materialSupplier.getId() + 
                                     " for Material: " + material.getId() + 
                                     " and Supplier: " + supplier.getId());
                    return materialSupplier;
                }).collect(Collectors.toSet());

            // Save MaterialSupplier entities directly without affecting the material's collection
            materialSupplierRepository.saveAll(suppliers);
        }
        
        // Save the material entity to persist all relationships
        materialRepository.save(material);

        return mapProductToProductResponse(material);
    }

    // --- Helper methods for SKU generation ---
    private String generateUniqueMaterialSku(Company company) {
        String newSku;
        Random random = new Random(); // Initialize Random
        do {
            // Generate a 7-digit random number
            // 1000000 is the smallest 7-digit number, 9999999 is the largest
            int randomNumber = 1000000 + random.nextInt(9000000); // Generates number between 1,000,000 and 9,999,999
            newSku = "MAT-" + randomNumber; // Example: MAT-1234567
        } while (materialRepository.findBySkuAndCompany(newSku, company).isPresent());
        return newSku;
    }

    private String generateVariantSku(String baseMaterialSku, int variantIndex, Company company) {
        // This is two layer to ensure that SKU of variant is unique in case 
        // some error happen while a lot of modify/ delete happened
        // Example: adding "-1", "-2" suffixes (Main Flow - Step 4)
        if (baseMaterialSku.length() > 12) {
            baseMaterialSku = baseMaterialSku.substring(0, 12);
        }
        String suffix = "-" + variantIndex;
        String variantSku = baseMaterialSku + suffix;

         // If still too long, truncate
        if (variantSku.length() > 16) {
            variantSku = variantSku.substring(0, 16);
        }

        // Ensure uniqueness for variant SKU as well
        String finalVariantSku;
        int attempt = 0;
        Random random = new Random(); // Initialize Random for unique part
        do {
            finalVariantSku = variantSku;
            if (attempt > 0) { // Add a unique identifier if initial SKU is not unique
                // Generate a 4-digit random number for unique part
                String uniquePart = "-" + (1000 + random.nextInt(9000)); // Generates number between 1000 and 9999
                finalVariantSku += uniquePart; // No length truncation here
            }
            attempt++;
        } while (materialVariantRepository.findBySkuAndCompany(finalVariantSku, company).isPresent());

        return finalVariantSku;
    }

    private AddMaterialResponse mapProductToProductResponse(Material material) {
        List<MaterialVariantResponse> variantResponses = material.getVariants() != null ?
                material.getVariants().stream()
                        .map(variant -> {
                            return new MaterialVariantResponse(variant);
                        }).toList() : List.of();

        List<MaterialSupplierResponse> supplierResponses = materialSupplierRepository.findByMaterial(material)
                .stream()
                .map(supplier -> new MaterialSupplierResponse(supplier))
                .toList();
                
        return new AddMaterialResponse(
                material.getSku(),
                material.getName(),
                material.getShortDes(),
                material.getGroup() != null ? material.getGroup().getName() : null,
                material.isActive(),
                material.getImageUrl(),
                material.getCreatedAt(),
                material.getCreated_by() != null ? material.getCreated_by().getUsername() : null,
                variantResponses,
                supplierResponses,
                material.getUpdatedAt(),
                material.getUpdated_by() != null ? material.getUpdated_by().getUsername() : null
        );
    }

    /*
     * To return all available materials those were stored in database
     */
    @Override
    public Page<SearchMaterialResponse> findAllMaterials(Pageable pageable, User currentUser) {
        Page<Material> materials = materialRepository.findAllActiveByCompany(currentUser.getCompany(), pageable);
        return materials.map(SearchMaterialResponse::new);
    }

    /**
     * Search and filter by param.
     * @param search search by (SKU, Name, ShortDescription) + recommend drop down
     * @param groupMaterials filter by group
     * @param statuses fiter by status
     * @param currentUser current user for company context
     * @return Page<SearchMaterialResponse>
     */
    @Override
    public Page<SearchMaterialResponse> findMaterial(
            String search,
            List<String> groupMaterials,
            List<String> statuses,
            Pageable pageable,
            User currentUser) {

        // handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // convert groupMaterials fromList<String> to List<Long>
        List<Long> processedGroupMaterialIds = null;
        if (!CollectionUtils.isEmpty(groupMaterials)) {
            processedGroupMaterialIds = groupMaterials.stream()
                .map(s -> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid groupMaterial id: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (processedGroupMaterialIds.isEmpty()) {
                processedGroupMaterialIds = null;
            }
        }

        // convert status from String -> Enum
        List<MaterialStatus> processedStatuses = null;
        if (!CollectionUtils.isEmpty(statuses)) {
            processedStatuses = statuses.stream()
                                      .map(s -> {
                                          try {
                                              return MaterialStatus.valueOf(s.toUpperCase());
                                          } catch (IllegalArgumentException e) {
                                              System.err.println("Invalid material status: " + s);
                                              return null;
                                          }
                                      })
                                      .filter(s -> s != null) // Eliminate the null value
                                      .collect(Collectors.toList());
            if (processedStatuses.isEmpty()) { // If all of these are invalid, status filter wont apply
                processedStatuses = null;
            }
        }

        System.out.println("Search: " + processedSearch);
        System.out.println("GroupProduct IDs: " + processedGroupMaterialIds);
        System.out.println("Statuses: " + processedStatuses);
        
        try {
            // call repository with company context
            Page<Material> materials = materialRepository.findByCriteriaAndCompany(
                processedSearch,
                processedGroupMaterialIds,
                processedStatuses,
                currentUser.getCompany(),
                pageable
            );
            System.out.println("Repository returned " + materials.getTotalElements() + " materials");
            return materials.map(SearchMaterialResponse::new);
        } catch (Exception e) {
            System.err.println("Error in MaterialService.findMaterial: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Query all materials (autocomplete in frontend).
     */
    @Override
    public List<AllMaterialSearchResponse> getAllMaterialsForAutocomplete(String search, User currentUser) {
        return materialRepository.findBySearchKeywordAndCompany(search, currentUser.getCompany()).stream()
                .map(AllMaterialSearchResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Query all material variants (autocomplete in frontend).
     */
    @Override
    public List<MaterialVariantAutoCompleteResponse> getAllMaterialVariantsForAutocomplete(String search, User currentUser) {
        List<MaterialVariant> variants;
        
        if (StringUtils.hasText(search)) {
            // Search by material name, material SKU, variant SKU, or variant name
            variants = materialVariantRepository.findBySearchKeywordAndCompany(search, currentUser.getCompany());
        } else {
            // Get all active variants
            variants = materialVariantRepository.findByCompanyAndIsActiveTrue(currentUser.getCompany());
        }
        
        return variants.stream()
                .map(variant -> new MaterialVariantAutoCompleteResponse(
                    variant.getId(),
                    variant.getMaterial().getName(),
                    variant.getMaterial().getSku(),
                    variant.getSku(),
                    variant.getName(),
                    variant.getMaterial().getGroup() != null ? variant.getMaterial().getGroup().getName() : null
                ))
                .collect(Collectors.toList());
    }

    /**
     * get Material by ID.
     */
    @Override
    public MaterialDetailResponse getMaterialById(Long id, User currentUser) {
        Material material = materialRepository.findByIdWithVariantsAndInventoryAndCompany(id, currentUser.getCompany())
                .orElseThrow(() -> new RuntimeException("Material not found with ID: " + id));

        List<MaterialVariantInventoryResponse> variantInventory = material.getVariants() != null
                ? material.getVariants().stream()
                    .map(variant -> {
                        MaterialVariantResponse variantResponse = new MaterialVariantResponse(variant);
                        InventoryResponse inventoryResponse = null;

                        if (variant.getMaterialInventory() != null) {
                            inventoryResponse = new InventoryResponse(variant.getMaterialInventory());
                        }

                        return new MaterialVariantInventoryResponse(variantResponse, inventoryResponse);
                    })
                    .collect(Collectors.toList())
                : Collections.emptyList();
        
        List<MaterialSupplierResponse> materialSupplier = material.getMaterialSuppliers() != null 
                ? material.getMaterialSuppliers().stream()
                    .map(supplier -> {
                        return new MaterialSupplierResponse(supplier);
                    })
                    .collect(Collectors.toList())
                : Collections.emptyList();

        return new MaterialDetailResponse(
            material.getId(),
            material.getSku(),
            material.getName(),
            material.getShortDes(),
            material.getGroup() != null ? material.getGroup().getName() : null,
            material.getStatus() != null ? material.getStatus().name() : null,
            material.getUom() != null ? material.getUom().name() : null,
            material.getImageUrl(),
            variantInventory,
            materialSupplier
        );
    }

    @Override
    public List<MaterialGroupResponse> getAllGroupName(User currentUser) {
        return materialGroupRepository.findByCompanyAndIsActiveTrue(currentUser.getCompany()).stream()
                .map(MaterialGroupResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public UpdateMaterialResponse updateMaterial(Long id, UpdateMaterialRequest request, User updatedByUser) {
        // Find existing material
        Material material = materialRepository.findByIdAndCompany(id, updatedByUser.getCompany())
                .orElseThrow(() -> new RuntimeException("Material not found with ID: " + id));

        // Update basic fields (excluding SKU)
        material.setName(request.getName());
        material.setShortDes(request.getShortDes());
        material.setUom(UoM.valueOf(request.getUom()));
        material.setImageUrl(request.getImageUrl());
        material.setUpdated_by(updatedByUser);
        material.setUpdatedAt(java.time.LocalDateTime.now());

        // Handle Material Group
        MaterialGroup materialGroup = null;
        if (request.getMaterialGroupId() != null) {
            // Choose Existing Material Group
            Long materialGroupId = Long.parseLong(request.getMaterialGroupId());
            materialGroup = materialGroupRepository.findByIdAndCompanyAndIsActiveTrue(materialGroupId, updatedByUser.getCompany())
                    .orElseThrow(() -> new RuntimeException("Material Group not found with ID: " + request.getMaterialGroupId()));
        } else if (StringUtils.hasText(request.getNewMaterialGroupName())) {
            // Create New Material Group
            String newGroupName = request.getNewMaterialGroupName().trim();
            if (materialGroupRepository.findByNameAndCompanyAndIsActiveTrue(newGroupName, updatedByUser.getCompany()).isPresent()) {
                throw new IllegalArgumentException("Group name already exists: " + newGroupName);
            }
            materialGroup = new MaterialGroup(newGroupName, updatedByUser, updatedByUser.getCompany());
            materialGroupRepository.save(materialGroup);
        }
        material.setGroup(materialGroup);

        // Handle Variants
        if (request.getVariants() != null) {
            // Get existing variants
            List<MaterialVariant> existingVariants = materialVariantRepository.findByMaterial(material);
            System.out.println("Existing variants count: " + existingVariants.size());
            System.out.println("Request variants count: " + request.getVariants().size());
            
            // Create maps for easy lookup
            Map<String, MaterialVariant> existingVariantsBySku = existingVariants.stream()
                .collect(Collectors.toMap(MaterialVariant::getSku, variant -> variant));
            
            Map<String, UpdateMaterialVariantRequest> requestVariantsBySku = request.getVariants().stream()
                .filter(v -> v.isExistingVariant())
                .collect(Collectors.toMap(UpdateMaterialVariantRequest::getSku, variant -> variant));
            
            // 1. Delete variants that are not in the request (removed by user)
            Set<String> existingSkus = existingVariantsBySku.keySet();
            Set<String> requestSkus = requestVariantsBySku.keySet();
            Set<String> skusToDelete = existingSkus.stream()
                .filter(sku -> !requestSkus.contains(sku))
                .collect(Collectors.toSet());
            
            for (String skuToDelete : skusToDelete) {
                MaterialVariant variantToDelete = existingVariantsBySku.get(skuToDelete);
                System.out.println("Deleting variant with SKU: " + skuToDelete);
                
                // Delete material_inventory_log first
                materialInventoryLogRepository.deleteByMaterialInventory(
                    materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(variantToDelete.getId(), updatedByUser.getCompany()).orElse(null)
                );
                // Delete material_inventory
                materialInventoryRepository.deleteByMaterialVariant(variantToDelete);
                // Delete material_variant
                materialVariantRepository.delete(variantToDelete);
            }
            
            // 2. Update existing variants
            for (UpdateMaterialVariantRequest variantRequest : request.getVariants()) {
                if (variantRequest.isExistingVariant()) {
                    MaterialVariant existingVariant = existingVariantsBySku.get(variantRequest.getSku());
                    if (existingVariant != null) {
                        System.out.println("Updating existing variant with SKU: " + variantRequest.getSku());
                        existingVariant.setName(variantRequest.getName());
                        existingVariant.setShortDes(variantRequest.getShortDescription());
                        existingVariant.setImageUrl(variantRequest.getImageUrl());
                        existingVariant.setUpdated_by(updatedByUser);
                        existingVariant.setUpdatedAt(java.time.LocalDateTime.now());
                        materialVariantRepository.save(existingVariant);
                    }
                }
            }
            
            // 3. Create new variants
            List<MaterialVariant> newVariants = new ArrayList<>();
            int newVariantIndex = existingVariants.size() + 1;
            
            for (UpdateMaterialVariantRequest variantRequest : request.getVariants()) {
                if (variantRequest.isNewVariant()) {
                    System.out.println("Creating new variant: " + variantRequest.getName());
                    
                    // Generate SKU for new variant
                    String sku = generateVariantSku(material.getSku(), newVariantIndex, updatedByUser.getCompany());
                    System.out.println("Generated SKU for new variant: " + sku);
                    
                    MaterialVariant newVariant = new MaterialVariant(
                        sku,
                        variantRequest.getName(),
                        variantRequest.getShortDescription(),
                        material.getUom(),
                        variantRequest.getImageUrl(),
                        material,
                        updatedByUser
                    );
                    newVariants.add(newVariant);
                    newVariantIndex++;
                }
            }
            
            // Save new variants
            if (!newVariants.isEmpty()) {
                materialVariantRepository.saveAll(newVariants);
            }
        }

        // Handle Suppliers
        if (request.getSuppliers() != null && !request.getSuppliers().isEmpty()) {
            // Clear existing suppliers from database
            List<MaterialSupplier> existingSuppliers = materialSupplierRepository.findByMaterial(material);
            materialSupplierRepository.deleteAll(existingSuppliers);
            
            // Add new suppliers
            Set<MaterialSupplier> suppliers = request.getSuppliers().stream()
                .map(supplierRequest -> {
                    Supplier supplier = supplierRepository.findById(supplierRequest.getSupplierId())
                            .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierRequest.getSupplierId()));
                    
                    MaterialSupplier materialSupplier = new MaterialSupplier(
                        material,
                        supplier,
                        supplierRequest.getPrice(),
                        supplierRequest.getCurrency(),
                        updatedByUser
                    );
                    
                    // Set the ID manually
                    materialSupplier.setId(new MaterialSupplierId(material.getId(), supplier.getId()));
                    
                    // Set optional fields
                    if (supplierRequest.getLeadTimeDays() != null) {
                        materialSupplier.setLeadTimeDays(supplierRequest.getLeadTimeDays());
                    }
                    if (supplierRequest.getMinOrderQuantity() != null) {
                        materialSupplier.setMinOrderQuantity(supplierRequest.getMinOrderQuantity());
                    }
                    if (StringUtils.hasText(supplierRequest.getSupplierMaterialCode())) {
                        materialSupplier.setSupplierMaterialCode(supplierRequest.getSupplierMaterialCode());
                    }
                    
                    return materialSupplier;
                }).collect(Collectors.toSet());
            
            // Save suppliers directly
            materialSupplierRepository.saveAll(suppliers);
        }

        // Save the updated material
        Material savedMaterial = materialRepository.save(material);

        // Return response
        return mapMaterialToUpdateResponse(savedMaterial);
    }

    private UpdateMaterialResponse mapMaterialToUpdateResponse(Material material) {
        List<MaterialVariantResponse> variantResponses = material.getVariants() != null ?
                material.getVariants().stream()
                        .map(variant -> new MaterialVariantResponse(variant)).toList() : List.of();
        
        // Fetch suppliers from database instead of relying on material's collection
        List<MaterialSupplierResponse> supplierResponses = materialSupplierRepository.findByMaterial(material)
                .stream()
                .map(supplier -> new MaterialSupplierResponse(supplier))
                .toList();
        
        return new UpdateMaterialResponse(
                material.getSku(),
                material.getName(),
                material.getShortDes(),
                material.getGroup() != null ? material.getGroup().getName() : null,
                material.isActive(),
                material.getImageUrl(),
                material.getCreatedAt(),
                material.getCreated_by() != null ? material.getCreated_by().getUsername() : null,
                variantResponses,
                supplierResponses,
                material.getUpdatedAt(),
                material.getUpdated_by() != null ? material.getUpdated_by().getUsername() : null
        );
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ChangeMaterialStatusResponse changeMaterialStatus(Long materialId, ChangeMaterialStatusRequest request, User changedByUser) {
        // Step 1: Find the material
        Material material = materialRepository.findByIdAndCompany(materialId, changedByUser.getCompany())
                .orElseThrow(() -> new RuntimeException("Material not found with ID: " + materialId));

        // Step 2: Get current status
        MaterialStatus currentStatus = material.getStatus();
        MaterialStatus newStatus = request.getNewStatus();

        // Step 3: Validate the status transition (E1: Invalid Status Transition)
        if (!materialStatusValidator.isValidTransition(currentStatus, newStatus)) {
            String errorMessage = materialStatusValidator.getInvalidTransitionMessage(currentStatus, newStatus);
            return new ChangeMaterialStatusResponse(materialId, currentStatus, newStatus, errorMessage, false);
        }

        // Step 4: Update the material status
        MaterialStatus previousStatus = material.getStatus();
        material.setStatus(newStatus);
        material.setUpdated_by(changedByUser);
        material.setUpdatedAt(java.time.LocalDateTime.now());

        // Step 5: Save the updated material
        Material savedMaterial = materialRepository.save(material);

        // Step 6: Create audit log entry
        MaterialStatusAuditLog auditLog = new MaterialStatusAuditLog(
            savedMaterial,
            changedByUser,
            previousStatus,
            newStatus,
            request.getReason()
        );
        materialStatusAuditLogRepository.save(auditLog);

        // Step 7: Return success response
        String successMessage = String.format("Material status successfully changed from '%s' to '%s'", 
                                           previousStatus, newStatus);
        return new ChangeMaterialStatusResponse(materialId, previousStatus, newStatus, successMessage, true);
    }

    @Override
    public MaterialStatusTransitionResponse getAvailableStatusTransitions(Long materialId, User currentUser) {
        Material material = materialRepository.findByIdAndCompany(materialId, currentUser.getCompany())
                .orElseThrow(() -> new RuntimeException("Material not found with ID: " + materialId));

        MaterialStatus currentStatus = material.getStatus();
        Set<MaterialStatus> availableTransitions = materialStatusValidator.getValidTransitions(currentStatus);

        String message = String.format("Available status transitions for material '%s' (current status: %s)", 
                                     material.getName(), currentStatus);
        return new MaterialStatusTransitionResponse(materialId, currentStatus, availableTransitions, message);
    }

    @Override
    public List<MaterialSupplierResponse> getSuppliersByMaterialVariantSku(String materialVariantSku, User currentUser) {
        // Validate input parameter
        if (materialVariantSku == null || materialVariantSku.trim().isEmpty()) {
            throw new IllegalArgumentException("Material variant SKU cannot be null or empty");
        }

        // Step 1: Find the material variant by SKU
        MaterialVariant materialVariant = materialVariantRepository.findBySkuAndCompany(materialVariantSku, currentUser.getCompany())
                .orElseThrow(() -> new RuntimeException("Material variant not found with SKU: " + materialVariantSku));

        // Step 2: Get the material from the variant
        Material material = materialVariant.getMaterial();
        if (material == null) {
            throw new RuntimeException("Material not found for variant SKU: " + materialVariantSku);
        }

        // Step 3: Find all suppliers for this material
        List<MaterialSupplier> materialSuppliers = materialSupplierRepository.findByMaterial(material);

        // Step 4: Convert to response DTOs with null safety
        List<MaterialSupplierResponse> supplierResponses = materialSuppliers.stream()
                .filter(materialSupplier -> materialSupplier != null) // Filter out null MaterialSupplier
                .filter(materialSupplier -> materialSupplier.getSupplier() != null) // Filter out MaterialSupplier with null Supplier
                .map(materialSupplier -> {
                    try {
                        return new MaterialSupplierResponse(materialSupplier);
                    } catch (IllegalArgumentException e) {
                        // Log the error but continue processing other suppliers
                        System.err.println("Skipping invalid MaterialSupplier: " + e.getMessage());
                        return null;
                    }
                })
                .filter(response -> response != null) // Filter out null responses
                .collect(Collectors.toList());

        return supplierResponses;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")
    public DeleteMaterialResponse deleteMaterial(Long id, User deletedByUser) {
        // Step 1: Find the material with company context
        Material material = materialRepository.findByIdAndCompany(id, deletedByUser.getCompany())
                .orElseThrow(() -> new RuntimeException("Material not found with ID: " + id));

        // Step 2: Check if material is already deleted
        if (material.getStatus() == MaterialStatus.DELETED) {
            throw new RuntimeException("Material is already deleted");
        }

        // Step 3: Soft delete the material
        material.setStatus(MaterialStatus.DELETED);
        material.setActive(false);
        material.setUpdated_by(deletedByUser);
        material.setUpdatedAt(java.time.LocalDateTime.now());

        // Step 4: Save the updated material
        materialRepository.save(material);

        // Step 5: Return success response
        return new DeleteMaterialResponse(
            material.getId(),
            material.getSku(),
            material.getName(),
            material.getStatus().name(),
            material.isActive(),
            material.getUpdatedAt(),
            deletedByUser.getUsername()
        );
    }

    private MaterialSupplierResponse mapMaterialSupplierToResponse(MaterialSupplier materialSupplier) {
        return new MaterialSupplierResponse(
            materialSupplier.getSupplier().getId(),
            materialSupplier.getSupplier().getName(),
            materialSupplier.getPrice(),
            materialSupplier.getCurrency(),
            materialSupplier.getLeadTimeDays(),
            materialSupplier.getMinOrderQuantity(),
            materialSupplier.getSupplierMaterialCode()
        );
    }
}
