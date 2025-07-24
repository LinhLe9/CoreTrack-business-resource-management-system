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

import org.example.coretrack.dto.material.AddMaterialRequest;
import org.example.coretrack.dto.material.AddMaterialResponse;
import org.example.coretrack.dto.material.AllMaterialSearchResponse;
import org.example.coretrack.dto.material.MaterialDetailResponse;
import org.example.coretrack.dto.material.MaterialGroupResponse;
import org.example.coretrack.dto.material.MaterialSupplierResponse;
import org.example.coretrack.dto.material.MaterialVariantInventoryResponse;
import org.example.coretrack.dto.material.MaterialVariantResponse;
import org.example.coretrack.dto.material.SearchMaterialResponse;
import org.example.coretrack.dto.product.InventoryResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.material.MaterialGroup;
import org.example.coretrack.model.material.MaterialStatus;
import org.example.coretrack.model.material.MaterialVariant;
import org.example.coretrack.model.material.UoM;
import org.example.coretrack.model.supplier.MaterialSupplier;
import org.example.coretrack.model.supplier.Supplier;
import org.example.coretrack.repository.MaterialGroupRepository;
import org.example.coretrack.repository.MaterialRepository;
import org.example.coretrack.repository.MaterialSupplierRepository;
import org.example.coretrack.repository.MaterialVariantRepository;
import org.example.coretrack.repository.SupplierRepository;
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
            materialGroup = materialGroupRepository.findByIdAndIsActiveTrue(materialGroupId)
                    .orElseThrow(() -> new RuntimeException("Material Group not found with ID: " + request.getMaterialGroupId()));
        } else if (StringUtils.hasText(request.getNewMaterialGroupName())) {
            // Create New Materiap Group (A5.2)
            String newGroupName = request.getNewMaterialGroupName().trim();
            // E4: Duplicate Group Name
            if (materialGroupRepository.findByNameAndIsActiveTrue(newGroupName).isPresent()) { 
                    throw new IllegalArgumentException("Group name already exists: " + newGroupName);
                }
            materialGroup = new MaterialGroup(newGroupName,createdByUser);
            materialGroupRepository.save(materialGroup); // Save new group first
        }

        // 2. Handle Material SKU (Main Flow - Step 2, A1, A2)
        String sku = "";
        if (StringUtils.hasText(request.getSku())) {
            // Manual SKU Input (A2)
            String manualSku = request.getSku().trim();
            // E2: Duplicate or Invalid SKU - uniqueness
            if (materialRepository.findBySku(manualSku).isPresent()) {
                throw new IllegalArgumentException("Material SKU already exists: " + manualSku);
            }
            // E2: Invalid SKU format - length (8-12 chars for tidy SKU)
            if (manualSku.length() < 8 || manualSku.length() > 12) {
                 throw new IllegalArgumentException("Manual SKU must be between 8 and 12 characters.");
            }
            // product.setSku(manualSku);
            sku = manualSku;
        } else {
            // Automatic SKU Generation (A1)
            // Generate a 12-unit SKU
            sku = generateUniqueMaterialSku(); 
        }
    
        // 3. Create Material Entity (Main Flow - Step 2)
        Material material = new Material(
            sku,
            request.getName(),
            request.getShortDes(),
            materialGroup,
            createdByUser 
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
        materialRepository.save(material); // Save product to get its ID for relationships

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
                    variant.setSku(generateVariantSku(material.getSku(), index));
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
            
            // Material has suppliers
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
                    return materialSupplier;
                }).collect(Collectors.toSet());

            material.setMaterialSuppliers(suppliers); // Set variants on the product
            materialSupplierRepository.saveAll(suppliers);
             } // Save all variants
        return mapProductToProductResponse(material) ; // Return the fully populated Product entity
    }

    // --- Helper methods for SKU generation ---
    private String generateUniqueMaterialSku() {
        String newSku;
        Random random = new Random(); // Initialize Random
        do {
            // Generate a 7-digit random number
            // 1000000 is the smallest 7-digit number, 9999999 is the largest
            int randomNumber = 1000000 + random.nextInt(9000000); // Generates number between 1,000,000 and 9,999,999
            newSku = "MATE-" + randomNumber; // Example: PROD-1234567
        } while (materialRepository.findBySku(newSku).isPresent());
        return newSku;
    }

    private String generateVariantSku(String baseMaterialSku, int variantIndex) {
        // This is two layer to ensure that SKU of variant is unique in case 
        // some error happen while a lot of modify/ delete happened
        // Example: adding "-1", "-2" suffixes (Main Flow - Step 4)
        String suffix = "-" + variantIndex;
        String variantSku = baseMaterialSku + suffix;

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
        } while (materialVariantRepository.findBySku(finalVariantSku).isPresent());

        return finalVariantSku;
    }

    private AddMaterialResponse mapProductToProductResponse(Material material) {
        List<MaterialVariantResponse> variantResponses = material.getVariants() != null ?
                material.getVariants().stream()
                        .map(variant -> new MaterialVariantResponse(variant)).toList() : List.of();
        List<MaterialSupplierResponse> supplierResponses = material.getMaterialSuppliers() != null ?
                material.getMaterialSuppliers().stream()
                        .map(supplier -> new MaterialSupplierResponse(supplier)).toList() : List.of();
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


    /**
     * Search and filter by param.
     * @param search search by (SKU, Name, ShortDescription) + recommend drop down
     * @param groupMaterials filter by group
     * @param statuses fiter by status
     * @return Page<SearchProductResponse>
     */
    @Override
    public Page<SearchMaterialResponse> findMaterial(
            String search,
            List<String> groupMaterials,
            List<String> statuses,
            Pageable pageable) {

        // Handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // convert groupMaterial from List<String> to List<Long>
        List<Long> processedGroupMaterialIds = null;
        if(!CollectionUtils.isEmpty(groupMaterials)) {
            processedGroupMaterialIds = groupMaterials.stream()
                .map(s-> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e){
                        System.err.println("Invalid groupMaterial id: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if(processedGroupMaterialIds.isEmpty()){
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
        // call repository
        Page<Material> materials = materialRepository.findByCriteria(
            processedSearch,
            processedGroupMaterialIds,
            processedStatuses,
            pageable
        );

        return materials.map(SearchMaterialResponse::new);
    }

    @Override
    public List<AllMaterialSearchResponse> getAllMaterialsForAutocomplete(String search) {
        return materialRepository.findBySearchKeyword(search).stream()
                .map(AllMaterialSearchResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SearchMaterialResponse> findAllMaterials (Pageable pageable){
        Page<Material> materials = materialRepository.findAllActive(pageable);
        return materials.map(SearchMaterialResponse::new);
    }


    @Override
    public MaterialDetailResponse getMaterialById(Long id) {
    Material material = materialRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

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
            :Collections.emptyList();

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
    public List<MaterialGroupResponse> getAllGroupName(){
        return materialGroupRepository.findByIsActiveTrueAndNameIsNotNull().stream()
                .map(MaterialGroupResponse::new)
                .collect(Collectors.toList());
    }
}
