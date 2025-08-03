package org.example.coretrack.controller;

import java.util.List;
import org.example.coretrack.dto.sale.SaleCardResponse;
import org.example.coretrack.dto.sale.SaleCreateRequest;
import org.example.coretrack.dto.sale.SaleStatusTransitionRule;
import org.example.coretrack.dto.sale.SaleTicketResponse;
import org.example.coretrack.dto.sale.UpdateSaleOrderStatusRequest;
import org.example.coretrack.model.Sale.Order;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.service.SaleService;
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
@RequestMapping("/api/sale")
@CrossOrigin(origins = "*")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @PostMapping("/bulk-create")
    @PreAuthorize("hasAnyRole('OWNER','SALE_STAFF')")
    public ResponseEntity<SaleCardResponse> createSaleTicket(
            @RequestBody SaleCreateRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            SaleCardResponse response = saleService.createSaleTicket(request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleTicketResponse> getSaleTicketById(@PathVariable Long id) {
        try {
            SaleTicketResponse ticket = saleService.getSaleTicketById(id);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<SaleCardResponse>> getSaleTickets(
            @RequestParam(required = false) String search,
            @RequestParam(name = "ticketStatus", required = false) List<String> ticketStatus,        
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        try {
            Page<SaleCardResponse> tickets = saleService.getSaleTickets(search, ticketStatus, pageable);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * Endpoint to autocomplete when user search on search bar
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<SaleCardResponse>> getAutoComplete(
        @RequestParam(required = false) String search){
           try {
            List<SaleCardResponse> tickets = saleService.getAutoComplete(search);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } 
    }

    @GetMapping("/status-rules")
    @PreAuthorize("permitAll")
    public ResponseEntity<List<SaleStatusTransitionRule>> getStatusTransitionRules() {
        try {
            List<SaleStatusTransitionRule> rules = saleService.getStatusTransitionRules();
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<SaleTicketResponse> updateDetailStatus(
            @PathVariable Long id,
            @RequestBody UpdateSaleOrderStatusRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            SaleTicketResponse response = saleService.updateDetailStatus(id, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{ticketId}/cancel")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Order> cancelSaleTicket(
            @PathVariable Long ticketId,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Order ticket = saleService.cancelSaleTicket(ticketId, reason, user);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{ticketId}/detail/{detailId}/allocate")
    @PreAuthorize("hasAnyRole('OWNER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<SaleTicketResponse> allocateOrderDetail(
            @PathVariable Long ticketId,
            @PathVariable Long detailId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            SaleTicketResponse response = saleService.allocateOrderDetail(ticketId, detailId, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
