package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasingTicketDetailRepository extends JpaRepository<PurchasingTicketDetail, Long> {
    
    Optional<PurchasingTicketDetail> findByIdAndIsActiveTrue(Long id);
    
    List<PurchasingTicketDetail> findByPurchasingTicketIdAndIsActiveTrue(Long ticketId);
    
    @Query("SELECT ptd FROM PurchasingTicketDetail ptd WHERE ptd.purchasingTicket.id = :ticketId AND ptd.isActive = true")
    List<PurchasingTicketDetail> findByTicketId(@Param("ticketId") Long ticketId);
    
    @Query("SELECT COUNT(ptd) FROM PurchasingTicketDetail ptd WHERE ptd.purchasingTicket.id = :ticketId AND ptd.isActive = true")
    long countByTicketId(@Param("ticketId") Long ticketId);
    
    @Query("SELECT COUNT(ptd) FROM PurchasingTicketDetail ptd WHERE ptd.purchasingTicket.id = :ticketId AND ptd.isActive = true AND ptd.status = :status")
    long countByTicketIdAndStatus(@Param("ticketId") Long ticketId, @Param("status") String status);
} 