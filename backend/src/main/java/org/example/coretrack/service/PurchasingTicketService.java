package org.example.coretrack.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchasingTicketService {
    BulkCreatePurchasingTicketResponse bulkCreatePurchasingTicket(BulkCreatePurchasingTicketRequest request, User user);

    PurchasingTicketResponse getPurchasingTicketById (Long id, User user);

    Page<PurchasingTicketCardResponse> getPurchasingTickets (String search, List<String> ticketStatus, Pageable pageable, User user);

    List<PurchasingTicketCardResponse> getAutoComplete(String search, User user);

    List<PurchasingStatusTransitionRule> getStatusTransitionRules();

    PurchasingTicketStatusesResponse getAllPurchasingTicketStatuses();

    PurchasingTicketDetailResponse updateDetailStatus(Long ticketId,Long detailId, UpdatePurchasingDetailStatusRequest request, User user);

    PurchasingTicket cancelPurchasingTicket(Long ticketId, String reason, User user);

    PurchasingTicketDetailResponse cancelPurchasingTicketDetail(Long ticketId, Long detailId, String reason, User user);

    PurchasingTicketDetailResponse getPurchasingTicketDetails (Long id, Long detailId, User user);
}
