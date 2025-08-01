package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.productionTicket.ProductionTicket;
import org.example.coretrack.model.productionTicket.ProductionTicketStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionTicketRepository extends JpaRepository<ProductionTicket, Long> {
    
    List<ProductionTicket> findByStatus(ProductionTicketStatus status);
    
    List<ProductionTicket> findByIsActive(boolean isActive);
    
    List<ProductionTicket> findByStatusAndIsActive(ProductionTicketStatus status, boolean isActive);
    
    @Query("SELECT pt FROM ProductionTicket pt WHERE pt.isActive = true")
    Page<ProductionTicket> findAllActive(Pageable pageable);

    @Query("""
    SELECT DISTINCT pt
    FROM ProductionTicket pt
    LEFT JOIN pt.ticketDetail ptd
    LEFT JOIN ptd.productVariant pv
    WHERE (:search IS NULL OR 
           LOWER(pt.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
           CAST(pt.id AS string) LIKE CONCAT('%', :search, '%') OR
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:ticketStatus IS NULL OR pt.status IN :ticketStatus)
      AND pt.isActive = true
    """)
    Page<ProductionTicket> findAllActiveByCriteria(
        @Param("search") String search,
        @Param("ticketStatus") List<ProductionTicketStatus> ticketStatus,    
        Pageable pageable);

    @Query("""
    SELECT DISTINCT pt
    FROM ProductionTicket pt
    LEFT JOIN pt.ticketDetail ptd
    LEFT JOIN ptd.productVariant pv
    WHERE (:search IS NULL OR 
           LOWER(pt.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
           CAST(pt.id AS string) LIKE CONCAT('%', :search, '%') OR
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND pt.isActive = true
    """)
    List<ProductionTicket> findAllBySearch(@Param("search") String search);
    
    @Query("SELECT pt FROM ProductionTicket pt WHERE pt.status = :status AND pt.isActive = true")
    Page<ProductionTicket> findByStatusAndActive(@Param("status") ProductionTicketStatus status, Pageable pageable);
    
    Optional<ProductionTicket> findByIdAndIsActive(Long id, boolean isActive);
    
    // Search by name
    Page<ProductionTicket> findByNameContainingIgnoreCaseAndIsActive(String name, boolean isActive, Pageable pageable);
    
    // Filter by multiple statuses
    Page<ProductionTicket> findByStatusInAndIsActive(List<ProductionTicketStatus> statuses, boolean isActive, Pageable pageable);
} 