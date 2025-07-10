package org.example.coretrack.service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.example.coretrack.dto.product.AddProductRequest;
import org.example.coretrack.dto.product.AddProductResponse;
import org.example.coretrack.dto.product.BOMItemRequest;
import org.example.coretrack.dto.product.BOMItemResponse;
import org.example.coretrack.dto.product.InventoryResponse;
import org.example.coretrack.dto.product.ProductDetailResponse;
import org.example.coretrack.dto.product.ProductVariantInfoResponse;
import org.example.coretrack.dto.product.ProductVariantInventoryResponse;
import org.example.coretrack.dto.product.ProductVariantResponse;
import org.example.coretrack.dto.product.SearchProductResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.product.BOM;
import org.example.coretrack.model.product.BOMItem;
import org.example.coretrack.model.product.product;
import org.example.coretrack.model.product.productGroup;
import org.example.coretrack.model.product.productStatus;
import org.example.coretrack.model.product.productVariant;
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
        productGroup productGroup = null;
        if (request.getProductGroupId() != null) {
            // Choose Existing Product Group (A5.1)
            productGroup = productGroupRepository.findById(request.getProductGroupId())
                    .orElseThrow(() -> new RuntimeException("Product Group not found with ID: " + request.getProductGroupId()));
        } else if (StringUtils.hasText(request.getNewProductGroupName())) {
            // Create New Product Group (A5.2)
            String newGroupName = request.getNewProductGroupName().trim();
            // E4: Duplicate Group Name
            if (productGroupRepository.findByName(newGroupName).isPresent()) { 
                    throw new IllegalArgumentException("Group name already exists: " + newGroupName);
                }
            productGroup = new productGroup(newGroupName,createdByUser);
            productGroupRepository.save(productGroup); // Save new group first
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
            // // You might add regex validation here for "shorten signs for group-product-variant structure"
            // product.setSku(manualSku);
            sku = manualSku;
        } else {
            // Automatic SKU Generation (A1)
            // Generate a 12-unit SKU
            sku = generateUniqueProductSku(); 
        }
    
        // 3. Create Product Entity (Main Flow - Step 2)
        product product = new product(
            request.getName(),
            sku,
            request.getDescription(),
            request.getPrice(),
            productGroup,
            createdByUser 
        );
        if(StringUtils.hasText(request.getImageUrl())){
            product.setImageUrl(request.getImageUrl());
        }
        productRepository.save(product); // Save product to get its ID for relationships

        // 4. Define Variant(s) (Main Flow - Step 4)
        List<productVariant> allProductVariants = new java.util.ArrayList<>();
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            // Product has variants
            List<productVariant> variants = request.getVariants().stream()
                .map(variantRequest -> {
                    productVariant variant = new productVariant(
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
                    variant.setSku(generateVariantSku(product.getSku(), product.getVariants().size() + 1));
                    return variant;
                }).collect(Collectors.toList());

            product.setVariants(variants); // Set variants on the product
            productVariantRepository.saveAll(variants); // Save all variants
            allProductVariants.addAll(variants);
        } else {
            // Product has no explicit variants, create a default variant for the base product
            productVariant defaultVariant = new productVariant(
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

        // 4. Add BOM Items (Main Flow - Step 3) 
        if (request.getBomItems() != null && !request.getBomItems().isEmpty()) {
            for (productVariant variant : allProductVariants) {
                // Create a new BillOfMaterials for each variant
                BOM bom = new BOM(variant, "1.0", createdByUser);
                bomRepository.save(bom); // Save the BOM header
                variant.setBom(bom);

                // Add the same set of BOM items to this BOM
                for (BOMItemRequest bomItemRequest : request.getBomItems()) {
                    Material material = materialRepository.findById(bomItemRequest.getMaterialId())
                            .orElseThrow(() -> new RuntimeException("Material not found with ID: " + bomItemRequest.getMaterialId()));

                    // E3: Insufficient Material Quantity for BOM (Implied/Future) - This check is typically done during production planning/order, not product creation.
                    // For product creation, we assume the BOM definition is what's required, not current stock.
                    // If you need this check here, it would involve InventoryService.

                    BOMItem bomItem = new BOMItem(
                        bom,
                        material,
                        bomItemRequest.getQuantity(),
                        material.getUom(), // UoM from material
                        createdByUser
                    );
                    bom.getBomItems().add(bomItem); // Add to BOM's items list and set bidirectional link
                }
                bomItemRepository.saveAll(bom.getBomItems()); // Save all BOM items for this variant's BOM
            }
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

    private AddProductResponse mapProductToProductResponse(product product) {
        List<ProductVariantResponse> variantResponses = product.getVariants() != null ?
                product.getVariants().stream()
                        .map(variant -> {
                            // Map BOM Items for EACH variant
                            List<BOMItemResponse> bomItemResponses = List.of();
                            if (variant.getBom() != null) {
                                bomItemResponses = variant.getBom().getBomItems().stream()
                                        .map(item -> new BOMItemResponse(
                                                item.getId(),
                                                item.getMaterial().getId(),
                                                item.getMaterial().getName(),
                                                item.getQuantity(),
                                                item.getUom().getDisplayName(),
                                                item.getNotes()
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

        // Xử lý tham số search: nếu null hoặc rỗng, đặt thành null để không áp dụng điều kiện LIKE
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // Xử lý tham số groupMaterials: nếu rỗng hoặc null, đặt thành null để không áp dụng điều kiện IN
        List<String> processedGroupMaterials = CollectionUtils.isEmpty(groupProducts) ? null : groupProducts;

        // Xử lý tham số statuses: chuyển đổi từ List<String> sang List<productStatus>
        List<productStatus> processedStatuses = null;
        if (!CollectionUtils.isEmpty(statuses)) {
            processedStatuses = statuses.stream()
                                      .map(s -> {
                                          try {
                                              return productStatus.valueOf(s.toUpperCase());
                                          } catch (IllegalArgumentException e) {
                                              // Xử lý nếu trạng thái không hợp lệ, có thể bỏ qua hoặc ném lỗi
                                              // Trong trường hợp này, chúng ta sẽ bỏ qua
                                              System.err.println("Invalid material status: " + s);
                                              return null;
                                          }
                                      })
                                      .filter(s -> s != null) // Loại bỏ các trạng thái null (không hợp lệ)
                                      .collect(Collectors.toList());
            if (processedStatuses.isEmpty()) { // Nếu tất cả các trạng thái đều không hợp lệ, không lọc theo status
                processedStatuses = null;
            }
        }

        Page<product> products = productRepository.findByCriteria(
            processedSearch,
            processedGroupMaterials,
            processedStatuses,
            pageable
        );

        return products.map(SearchProductResponse::new);
    }

    /**
     * Query all products (autocomplete in frontend).
     */

    @Override
    public List<SearchProductResponse> getAllProductsForAutocomplete(String search) {
        return productRepository.findBySearchKeyword(search).stream()
                .map(SearchProductResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Lấy vật liệu theo ID (cho A1: điều hướng đến chi tiết).
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
    product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

    List<ProductVariantInventoryResponse> variantInventory = product.getVariants() != null
            ? product.getVariants().stream()
                .map(variant -> {
                    ProductVariantInfoResponse variantResponse = new ProductVariantInfoResponse(variant);
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
        product.getDescription(),
        product.getGroup() != null ? product.getGroup().getName() : null,
        product.getStatus() != null ? product.getStatus().name() : null,
        product.getPrice(),
        product.getImageUrl(),
        variantInventory
    );
}

    @Override
    public List<String> getAllGroupName(){
        return productGroupRepository.findDistinctGroupNames();
    }
}
