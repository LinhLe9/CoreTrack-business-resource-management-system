package org.example.coretrack.controller;

import java.util.List;

import org.example.coretrack.dto.material.AddMaterialRequest;
import org.example.coretrack.dto.material.AddMaterialResponse;
import org.example.coretrack.dto.material.AllMaterialSearchResponse;
import org.example.coretrack.dto.material.MaterialDetailResponse;
import org.example.coretrack.dto.material.MaterialGroupResponse;
import org.example.coretrack.dto.material.SearchMaterialResponse;
import org.example.coretrack.dto.material.UpdateMaterialRequest;
import org.example.coretrack.dto.material.UpdateMaterialResponse;
import org.example.coretrack.dto.material.ChangeMaterialStatusRequest;
import org.example.coretrack.dto.material.ChangeMaterialStatusResponse;
import org.example.coretrack.dto.material.MaterialStatusTransitionResponse;
import org.example.coretrack.dto.material.UoMResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.UoM;
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
import org.springframework.web.bind.annotation.PutMapping;
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
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated or not found");
        }
        AddMaterialResponse response = materialService.createMaterial(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UpdateMaterialResponse> updateMaterial(@PathVariable Long id, @Valid @RequestBody UpdateMaterialRequest request) {
        // Get current user information from Spring Security Context
        User currentUser = getCurrentUserFromSecurityContext();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated or not found");
        }
        UpdateMaterialResponse response = materialService.updateMaterial(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    private User getCurrentUserFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("No authentication found");
                return null;
            }

            System.out.println("Authentication principal class: " + authentication.getPrincipal().getClass().getName());
            System.out.println("Authentication principal: " + authentication.getPrincipal());

            // Check if principal is User object directly
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                System.out.println("User found directly from principal: " + user.getEmail());
                return user;
            }
            // Check if principal is UserDetails
            else if (authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                System.out.println("Username from UserDetails: " + username);
                return userRepository.findByUsername(username).orElse(null);
            }
            else {
                System.out.println("Unknown principal type: " + authentication.getPrincipal().getClass().getName());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error getting current user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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

        try {
            System.out.println("=== Material Filter Request ===");
            System.out.println("Search: " + search);
            System.out.println("GroupMaterials: " + groupMaterials);
            System.out.println("Status: " + status);
            System.out.println("Pageable: " + pageable);

            // Validation E1: Invalid Search Keyword/Format
            if (search != null && search.length() > 255) { // maximum 255 characters
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword is too long. Max 255 characters allowed.");
            }

            Page<SearchMaterialResponse> materials = materialService.findMaterial(search, groupMaterials, status, pageable);
            System.out.println("Found " + materials.getTotalElements() + " materials");

            // A2: No matching results - frontend solves
            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            System.err.println("Error in MaterialController.getProducts: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing material filter request: " + e.getMessage());
        }
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

    /*
     * Endpoint for returning the list of UoM values
     */
    @GetMapping("/uom")
    public ResponseEntity<List<UoMResponse>> getAllUoM() {
        try {
            List<UoMResponse> uomList = java.util.Arrays.stream(UoM.values())
                .map(uom -> new UoMResponse(uom.name(), uom.getDisplayName()))
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(uomList);
        } catch (Exception e) {
            e.printStackTrace(); // log error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build(); 
        }
    }

    /**
     * Endpoint for changing material status
     * Precondition: Either Owner or Warehouse Staff is logged in
     * Activation: User selects material and initiates status change
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ChangeMaterialStatusResponse> changeMaterialStatus(
            @PathVariable Long id, 
            @Valid @RequestBody ChangeMaterialStatusRequest request) {
        
        try {
            // Get current user information from Spring Security Context
            User currentUser = getCurrentUserFromSecurityContext();
            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated or not found");
            }

            ChangeMaterialStatusResponse response = materialService.changeMaterialStatus(id, request, currentUser);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                // E1: Invalid Status Transition
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            // Handle material not found or other runtime exceptions
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ChangeMaterialStatusResponse(id, null, request.getNewStatus(), 
                    e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ChangeMaterialStatusResponse(id, null, request.getNewStatus(), 
                    "Internal server error: " + e.getMessage(), false));
        }
    }

    /**
     * Endpoint for getting available status transitions for a material
     * Step 1 of main flow: Display current status and available options
     */
    @GetMapping("/{id}/status-transitions")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<MaterialStatusTransitionResponse> getAvailableStatusTransitions(@PathVariable Long id) {
        try {
            MaterialStatusTransitionResponse response = materialService.getAvailableStatusTransitions(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Handle material not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MaterialStatusTransitionResponse(id, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MaterialStatusTransitionResponse(id, null, null, 
                    "Internal server error: " + e.getMessage()));
        }
    }
}
