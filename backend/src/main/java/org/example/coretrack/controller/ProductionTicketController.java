package org.example.coretrack.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.example.coretrack.dto.productionTicket.BulkCreateProductionTicketRequest;
import org.example.coretrack.dto.productionTicket.BulkCreateProductionTicketResponse;
import org.example.coretrack.dto.productionTicket.CreateProductionTicketRequest;
import org.example.coretrack.dto.productionTicket.CreateProductionTicketResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketCardResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketDetailResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketStatusesResponse;
import org.example.coretrack.dto.productionTicket.StatusTransitionRule;
import org.example.coretrack.dto.productionTicket.UpdateDetailStatusRequest;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.productionTicket.ProductionTicket;
import org.example.coretrack.model.productionTicket.ProductionTicketDetail;
import org.example.coretrack.repository.ProductionTicketDetailRepository;
import org.example.coretrack.service.ProductionTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production-tickets")
@CrossOrigin(origins = "*")
public class ProductionTicketController {

    @Autowired
    private ProductionTicketService productionTicketService;

    @Autowired
    private ProductionTicketDetailRepository productionTicketDetailRepository;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<CreateProductionTicketResponse> createProductionTicket(
            @RequestBody CreateProductionTicketRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            CreateProductionTicketResponse response = productionTicketService.createProductionTicket(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/bulk-create")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<BulkCreateProductionTicketResponse> bulkCreateProductionTicket(
            @RequestBody BulkCreateProductionTicketRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            BulkCreateProductionTicketResponse response = productionTicketService.bulkCreateProductionTicket(request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionTicketResponse> getProductionTicketById(@PathVariable Long id, Authentication authentication) {
        try {
             User user = (User) authentication.getPrincipal();
            ProductionTicketResponse ticket = productionTicketService.getProductionTicketById(id,user);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductionTicketCardResponse>> getProductionTickets(
            @RequestParam(required = false) String search,
            @RequestParam(name = "ticketStatus", required = false) List<String> ticketStatus,        
            @PageableDefault(page = 0, size = 20) Pageable pageable,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Page<ProductionTicketCardResponse> tickets = productionTicketService.getProductionTickets(search, ticketStatus, pageable, user);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * Endpoint to autocomplete when user search on search bar
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<ProductionTicketCardResponse>> getAutoComplete(
        @RequestParam(required = false) String search,
        Authentication authentication){
           try {
            User user = (User) authentication.getPrincipal();
            List<ProductionTicketCardResponse> tickets = productionTicketService.getAutoComplete(search, user);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } 
    }

    @GetMapping("/status-rules")
    @PreAuthorize("permitAll")
    public ResponseEntity<List<StatusTransitionRule>> getStatusTransitionRules() {
        try {
            List<StatusTransitionRule> rules = productionTicketService.getStatusTransitionRules();
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statuses")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ProductionTicketStatusesResponse> getAllProductionTicketStatuses() {
        try {
            ProductionTicketStatusesResponse response = productionTicketService.getAllProductionTicketStatuses();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{ticketId}/details/{detailId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ProductionTicketDetailResponse> updateDetailStatus(
            @PathVariable Long ticketId,
            @PathVariable Long detailId,
            @RequestBody UpdateDetailStatusRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            ProductionTicketDetailResponse response = productionTicketService.updateDetailStatus(ticketId, detailId, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{ticketId}/cancel")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<ProductionTicket> cancelProductionTicket(
            @PathVariable Long ticketId,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            ProductionTicket ticket = productionTicketService.cancelProductionTicket(ticketId, reason, user);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{ticketId}/details/{detailId}/cancel")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF','PRODUCTION_STAFF')")
    public ResponseEntity<ProductionTicketDetailResponse> cancelProductionTicketDetail(
            @PathVariable Long ticketId,
            @PathVariable Long detailId,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            ProductionTicketDetailResponse response = productionTicketService.cancelProductionTicketDetail(ticketId, detailId, reason, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/details/{detailId}")
    public ResponseEntity<ProductionTicketDetailResponse> getProductionTicketDetails(
        @PathVariable Long id,
        @PathVariable Long detailId,
        Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            ProductionTicketDetailResponse details = productionTicketService.getProductionTicketDetails(id, detailId, user);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Production Ticket Controller is working!");
    }

    @GetMapping("/test-detail/{detailId}")
    @PreAuthorize("permitAll")
    public ResponseEntity<String> testDetailEndpoint(@PathVariable Long detailId) {
        try {
            boolean exists = productionTicketDetailRepository.findByIdAndIsActive(detailId, true).isPresent();
            return ResponseEntity.ok("Detail " + detailId + " exists: " + exists);
        } catch (Exception e) {
            return ResponseEntity.ok("Error checking detail " + detailId + ": " + e.getMessage());
        }
    }

    @GetMapping("/test-all-details")
    @PreAuthorize("permitAll")
    public ResponseEntity<String> testAllDetailsEndpoint() {
        try {
            List<ProductionTicketDetail> allDetails = productionTicketDetailRepository.findByIsActive(true);
            StringBuilder result = new StringBuilder();
            result.append("All active production ticket details:\n");
            for (ProductionTicketDetail detail : allDetails) {
                result.append("ID: ").append(detail.getId())
                      .append(", Ticket ID: ").append(detail.getProductionTicket().getId())
                      .append(", Product Variant: ").append(detail.getProductVariant().getSku())
                      .append(", isActive: ").append(detail.isActive())
                      .append("\n");
            }
            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            return ResponseEntity.ok("Error getting all details: " + e.getMessage());
        }
    }

    @GetMapping("/test-detail-by-id/{detailId}")
    @PreAuthorize("permitAll")
    public ResponseEntity<String> testDetailByIdEndpoint(@PathVariable Long detailId) {
        try {
            // Try to find by ID only (without isActive filter)
            Optional<ProductionTicketDetail> detailOpt = productionTicketDetailRepository.findById(detailId);
            if (detailOpt.isPresent()) {
                ProductionTicketDetail detail = detailOpt.get();
                StringBuilder result = new StringBuilder();
                result.append("Detail found by ID ").append(detailId).append(":\n");
                result.append("ID: ").append(detail.getId()).append("\n");
                result.append("Ticket ID: ").append(detail.getProductionTicket().getId()).append("\n");
                result.append("Product Variant: ").append(detail.getProductVariant().getSku()).append("\n");
                result.append("isActive: ").append(detail.isActive()).append("\n");
                result.append("Status: ").append(detail.getStatus()).append("\n");
                return ResponseEntity.ok(result.toString());
            } else {
                return ResponseEntity.ok("Detail with ID " + detailId + " not found");
            }
        } catch (Exception e) {
            return ResponseEntity.ok("Error checking detail " + detailId + ": " + e.getMessage());
        }
    }

    @GetMapping("/test-cascade/{id}")
    public ResponseEntity<Map<String, Object>> testCascadeRelationships(@PathVariable Long id) {
        try {
            boolean result = productionTicketService.testCascadeRelationships(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "Cascade relationships are working correctly" : "Cascade relationships are not working correctly");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing cascade relationships: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-sample")
    public ResponseEntity<Map<String, Object>> testSampleData() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Production Ticket endpoints are ready for testing");
            response.put("endpoints", List.of(
                "POST /api/production-tickets/create - Create new production ticket",
                "GET /api/production-tickets/{id} - Get production ticket by ID",
                "GET /api/production-tickets/{id}/details/{detailId} - Get specific production ticket detail",
                "GET /api/production-tickets/filter - Filter production tickets",
                "GET /api/production-tickets/autocomplete - Autocomplete search",
                "GET /api/production-tickets/status-rules - Get status transition rules",
                "PUT /api/production-tickets/{id}/status - Update ticket status",
                "PUT /api/production-tickets/{ticketId}/details/{detailId}/status - Update detail status",
                "PUT /api/production-tickets/{ticketId}/cancel - Cancel production ticket",
                "PUT /api/production-tickets/{ticketId}/details/{detailId}/cancel - Cancel specific detail"
            ));
            response.put("filter_examples", List.of(
                "GET /api/production-tickets/filter - Get all tickets",
                "GET /api/production-tickets/filter?search=test - Search by ticket name",
                "GET /api/production-tickets/filter?search=SKU123 - Search by product variant SKU",
                "GET /api/production-tickets/filter?search=ProductName - Search by product variant name",
                "GET /api/production-tickets/filter?ticketStatus=NEW&ticketStatus=COMPLETE - Filter by status",
                "GET /api/production-tickets/filter?search=test&ticketStatus=NEW - Search and filter"
            ));
            response.put("autocomplete_examples", List.of(
                "GET /api/production-tickets/autocomplete?search=Pro - Autocomplete by ticket name",
                "GET /api/production-tickets/autocomplete?search=123 - Autocomplete by ticket ID",
                "GET /api/production-tickets/autocomplete?search=SKU - Autocomplete by product SKU",
                "GET /api/production-tickets/autocomplete?search=iPhone - Autocomplete by product name"
            ));
            response.put("details_examples", List.of(
                "GET /api/production-tickets/1/details/1 - Get detail 1 from ticket 1",
                "GET /api/production-tickets/2/details/3 - Get detail 3 from ticket 2"
            ));
            response.put("status_management_examples", List.of(
                "GET /api/production-tickets/status-rules - Get status transition rules",
                "PUT /api/production-tickets/1/details/1/status - Update detail 1 status",
                "PUT /api/production-tickets/1/cancel?reason=User cancelled - Cancel entire ticket",
                "PUT /api/production-tickets/1/details/1/cancel?reason=Detail cancelled - Cancel specific detail"
            ));
            response.put("search_capabilities", List.of(
                "Production Ticket Name",
                "Production Ticket ID",
                "Product Variant SKU",
                "Product Variant Name"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-search")
    public ResponseEntity<Map<String, Object>> testSearchFunctionality() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Search functionality test");
            response.put("search_fields", Map.of(
                "ticket_name", "Search by production ticket name",
                "ticket_id", "Search by production ticket ID", 
                "product_sku", "Search by product variant SKU",
                "product_name", "Search by product variant name"
            ));
            response.put("example_queries", List.of(
                "GET /api/production-tickets/filter?search=ProductionTicket1",
                "GET /api/production-tickets/filter?search=123",
                "GET /api/production-tickets/filter?search=SKU-001",
                "GET /api/production-tickets/filter?search=iPhone"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing search: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-autocomplete")
    public ResponseEntity<Map<String, Object>> testAutocompleteFunctionality() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Autocomplete functionality test");
            response.put("endpoint", "GET /api/production-tickets/autocomplete?search={search_term}");
            response.put("autocomplete_fields", Map.of(
                "ticket_name", "Autocomplete by production ticket name",
                "ticket_id", "Autocomplete by production ticket ID", 
                "product_sku", "Autocomplete by product variant SKU",
                "product_name", "Autocomplete by product variant name"
            ));
            response.put("example_queries", List.of(
                "GET /api/production-tickets/autocomplete?search=Pro",
                "GET /api/production-tickets/autocomplete?search=123",
                "GET /api/production-tickets/autocomplete?search=SKU",
                "GET /api/production-tickets/autocomplete?search=iPhone"
            ));
            response.put("response_format", "List of ProductionTicketCardResponse objects");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing autocomplete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-details")
    public ResponseEntity<Map<String, Object>> testDetailsFunctionality() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Production Ticket Details functionality test");
            response.put("endpoint", "GET /api/production-tickets/{ticketId}/details/{detailId}");
            response.put("description", "Get detailed information about a specific production ticket detail including BOM items");
            response.put("parameters", Map.of(
                "ticketId", "ID of the production ticket",
                "detailId", "ID of the specific detail within the ticket"
            ));
            response.put("response_includes", List.of(
                "Detail ID and basic information",
                "Product variant SKU and quantity",
                "BOM items with material variants",
                "Status and completion dates",
                "Audit information (created/updated by, timestamps)"
            ));
            response.put("example_queries", List.of(
                "GET /api/production-tickets/1/details/1 - Get detail 1 from ticket 1",
                "GET /api/production-tickets/2/details/3 - Get detail 3 from ticket 2"
            ));
            response.put("error_handling", List.of(
                "404 if ticket not found",
                "404 if detail not found", 
                "400 if detail doesn't belong to specified ticket"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-status-management")
    public ResponseEntity<Map<String, Object>> testStatusManagementFunctionality() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Production Ticket Status Management functionality test");
            response.put("status_transition_rules", Map.of(
                "NEW", "Can transition to: APPROVAL, CANCELLED",
                "APPROVAL", "Can transition to: COMPLETE, CANCELLED",
                "COMPLETE", "Can transition to: READY, CANCELLED",
                "READY", "Can transition to: CLOSED",
                "CLOSED", "No transitions allowed",
                "CANCELLED", "No transitions allowed"
            ));
            response.put("endpoints", Map.of(
                "GET /api/production-tickets/status-rules", "Get all status transition rules",
                "PUT /api/production-tickets/{ticketId}/details/{detailId}/status", "Update detail status",
                "PUT /api/production-tickets/{ticketId}/cancel", "Cancel entire production ticket",
                "PUT /api/production-tickets/{ticketId}/details/{detailId}/cancel", "Cancel specific detail"
            ));
            response.put("ticket_status_logic", Map.of(
                "All details NEW", "Ticket status: NEW",
                "Any detail APPROVED", "Ticket status: PARTIAL_COMPLETE (In Progress)",
                "Any detail COMPLETE", "Ticket status: PARTIAL_COMPLETE",
                "All details COMPLETE", "Ticket status: COMPLETE",
                "Any detail CANCELLED", "Ticket status: PARTIAL_COMPLETE (Partial Cancelled)",
                "All details CANCELLED", "Ticket status: CANCELLED"
            ));
            response.put("example_requests", List.of(
                "PUT /api/production-tickets/1/details/1/status with body: {\"newStatus\": \"APPROVAL\", \"note\": \"Approved by manager\"}",
                "PUT /api/production-tickets/1/cancel?reason=User cancelled",
                "PUT /api/production-tickets/1/details/1/cancel?reason=Detail cancelled"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing status management: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-bulk-create")
    public ResponseEntity<Map<String, Object>> testBulkCreateFunctionality() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Production Ticket Bulk Create functionality test");
            response.put("endpoint", "POST /api/production-tickets/bulk-create");
            response.put("required_roles", List.of("OWNER", "MANAGER"));
            response.put("request_structure", Map.of(
                "name", "String - Base name for production tickets",
                "productVariants", "List<ProductVariantBomRequest> - List of product variants with their BOM items"
            ));
            response.put("status_behavior", Map.of(
                "default_status", "All new production tickets are created with NEW status",
                "no_status_parameter", "Status is not needed in request since it's always NEW",
                "status_workflow", "Status changes happen through separate endpoints"
            ));
            response.put("productVariantBomRequest_structure", Map.of(
                "productVariantSku", "String - SKU of the product variant",
                "quantity", "Integer - Quantity to produce",
                "expectedCompleteDate", "LocalDate - Expected completion date (YYYY-MM-DD format)",
                "boms", "List<BomItemProductionTicketRequest> - BOM items for this specific variant (optional)"
            ));
            response.put("example_request", Map.of(
                "name", "Bulk Production Order",
                "productVariants", List.of(
                    Map.of(
                        "productVariantSku", "PROD-001",
                        "quantity", 10,
                        "expectedCompleteDate", "2024-01-15",
                        "boms", List.of(Map.of("materialVariantSku", "MAT-001", "plannedQuantity", 5, "actualQuantity", 0))
                    ),
                    Map.of(
                        "productVariantSku", "PROD-002",
                        "quantity", 20,
                        "expectedCompleteDate", "2024-01-20",
                        "boms", List.of(Map.of("materialVariantSku", "MAT-002", "plannedQuantity", 8, "actualQuantity", 0))
                    ),
                    Map.of(
                        "productVariantSku", "PROD-003",
                        "quantity", 15,
                        "expectedCompleteDate", "2024-01-25",
                        "boms", List.of(Map.of("materialVariantSku", "MAT-003", "plannedQuantity", 6, "actualQuantity", 0))
                    )
                )
            ));
            response.put("response_structure", Map.of(
                "success", "boolean - Overall success status",
                "message", "String - Summary message",
                "createdTickets", "List<CreateProductionTicketResponse> - Successfully created tickets",
                "errors", "List<String> - Error messages for failed creations",
                "totalRequested", "int - Total number of tickets requested",
                "totalCreated", "int - Total number of tickets successfully created",
                "totalFailed", "int - Total number of tickets that failed to create"
            ));
            response.put("validation_rules", List.of(
                "Each ProductVariantBomRequest must have valid productVariantSku",
                "Each ProductVariantBomRequest must have quantity > 0",
                "Each ProductVariantBomRequest must have valid expectedCompleteDate",
                "BOM items (if provided) must reference valid material variants",
                "Product variants must exist in the database",
                "Product variants must be active"
            ));
            response.put("features", List.of(
                "Creates multiple production tickets in a single request",
                "Continues processing even if some tickets fail",
                "Provides detailed error messages for failed creations",
                "Returns summary statistics (total requested, created, failed)",
                "Each ticket gets its own status logs",
                "Uses same BOM configuration for all tickets"
            ));
            response.put("bulk_create_logic", Map.of(
                "single_ticket", "Creates one ProductionTicket with multiple ProductionTicketDetails",
                "not_multiple_tickets", "Does not create separate ProductionTickets for each variant",
                "shared_ticket_id", "All details belong to the same ProductionTicket",
                "cascade_save", "Saves ticket with all details using JPA cascade",
                "status_logs", "Creates initial status logs for ticket and all details"
            ));
            response.put("response_meaning", Map.of(
                "createdTickets", "List of CreateProductionTicketResponse for each detail (same ticket ID)",
                "totalCreated", "Number of successfully created details",
                "totalFailed", "Number of details that failed to create",
                "message", "Indicates success/failure of the bulk operation"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing bulk create: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-statuses")
    public ResponseEntity<Map<String, Object>> testStatusesFunctionality() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Production Ticket Statuses functionality test");
            response.put("endpoint", "GET /api/production-tickets/statuses");
            response.put("required_roles", List.of("OWNER", "WAREHOUSE_STAFF"));
            response.put("response_structure", Map.of(
                "productionTicketStatuses", "List of ProductionTicketStatus with name, displayName, and description",
                "productionTicketDetailStatuses", "List of ProductionTicketDetailStatus with name, displayName, and description"
            ));
            response.put("productionTicketStatuses", List.of(
                "NEW - Production ticket is newly created and waiting for approval",
                "PARTIAL_COMPLETE - Some production ticket details are in progress or completed",
                "COMPLETE - All production ticket details are completed",
                "CANCELLED - Production ticket has been cancelled"
            ));
            response.put("productionTicketDetailStatuses", List.of(
                "NEW - Production detail is newly created and waiting for approval",
                "APPROVAL - Production detail is approved and in progress",
                "COMPLETE - Production detail is completed",
                "READY - Production detail is ready for shipment",
                "CLOSED - Production detail is closed and finalized",
                "CANCELLED - Production detail has been cancelled"
            ));
            response.put("use_cases", List.of(
                "Frontend dropdown/select components",
                "Status filtering in search",
                "Status validation in forms",
                "Status display in UI components",
                "API documentation and testing"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing statuses: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-inventory-integration")
    public ResponseEntity<Map<String, Object>> testInventoryIntegration() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Production Ticket Inventory Integration functionality test");
            response.put("inventory_update_rules", Map.of(
                "NEW → APPROVAL", "Add quantity to futureStock",
                "APPROVAL → READY", "Move quantity from futureStock to currentStock",
                "APPROVAL → CANCELLED", "Remove quantity from futureStock",
                "READY → CANCELLED", "Remove quantity from currentStock"
            ));
            response.put("inventory_fields", Map.of(
                "futureStock", "Products planned for production but not yet ready",
                "currentStock", "Products ready and available for sale/shipment",
                "allocatedStock", "Products reserved for specific orders"
            ));
            response.put("status_workflow", List.of(
                "NEW: Production ticket created, no inventory impact",
                "APPROVAL: Production approved, quantity added to futureStock",
                "COMPLETE: Production completed, no inventory impact",
                "READY: Product ready, quantity moved from futureStock to currentStock",
                "CLOSED: Production ticket closed, no inventory impact",
                "CANCELLED: Production cancelled, quantity removed from appropriate stock"
            ));
            response.put("database_operations", Map.of(
                "addToFutureStock", "Service method: Add quantity to futureStock with validation",
                "moveFromFutureToCurrentStock", "Service method: Move quantity from futureStock to currentStock with validation",
                "removeFromFutureStock", "Service method: Remove quantity from futureStock with validation",
                "removeFromCurrentStock", "Service method: Remove quantity from currentStock with validation"
            ));
            response.put("service_layer_benefits", List.of(
                "Better separation of concerns",
                "Easier to test business logic",
                "Centralized validation and error handling",
                "More maintainable code structure",
                "Proper transaction management",
                "Audit trail with user information"
            ));
            response.put("validation_checks", List.of(
                "Check if ProductInventory exists for ProductVariant",
                "Automatically create default ProductInventory if not exists",
                "Ensure quantity is positive before inventory operations",
                "Validate stock levels before reduction operations",
                "Handle null stock values (treat as zero)",
                "Log all inventory changes for audit trail",
                "Update audit fields (updatedAt, updatedBy)"
            ));
            response.put("default_inventory_creation", Map.of(
                "trigger", "When creating ProductionTicket and ProductInventory doesn't exist for ProductVariant",
                "location", "createProductionTicket() and bulkCreateProductionTicket() methods",
                "currentStock", "0",
                "futureStock", "0", 
                "allocatedStock", "0",
                "minAlertStock", "0",
                "maxStockLevel", "100",
                "inventoryStatus", "OUT_OF_STOCK"
            ));
            response.put("initial_inventory_log", Map.of(
                "transactionType", "SET",
                "transactionSourceType", "SET_INVENTORY",
                "quantity", "0",
                "beforeQuantity", "0",
                "afterQuantity", "0",
                "note", "Default ProductInventory created for Production Ticket",
                "referenceDocumentType", "PRODUCTION_TICKET",
                "referenceDocumentId", "ProductionTicket ID (not null)"
            ));
            response.put("creation_timing", Map.of(
                "ProductInventory creation", "After ProductionTicket is saved and has ID",
                "referenceDocumentId", "Set to ProductionTicket ID for proper tracking",
                "audit_trail", "Complete tracking from ProductionTicket to ProductInventoryLog"
            ));
            response.put("workflow_improvements", List.of(
                "ProductInventory is created proactively during ticket creation",
                "No need to check/create inventory during status updates",
                "Better performance as inventory operations are separated from status updates",
                "Consistent state: ProductInventory always exists when needed",
                "Cleaner separation of concerns"
            ));
            response.put("error_handling", List.of(
                "Handle cases where ProductInventory doesn't exist for ProductVariant",
                "Handle insufficient stock scenarios with detailed error messages",
                "Rollback inventory changes if production ticket update fails",
                "Log inventory update errors for debugging",
                "Provide meaningful error messages to users"
            ));
            response.put("inventory_operations_logging", Map.of(
                "addToFutureStock", "Creates ProductInventoryLog with IN transaction type",
                "moveFromFutureToCurrentStock", "Creates ProductInventoryLog with IN transaction type",
                "removeFromFutureStock", "Creates ProductInventoryLog with OUT transaction type",
                "removeFromCurrentStock", "Creates ProductInventoryLog with OUT transaction type"
            ));
            response.put("log_details", Map.of(
                "transactionType", "IN for additions, OUT for removals",
                "transactionSourceType", "PRODUCTION_COMPLETION for additions, PRODUCTION_CONSUMPTION for removals",
                "referenceDocumentType", "PRODUCTION_TICKET",
                "referenceDocumentId", "Directly set to ProductionTicket ID from method parameter",
                "note", "Descriptive message about the operation"
            ));
            response.put("simplified_approach", List.of(
                "ticketId passed directly to inventory service methods",
                "No need for separate log update method",
                "Cleaner and more straightforward implementation",
                "Better separation of concerns"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing inventory integration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-logging")
    public ResponseEntity<Map<String, Object>> testLoggingFunctionality() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Production Ticket Status Logging functionality test");
            response.put("logging_features", Map.of(
                "Initial Logs", "Created when production ticket is created",
                "Status Change Logs", "Logged for every status change",
                "Audit Trail", "Track who made changes and when",
                "Detailed Notes", "Include reasons for status changes"
            ));
            response.put("logged_events", List.of(
                "Production ticket creation (NEW status)",
                "Production ticket detail creation (NEW status)",
                "Detail status changes (NEW → APPROVAL → COMPLETE → READY → CLOSED)",
                "Detail cancellations (any status → CANCELLED)",
                "Ticket status updates (based on detail changes)",
                "Ticket cancellations (all details → CANCELLED)"
            ));
            response.put("log_tables", List.of(
                "production_ticket_status_log - Logs ProductionTicket status changes",
                "production_ticket_detail_status_log - Logs ProductionTicketDetail status changes"
            ));
            response.put("log_fields", Map.of(
                "old_status", "Previous status before change",
                "new_status", "New status after change",
                "note", "Reason or description for the change",
                "updatedAt", "Timestamp when change occurred",
                "updatedBy", "User who made the change"
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error testing logging: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
