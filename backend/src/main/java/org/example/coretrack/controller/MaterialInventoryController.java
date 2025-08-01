package org.example.coretrack.controller;

import java.util.List;
import java.util.Optional;

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
import org.example.coretrack.repository.UserRepository;
import org.example.coretrack.service.MaterialInventoryService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/material-inventory")
public class MaterialInventoryController {
    @Autowired
    private MaterialInventoryService materialInventoryService;

    @Autowired
    private UserRepository userRepository;

    /* 
     * Endpoint to initial inventory stock of material
     */
    @PostMapping("/init")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<AddMaterialInventoryResponse> createProductInventory(@Valid @RequestBody AddMaterialInventoryRequest request) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        AddMaterialInventoryResponse response = materialInventoryService.createMaterialInventory(request, currentUser);
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

    /*
     * Endpoint to set stock to a specific product variant 
     */
    @PutMapping("/{variantId}/set")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<InventoryTransactionResponse> setStock(
        @PathVariable Long variantId, 
        @RequestBody StockSetRequest request
    ) { 
        User currentUser = getCurrentUserFromSecurityContext();
        InventoryTransactionResponse response = materialInventoryService.setStock(variantId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
     * Endpoint to add stock to a specific product variant
     */
    @PutMapping("/{variantId}/add")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<InventoryTransactionResponse> addStock(
        @PathVariable Long variantId,
        @RequestBody StockModifyRequest request
    ){
        User currentUser = getCurrentUserFromSecurityContext();
        InventoryTransactionResponse response = materialInventoryService.addStock(variantId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
     * Endpoint to subtract stock from a specific product variant
     */
    @PutMapping("/{variantId}/subtract")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<InventoryTransactionResponse> subtractStock(
        @PathVariable Long variantId,
        @RequestBody StockModifyRequest request
    ){
        User currentUser = getCurrentUserFromSecurityContext();
        InventoryTransactionResponse response = materialInventoryService.subtractStock(variantId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
     * Endpoint when user search + filter the data
     */
    @GetMapping ("/filter")
    public ResponseEntity<Page<SearchInventoryResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(name = "groupMaterials",required = false) List<String> groupMaterials, 
            @RequestParam(name = "inventoryStatus", required = false) List<String> inventoryStatus,
            @PageableDefault(page = 0, size = 20) Pageable pageable) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        Page<SearchInventoryResponse> productInventory = materialInventoryService.findMaterial(search, groupMaterials, inventoryStatus, pageable);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(productInventory);
    }

    /*
     * Endpoint to autocomplete when user search on search bar
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<AllSearchInventoryResponse>> getAutoComplete(
        @RequestParam(required = false) String search) {
            if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        List<AllSearchInventoryResponse> materialInventory = materialInventoryService.getAllForAutocomplete(search);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(materialInventory);
    }

    /**
     * Endpoint for product inventory detail by ID (A1)
     */
    @GetMapping("/{variantId}")
    public ResponseEntity<MaterialInventoryDetailResponse> getMaterialInventoryById(@PathVariable Long variantId) {
        MaterialInventoryDetailResponse material = materialInventoryService.getMaterialInventoryById(variantId);
        if (material == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Material variant Inventory not found with ID: " + variantId);
        }
        return ResponseEntity.ok(material);
    }

    /*
     * Bulk operations endpoints
     */
    
    /**
     * Bulk set stock for multiple product variants
     */
    @PutMapping("/bulk/set")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<BulkInventoryTransactionResponse> bulkSetStock(
        @Valid @RequestBody BulkStockSetRequest request
    ) {
        User currentUser = getCurrentUserFromSecurityContext();
        BulkInventoryTransactionResponse response = materialInventoryService.bulkSetStock(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Bulk add stock for multiple product variants
     */
    @PutMapping("/bulk/add")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<BulkInventoryTransactionResponse> bulkAddStock(
        @Valid @RequestBody BulkStockModifyRequest request
    ) {
        User currentUser = getCurrentUserFromSecurityContext();
        BulkInventoryTransactionResponse response = materialInventoryService.bulkAddStock(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Bulk subtract stock for multiple product variants
     */
    @PutMapping("/bulk/subtract")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<BulkInventoryTransactionResponse> bulkSubtractStock(
        @Valid @RequestBody BulkStockModifyRequest request
    ) {
        User currentUser = getCurrentUserFromSecurityContext();
        BulkInventoryTransactionResponse response = materialInventoryService.bulkSubtractStock(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Bulk initialize inventory for multiple product variants
     */
    @PostMapping("/bulk/init")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<BulkMInitInventoryResponse> bulkInitInventory(
        @Valid @RequestBody BulkMInitInventoryRequest request
    ) {
        User currentUser = getCurrentUserFromSecurityContext();
        BulkMInitInventoryResponse response = materialInventoryService.bulkInitInventory(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    
    /**
     * Get transaction enums only (for stock operations)
     */
    @GetMapping("/enums/transaction-enums")
    public ResponseEntity<TransactionEnumsResponse> getTransactionEnums() {
        TransactionEnumsResponse response = materialInventoryService.getTransactionEnums();
        return ResponseEntity.ok(response);
    }
}
