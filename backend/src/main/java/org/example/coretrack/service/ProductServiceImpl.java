package org.example.coretrack.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.example.coretrack.dto.product.AddProductRequest;
import org.example.coretrack.dto.product.AddProductResponse;
import org.example.coretrack.dto.product.AllProductSearchResponse;
import org.example.coretrack.dto.product.BOMItemRequest;
import org.example.coretrack.dto.product.BOMItemResponse;
import org.example.coretrack.dto.product.InventoryResponse;
import org.example.coretrack.dto.product.ProductDetailResponse;
import org.example.coretrack.dto.product.ProductGroupResponse;
import org.example.coretrack.dto.product.ProductVariantInfoResponse;
import org.example.coretrack.dto.product.ProductVariantInventoryResponse;
import org.example.coretrack.dto.product.ProductVariantRequest;
import org.example.coretrack.dto.product.ProductVariantResponse;
import org.example.coretrack.dto.product.SearchProductResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.product.BOM;
import org.example.coretrack.model.product.BOMItem;
import org.example.coretrack.model.product.Product;
import org.example.coretrack.model.product.ProductGroup;
import org.example.coretrack.model.product.ProductStatus;
import org.example.coretrack.model.product.ProductVariant;
import org.example.coretrack.repository.BomItemRepository;
import org.example.coretrack.repository.BomRepository;
import org.example.coretrack.repository.MaterialRepository;
import org.example.coretrack.repository.ProductGroupRepository;
import org.example.coretrack.repository.ProductRepository;
import org.example.coretrack.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired 
    private ProductGroupRepository productGroupRepository;

    @Autowired 
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private BomItemRepository bomItemRepository;
    
    @Override
    @Transactional
    @PreAuthorize("hasRole('OWNER')")  
    public AddProductResponse createProduct(AddProductRequest request, User createdByUser) {
        
        // 1. Handle Product Group (Main Flow - Step 4, A5)
        // Initialize as null
        ProductGroup productGroup = null;
        if (request.getProductGroupId() != null) {
            // Choose Existing Product Group (A5.1)
            Long ProductGroupId = Long.parseLong(request.getProductGroupId());
            productGroup = productGroupRepository.findByIdAndIsActiveTrue(ProductGroupId)
                    .orElseThrow(() -> new RuntimeException("Product Group not found with ID: " + request.getProductGroupId()));
        } else if (StringUtils.hasText(request.getNewProductGroupName())) {
            // Create New Product Group (A5.2)
            String newGroupName = request.getNewProductGroupName().trim();
            // E4: Duplicate Group Name
            if (productGroupRepository.findByNameAndIsActiveTrue(newGroupName).isPresent()) { 
                    throw new IllegalArgumentException("Group name already exists: " + newGroupName);
                }
            productGroup = new ProductGroup(newGroupName,createdByUser);
            productGroupRepository.save(productGroup); // Save new group first
        } else {
            productGroup = null;
        }

        // 2. Handle Product SKU (Main Flow - Step 2, A1, A2)
        String sku = "";
        if (StringUtils.hasText(request.getSku())) {
            // Manual SKU Input (A2)
            String manualSku = request.getSku().trim();
            // E2: Duplicate or Invalid SKU - uniqueness
            if (productRepository.findBySku(manualSku).isPresent()) {
                throw new IllegalArgumentException("Product SKU already exists: " + manualSku);
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
            sku = generateUniqueProductSku(); 
        }
    
        // 3. Create Product Entity (Main Flow - Step 2)
        Product product = new Product(
            request.getName(),
            sku,
            request.getDescription(),
            request.getPrice(),
            request.getCurrency(),
            productGroup,
            createdByUser 
        );
        if(StringUtils.hasText(request.getImageUrl())){
            product.setImageUrl(request.getImageUrl());
        }
        productRepository.save(product); // Save product to get its ID for relationships

        // 4. Define Variant(s) (Main Flow - Step 4)
        List<ProductVariant> allProductVariants = new java.util.ArrayList<>();

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            // Product has variants
            AtomicInteger skuCounter = new AtomicInteger(1);
            List<ProductVariant> variants = request.getVariants().stream()
                .map(variantRequest -> {
                    int index = skuCounter.getAndIncrement();
                    ProductVariant variant = new ProductVariant(
                        product, // Link to the base product
                        variantRequest.getName(),
                        null, // SKU will be generated later
                        variantRequest.getShortDescription(),
                        request.getPrice(),
                        createdByUser
                    );
                    if(StringUtils.hasText(variantRequest.getImageUrl())){
                        variant.setImageUrl(variantRequest.getImageUrl());
                    }
                    // Generate SKU for variant (e.g., adding "-1", "-2" suffixes)
                    variant.setSku(generateVariantSku(product.getSku(), index));
                    return variant;
                }).collect(Collectors.toList());

            product.setVariants(variants); // Set variants on the product
            productVariantRepository.saveAll(variants); // Save all variants
            allProductVariants.addAll(variants);

            // loop to add BOM Items
            for (int i = 0; i < variants.size(); i++){
                ProductVariant variant = variants.get(i);
                ProductVariantRequest variantRequest = request.getVariants().get(i);

                if (variantRequest.getBomItems() != null && !variantRequest.getBomItems().isEmpty()) {
                // Create a new BillOfMaterials for each variant
                    BOM bom = new BOM(variant, "1.0", createdByUser);
                    bomRepository.save(bom); // Save the BOM header
                    List<BOMItemRequest> bomItemRequest = variantRequest.getBomItems();
                    for (int j = 0; j < bomItemRequest.size(); j++){
                        Optional<Material> optional = materialRepository.findBySku(bomItemRequest.get(j).getMaterialSku());
                        if (!optional.isPresent()) {
                            throw new RuntimeException("Material not found with SKU: " + bomItemRequest.get(j).getMaterialSku());
                        }
                        Material material = optional.get();
                        BOMItem bomItem = new BOMItem(
                            bom,
                            material,
                            bomItemRequest.get(j).getQuantity(),
                            material.getUom(), // UoM from material
                            createdByUser
                        );
                        bom.getBomItems().add(bomItem);

                    }
                    bomItemRepository.saveAll(bom.getBomItems());
                    variant.setBom(bom);
                }
            }
        } else {
            // Product has no explicit variants, create a default variant for the base product
            ProductVariant defaultVariant = new ProductVariant(
                product,
                product.getName(),
                product.getSku(), // Base product SKU becomes default variant SKU
                product.getDescription(),
                product.getPrice(),
                createdByUser
            );
            if(StringUtils.hasText(product.getImageUrl())){
                defaultVariant.setImageUrl(product.getImageUrl());
            }
            productVariantRepository.save(defaultVariant);
            product.getVariants().add(defaultVariant); // Add to product's variants list
            allProductVariants.add(defaultVariant);
        }
        return mapProductToProductResponse(product) ; // Return the fully populated Product entity
    }

    // --- Helper methods for SKU generation ---
    private String generateUniqueProductSku() {
        String newSku;
        Random random = new Random(); // Initialize Random
        do {
            // Generate a 7-digit random number
            // 1000000 is the smallest 7-digit number, 9999999 is the largest
            int randomNumber = 1000000 + random.nextInt(9000000); // Generates number between 1,000,000 and 9,999,999
            newSku = "PROD-" + randomNumber; // Example: PROD-1234567
        } while (productRepository.findBySku(newSku).isPresent());
        return newSku;
    }

    private String generateVariantSku(String baseProductSku, int variantIndex) {
        // This is two layer to ensure that SKU of variant is unique in case 
        // some error happen while a lot of modify/ delete happened
        // Example: adding "-1", "-2" suffixes (Main Flow - Step 4)
        String suffix = "-" + variantIndex;
        String variantSku = baseProductSku + suffix;

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
        } while (productVariantRepository.findBySku(finalVariantSku).isPresent());

        return finalVariantSku;
    }

    private AddProductResponse mapProductToProductResponse(Product product) {
        List<ProductVariantResponse> variantResponses = product.getVariants() != null ?
                product.getVariants().stream()
                        .map(variant -> {
                            // Map BOM Items for EACH variant
                            List<BOMItemResponse> bomItemResponses = List.of();
                            if (variant.getBom() != null) {
                                bomItemResponses = variant.getBom().getBomItems().stream()
                                        .map(item -> new BOMItemResponse(
                                                item.getId(),
                                                item.getMaterial().getSku(),
                                                item.getMaterial().getName(),
                                                item.getQuantity(),
                                                item.getUom().getDisplayName()
                                        )).toList();
                            }
                            return new ProductVariantResponse(
                                    variant.getId(),
                                    variant.getSku(),
                                    variant.getName(),
                                    variant.getDescription(),
                                    variant.getImageUrl(),
                                    bomItemResponses // Pass BOM items to variant response
                            );
                        }).toList() : List.of();

        return new AddProductResponse(
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCurrency(),
                product.getProductGroup() != null ? product.getProductGroup().getName() : null,
                product.getImageUrl(),
                product.isActive(),
                variantResponses,
                product.getCreatedAt(),
                product.getCreatedBy() != null ? product.getCreatedBy().getUsername() : null,
                product.getUpdatedAt(),
                product.getUpdatedBy() != null ? product.getUpdatedBy().getUsername() : null
        );
    }

    /*
     * To return all available products those were stored in database
     */
    @Override
     public Page<SearchProductResponse> findAllProducts (Pageable pageable){
        Page<Product> products = productRepository.findAllActive(pageable);
        return products.map(SearchProductResponse::new);
     }

    /**
     * Search and filter by param.
     * @param search search by (SKU, Name, ShortDescription) + recommend drop down
     * @param groupProducts filter by group
     * @param statuses fiter by status
     * @return Page<SearchProductResponse>
     */

    @Override
    public Page<SearchProductResponse> findProduct(
            String search,
            List<String> groupProducts,
            List<String> statuses,
            Pageable pageable) {

        // handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // convert groupProducts fromList<String> to List<Long>
        List<Long> processedGroupProductIds = null;
        if (!CollectionUtils.isEmpty(groupProducts)) {
            processedGroupProductIds = groupProducts.stream()
                .map(s -> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid groupProduct id: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (processedGroupProductIds.isEmpty()) {
                processedGroupProductIds = null;
            }
        }

        // convert status from String -> Enum
        List<ProductStatus> processedStatuses = null;
        if (!CollectionUtils.isEmpty(statuses)) {
            processedStatuses = statuses.stream()
                .map(s -> {
                    try {
                        return ProductStatus.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid product status: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (processedStatuses.isEmpty()) {
                processedStatuses = null;
            }
        }

        System.out.println("Search: " + processedSearch);
        System.out.println("GroupProduct IDs: " + processedGroupProductIds);
        System.out.println("Statuses: " + processedStatuses);
        // call repository
        Page<Product> products = productRepository.findByCriteria(
            processedSearch,
            processedGroupProductIds, 
            processedStatuses,
            pageable
        );

        return products.map(SearchProductResponse::new);
    }

    /**
     * Query all products (autocomplete in frontend).
     */

    @Override
    public List<AllProductSearchResponse> getAllProductsForAutocomplete(String search) {
        return productRepository.findBySearchKeyword(search).stream()
                .map(AllProductSearchResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * get Product by ID.
     */
    // @Override
    // public ProductDetailResponse getProductById(Long id) {
    //     product product = productRepository.findById(id)
    //                     .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    //     List<ProductVariantInventoryResponse> variantInventory = product.getVariants() != null ?
    //             product.getVariants().stream()
    //                     .map(variant -> {
    //                         // Map BOM Items for EACH variant
    //                         List<BOMItemResponse> bomItemResponses = variant.getBom() != null ?
    //                             variant.getBom().getBomItems().stream()
    //                                     .map(item -> new BOMItemResponse(
    //                                             item.getId(),
    //                                             item.getMaterial().getId(),
    //                                             item.getMaterial().getName(),
    //                                             item.getQuantity(),
    //                                             item.getUom().getDisplayName(),
    //                                             item.getNotes()
    //                                         )
    //                                     ).toList(): List.of();
    //                         ProductVariantResponse variantResponse = new ProductVariantResponse(
    //                                 variant.getId(),
    //                                 variant.getSku(),
    //                                 variant.getName(),
    //                                 variant.getDescription(),
    //                                 variant.getImageUrl(),
    //                                 bomItemResponses // Pass BOM items to variant response
    //                         );

    //                         // map inventory if available
    //                         InventoryResponse inventoryResponse = null;
    //                         if(variant.getProductInventory() !=null) {
    //                             inventoryResponse  = new InventoryResponse(variant.getProductInventory());
    //                         }
    //                         return new ProductVariantInventoryResponse(variantResponse, inventoryResponse);
    //                     }).toList() : List.of();   
    //     ProductDetailResponse response = new ProductDetailResponse(
    //                     product.getId(),
    //                     product.getSku(),
    //                     product.getName(),
    //                     product.getDescription(),
    //                     product.getGroup().getName(),
    //                     product.getStatus().name(),
    //                     product.getPrice(),
    //                     product.getImageUrl(),
    //                     variantInventory
    //             );
    //     return response;            
    // }

    @Override
    public ProductDetailResponse getProductById(Long id) {
    Product product = productRepository.findByIdWithVariantsAndInventory(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

    List<ProductVariantInventoryResponse> variantInventory = product.getVariants() != null
            ? product.getVariants().stream()
                .map(variant -> {
                    ProductVariantInfoResponse variantResponse = variant != null ? new ProductVariantInfoResponse(variant) : null;
                    InventoryResponse inventoryResponse = null;

                    if (variant.getProductInventory() != null) {
                        inventoryResponse = new InventoryResponse(variant.getProductInventory());
                    }

                    return new ProductVariantInventoryResponse(variantResponse, inventoryResponse);
                })
                .collect(Collectors.toList())
            : Collections.emptyList();

    return new ProductDetailResponse(
        product.getId(),
        product.getSku(),
        product.getName(),
        product.getDescription() != null ? product.getDescription() : null,
        product.getGroup() != null ? product.getGroup().getName() : null,
        product.getStatus() != null ? product.getStatus().name() : null,
        product.getPrice() != null ? product.getPrice() : null,
        product.getImageUrl() != null ? product.getImageUrl() : null,
        variantInventory
    );
    }

    @Override
    public List<ProductGroupResponse> getAllGroupName(){
        return productGroupRepository.findByIsActiveTrueAndNameIsNotNull().stream()
                .map(ProductGroupResponse::new)
                .collect(Collectors.toList());
    }
}
