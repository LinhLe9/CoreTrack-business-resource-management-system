package org.example.coretrack.controller;

import java.util.List;

import org.example.coretrack.dto.material.AddMaterialRequest;
import org.example.coretrack.dto.material.AddMaterialResponse;
import org.example.coretrack.dto.material.AllMaterialSearchResponse;
import org.example.coretrack.dto.material.MaterialDetailResponse;
import org.example.coretrack.dto.material.MaterialGroupResponse;
import org.example.coretrack.dto.material.SearchMaterialResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.repository.UserRepository;
import org.example.coretrack.service.MaterialService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add-material")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AddMaterialResponse> createProduct(@Valid @RequestBody AddMaterialRequest request) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        AddMaterialResponse response = materialService.createMaterial(request, currentUser);
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
     * @param groupMaterials one or list of product group from front end
     * @param statuses one or list of statuses from front end
     * @param pagable
     * @return SeachProductResponse
     */
    @GetMapping ("/filter")
    public ResponseEntity<Page<SearchMaterialResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(name = "groupMaterials", required = false) List<String> groupMaterials, 
            @RequestParam(name = "status", required = false) List<String> status,
            @PageableDefault(page = 0, size = 20) Pageable pageable) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        Page<SearchMaterialResponse> materials = materialService.findMaterial(search, groupMaterials, status, pageable);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AllMaterialSearchResponse>> getMaterials(
            @RequestParam(required = false) String search) { 

        // Validation E1: Invalid Search Keyword/Format
        if (search != null && search.length() > 255) { // maximum 255 characters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
        }

        List<AllMaterialSearchResponse> materials = materialService.getAllMaterialsForAutocomplete(search);

        // A2: No matching results - frontend solves
        return ResponseEntity.ok(materials);
    }

    /**
     * Endpoint for product detail by ID (A1)
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDetailResponse> getMaterialById(@PathVariable Long id) {
        MaterialDetailResponse material = materialService.getMaterialById(id);
        if (material == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found with ID: " + id);
        }
        return ResponseEntity.ok(material);
    }

    /*
     * Endpoint for returning the product group name
     */
    @GetMapping("/material-groups")
    public ResponseEntity<List<MaterialGroupResponse>> getAllMaterialGroups() {
        try {
            List<MaterialGroupResponse> groups = materialService.getAllGroupName();
            System.out.println("Result: " + groups);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            e.printStackTrace(); // log error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build(); 
        }
    }
}
