package org.example.coretrack.controller;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.dto.product.AddProductRequest;
import org.example.coretrack.dto.product.AddProductResponse;
import org.example.coretrack.dto.product.AllProductSearchResponse;
import org.example.coretrack.dto.product.BOMItemResponse;
import org.example.coretrack.dto.product.ProductDetailResponse;
import org.example.coretrack.dto.product.ProductGroupResponse;
import org.example.coretrack.dto.product.SearchProductResponse;
import org.example.coretrack.dto.product.UpdateProductRequest;
import org.example.coretrack.dto.product.UpdateProductResponse;
import org.example.coretrack.dto.product.ChangeProductStatusRequest;
import org.example.coretrack.dto.product.ChangeProductStatusResponse;
import org.example.coretrack.dto.product.ProductStatusTransitionResponse;
import org.example.coretrack.dto.product.ProductVariantAutoCompleteResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.repository.UserRepository;
import org.example.coretrack.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AddProductResponse> createProduct(@Valid @RequestBody AddProductRequest request) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        AddProductResponse response = productService.createProduct(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private User getCurrentUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; 
        }
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String principalName = ((UserDetails) authentication.getPrincipal()).getUsername();

            Optional<User> userOpt = userRepository.findByUsername(principalName);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }

            return userRepository.findByEmail(principalName).orElse(null);
        }
        return null;
    }
    
    /**
     * Test endpoint to check if the API is working
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Product API is working!");
    }
    
    /**
     * Endpoint to search and filter
     * @param search search key (SKU, Name, ShortDescription)
     * @param groupProduct one or list of product group from front end
     * @param status one or list of statuses from front end
     * @param pagable
     * @return SeachProductResponse
     */
    @GetMapping ("/filter")
    public ResponseEntity<Page<SearchProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(name = "groupProducts",required = false) List<String> groupProduct, 
            @RequestParam(name = "status", required = false) List<String> status,
            @PageableDefault(page = 0, size = 20) Pageable pageable) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        Page<SearchProductResponse> products = productService.findProduct(search, groupProduct, status, pageable);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(products);
    }

    /**
     * Endpoint to search and filter return all, doesnt use pagenation
     * @param search search key (SKU, Name, ShortDescription)
     * @return SeachProductResponse
     */
    @GetMapping("/all")
    public ResponseEntity<List<AllProductSearchResponse>> getProducts(
            @RequestParam(required = false) String search) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        List<AllProductSearchResponse> products = productService.getAllProductsForAutocomplete(search);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(products);
    }

    /**
     * Endpoint to get all product variants for autocomplete
     */
    @GetMapping("/variants/autocomplete")
    public ResponseEntity<List<ProductVariantAutoCompleteResponse>> getProductVariantsForAutocomplete(
            @RequestParam(required = false) String search) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        List<ProductVariantAutoCompleteResponse> variants = productService.getAllProductVariantsForAutocomplete(search);

        return ResponseEntity.ok(variants);
    }

    /**
     * Endpoint for product detail by ID (A1)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Long id) {
        ProductDetailResponse product = productService.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id);
        }
        return ResponseEntity.ok(product);
    }

    /*
     * Endpoint for returning the product group name
     */
    @GetMapping("/product-groups")
    public ResponseEntity<List<ProductGroupResponse>> getAllProductGroups() {
        try {
            List<ProductGroupResponse> groups = productService.getAllGroupName();
            System.out.println("Result: " + groups);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            e.printStackTrace(); // log lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build(); // hoặc .body(null)
        }
    }

    /**
     * Endpoint for updating product information (excluding SKU)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UpdateProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated or not found");
        }
        UpdateProductResponse response = productService.updateProduct(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ChangeProductStatusResponse> changeProductStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeProductStatusRequest request) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated or not found");
        }
        ChangeProductStatusResponse response = productService.changeProductStatus(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status-transitions")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ProductStatusTransitionResponse> getAvailableStatusTransitions(@PathVariable Long id) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated or not found");
        }
        ProductStatusTransitionResponse response = productService.getAvailableStatusTransitions(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/variant/{variantId}")
    public ResponseEntity<List<BOMItemResponse>> getBomItem (
        @PathVariable Long id,
        @PathVariable Long variantId
    ){
        List<BOMItemResponse> response = productService.getBomItem(id, variantId);
        return ResponseEntity.ok(response);
    }
}
