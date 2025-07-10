package org.example.coretrack.controller;

import java.util.List;

import org.example.coretrack.dto.product.AddProductRequest;
import org.example.coretrack.dto.product.AddProductResponse;
import org.example.coretrack.dto.product.ProductDetailResponse;
import org.example.coretrack.dto.product.SearchProductResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.repository.UserRepository;
import org.example.coretrack.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
    /**
     * Endpoint to search and filter
     * @param search search key (SKU, Name, ShortDescription)
     * @param groupMaterial one or list of product group from front end
     * @param status one or list of statuses from front end
     * @param pagable
     * @return SeachProductResponse
     */
    @GetMapping
    public ResponseEntity<Page<SearchProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> groupMaterial, 
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) Pageable pageable) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        Page<SearchProductResponse> products = productService.findProduct(search, groupMaterial, status, pageable);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(products);
    }

    /**
     * Endpoint to search and filter return all, doesnt use pagenation
     * @param search search key (SKU, Name, ShortDescription)
     * @return SeachProductResponse
     */
    @GetMapping("/all")
    public ResponseEntity<List<SearchProductResponse>> getProducts(
            @RequestParam(required = false) String search) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        List<SearchProductResponse> products = productService.getAllProductsForAutocomplete(search);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(products);
    }

    /**
     * Endpoint for product detail by ID (A1)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getMaterialById(@PathVariable Long id) {
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
    public List<String> getAllMaterialGroups() {
        return productService.getAllGroupName();
    }
}
