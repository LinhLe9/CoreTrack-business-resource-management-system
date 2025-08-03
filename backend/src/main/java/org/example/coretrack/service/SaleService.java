package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.sale.SaleCardResponse;
import org.example.coretrack.dto.sale.SaleCreateRequest;
import org.example.coretrack.dto.sale.SaleStatusTransitionRule;
import org.example.coretrack.dto.sale.SaleTicketResponse;
import org.example.coretrack.dto.sale.UpdateSaleOrderStatusRequest;
import org.example.coretrack.model.Sale.Order;
import org.example.coretrack.model.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SaleService {
    SaleCardResponse createSaleTicket (SaleCreateRequest request, User user);

    SaleTicketResponse getSaleTicketById(Long id);

    Page<SaleCardResponse> getSaleTickets(String search, List<String> ticketStatus, Pageable pageable);

    List<SaleCardResponse> getAutoComplete(String search);

    List<SaleStatusTransitionRule> getStatusTransitionRules();

    Order cancelSaleTicket(Long ticketId, String reason, User user);

    SaleTicketResponse updateDetailStatus(Long id, UpdateSaleOrderStatusRequest request, User user);

    SaleTicketResponse allocateOrderDetail(Long ticketId, Long detailId, User user);

}
