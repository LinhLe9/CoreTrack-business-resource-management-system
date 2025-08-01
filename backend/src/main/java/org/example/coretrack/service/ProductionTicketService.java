package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.productionTicket.BomItemProductionTicketRequest;
import org.example.coretrack.dto.productionTicket.BomItemProductionTicketResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionTicketService {
    CreateProductionTicketResponse createProductionTicket(CreateProductionTicketRequest request, User user);
    
    ProductionTicketResponse getProductionTicketById(Long id);
    
    Page<ProductionTicketCardResponse> getProductionTickets(String search, List<String> ticketStatus, Pageable pageable);

    List<ProductionTicketCardResponse> getAutoComplete (String search);
    
    ProductionTicketDetailResponse getProductionTicketDetails(Long id, Long detailId);
        
    // New methods for status management
    BulkCreateProductionTicketResponse bulkCreateProductionTicket(BulkCreateProductionTicketRequest request, User user);

    List<StatusTransitionRule> getStatusTransitionRules();
    
    ProductionTicketDetailResponse updateDetailStatus(Long ticketId, Long detailId, UpdateDetailStatusRequest request, User user);
    
    ProductionTicket cancelProductionTicket(Long ticketId, String reason, User user);
    
    ProductionTicketDetailResponse cancelProductionTicketDetail(Long ticketId, Long detailId, String reason, User user);
    
    boolean testCascadeRelationships(Long ticketId);

    ProductionTicketStatusesResponse getAllProductionTicketStatuses();
}
