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
}
