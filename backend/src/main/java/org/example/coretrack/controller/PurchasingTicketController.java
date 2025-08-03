package org.example.coretrack.controller;

import java.util.List;

import org.example.coretrack.dto.purchasingTicket.BulkCreatePurchasingTicketRequest;
import org.example.coretrack.dto.purchasingTicket.BulkCreatePurchasingTicketResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingStatusTransitionRule;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketCardResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketDetailResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketStatusesResponse;
import org.example.coretrack.dto.purchasingTicket.UpdatePurchasingDetailStatusRequest;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.purchasingTicket.PurchasingTicket;
import org.example.coretrack.service.PurchasingTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchasing-tickets")
@CrossOrigin(origins = "*")
public class PurchasingTicketController {
    
    @Autowired
    private PurchasingTicketService purchasingTicketService;

    @PostMapping("/bulk-create")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<BulkCreatePurchasingTicketResponse> bulkCreatePurchasingTicket(
            @RequestBody BulkCreatePurchasingTicketRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            BulkCreatePurchasingTicketResponse response = purchasingTicketService.bulkCreatePurchasingTicket(request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchasingTicketResponse> getPurchasingTicketById(@PathVariable Long id) {
        try {
            PurchasingTicketResponse ticket = purchasingTicketService.getPurchasingTicketById(id);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<PurchasingTicketCardResponse>> getPurchasingTickets(
            @RequestParam(required = false) String search,
            @RequestParam(name = "ticketStatus", required = false) List<String> ticketStatus,        
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        try {
            Page<PurchasingTicketCardResponse> tickets = purchasingTicketService.getPurchasingTickets(search, ticketStatus, pageable);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * Endpoint to autocomplete when user search on search bar
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<PurchasingTicketCardResponse>> getAutoComplete(
        @RequestParam(required = false) String search){
           try {
            List<PurchasingTicketCardResponse> tickets = purchasingTicketService.getAutoComplete(search);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } 
    }

    @GetMapping("/status-rules")
    @PreAuthorize("permitAll")
    public ResponseEntity<List<PurchasingStatusTransitionRule>> getStatusTransitionRules() {
        try {
            List<PurchasingStatusTransitionRule> rules = purchasingTicketService.getStatusTransitionRules();
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statuses")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<PurchasingTicketStatusesResponse> getAllPurchasingTicketStatuses() {
        try {
            PurchasingTicketStatusesResponse response = purchasingTicketService.getAllPurchasingTicketStatuses();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{ticketId}/details/{detailId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<PurchasingTicketDetailResponse> updateDetailStatus(
            @PathVariable Long ticketId,
            @PathVariable Long detailId,
            @RequestBody UpdatePurchasingDetailStatusRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PurchasingTicketDetailResponse response = purchasingTicketService.updateDetailStatus(ticketId, detailId, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{ticketId}/cancel")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<PurchasingTicket> cancelPurchasingTicket(
            @PathVariable Long ticketId,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PurchasingTicket ticket = purchasingTicketService.cancelPurchasingTicket(ticketId, reason, user);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{ticketId}/details/{detailId}/cancel")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<PurchasingTicketDetailResponse> cancelPurchasingTicketDetail(
            @PathVariable Long ticketId,
            @PathVariable Long detailId,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PurchasingTicketDetailResponse response = purchasingTicketService.cancelPurchasingTicketDetail(ticketId, detailId, reason, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/details/{detailId}")
    public ResponseEntity<PurchasingTicketDetailResponse> getPurchasingTicketDetails(
        @PathVariable Long id,
        @PathVariable Long detailId) {
        try {
            PurchasingTicketDetailResponse details = purchasingTicketService.getPurchasingTicketDetails(id, detailId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
