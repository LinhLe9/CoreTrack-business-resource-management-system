package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.purchasingTicket.PurchasingTicket;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasingTicketRepository extends JpaRepository<PurchasingTicket, Long> {
    
    Optional<PurchasingTicket> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT pt FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "AND (:search IS NULL OR LOWER(pt.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:ticketStatus IS NULL OR pt.status IN :ticketStatus) " +
           "ORDER BY pt.createdAt DESC")
    Page<PurchasingTicket> findBySearchAndStatus(@Param("search") String search, 
                                                @Param("ticketStatus") List<PurchasingTicketStatus> ticketStatus, 
                                                Pageable pageable);
    
    @Query("SELECT pt FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "AND LOWER(pt.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY pt.createdAt DESC " +
           "LIMIT 10")
    List<PurchasingTicket> findBySearchForAutoComplete(@Param("search") String search);
    
    @Query("SELECT pt FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "ORDER BY pt.createdAt DESC")
    List<PurchasingTicket> findAllActive();
    
    @Query("SELECT COUNT(pt) FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "AND pt.status = :status")
    long countByStatus(@Param("status") PurchasingTicketStatus status);
} 