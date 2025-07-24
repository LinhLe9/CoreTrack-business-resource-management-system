package org.example.coretrack.controller;

import java.util.List;

import org.example.coretrack.dto.supplier.AddSupplierRequest;
import org.example.coretrack.dto.supplier.AddSupplierResponse;
import org.example.coretrack.dto.supplier.AllSupplierSearchResponse;
import org.example.coretrack.dto.supplier.SearchSupplierResponse;
import org.example.coretrack.dto.supplier.SupplierDetailResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.repository.UserRepository;
import org.example.coretrack.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add-supplier")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AddSupplierResponse> createSupplier(@Valid @RequestBody AddSupplierRequest request) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        AddSupplierResponse response = supplierService.createSupplier(request, currentUser);
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
     * @param countries one or list of product group from front end
     * @param pagable
     * @return SeachProductResponse
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<SearchSupplierResponse>> getSuppliers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> countries, 
            @RequestParam(required = false) Pageable pageable) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        Page<SearchSupplierResponse> suppliers = supplierService.findSupplier(search, countries, pageable);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(suppliers);
    }

     /**
     * Endpoint to search and filter return all, doesnt use pagenation
     * @param search search key (SKU, Name, ShortDescription)
     * @return SeachProductResponse
     */
    @GetMapping("/all")
    public ResponseEntity<List<AllSupplierSearchResponse>> getProducts(
            @RequestParam(required = false) String search) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        List<AllSupplierSearchResponse> supplier = supplierService.getAllSuppliersForAutocomplete(search);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/available-country")
    public ResponseEntity<List<String>> getSupplierFilters() {
        List<String> countries = supplierService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    /**
     * Endpoint for product detail by ID (A1)
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDetailResponse> getSupplierById(@PathVariable Long id) {
        SupplierDetailResponse supplier = supplierService.getSupplierById(id);
        if (supplier == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found with ID: " + id);
        }
        return ResponseEntity.ok(supplier);
    }
}
