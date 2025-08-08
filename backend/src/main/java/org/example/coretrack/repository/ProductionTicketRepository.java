package org.example.coretrack.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.productionTicket.ProductionTicket;
import org.example.coretrack.model.productionTicket.ProductionTicketStatus;
import org.example.coretrack.model.auth.Company;

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
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find production tickets by status and company
     */
    List<ProductionTicket> findByStatusAndCompany(ProductionTicketStatus status, Company company);
    
    /**
     * Find production tickets by active status and company
     */
    List<ProductionTicket> findByIsActiveAndCompany(boolean isActive, Company company);
    
    /**
     * Find production tickets by status, active status and company
     */
    List<ProductionTicket> findByStatusAndIsActiveAndCompany(ProductionTicketStatus status, boolean isActive, Company company);
    
    /**
     * Find production ticket by ID and company
     */
    Optional<ProductionTicket> findByIdAndCompany(Long id, Company company);
    
    /**
     * Find production ticket by ID, active status and company
     */
    Optional<ProductionTicket> findByIdAndIsActiveAndCompany(Long id, boolean isActive, Company company);
    
    // Method for BusinessAnalyticsService
    List<ProductionTicket> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT pt FROM ProductionTicket pt WHERE pt.isActive = true")
    Page<ProductionTicket> findAllActive(Pageable pageable);
    
    @Query("SELECT pt FROM ProductionTicket pt WHERE pt.isActive = true AND pt.company = :company")
    Page<ProductionTicket> findAllActiveByCompany(@Param("company") Company company, Pageable pageable);

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
      AND (:ticketStatus IS NULL OR pt.status IN :ticketStatus)
      AND pt.isActive = true AND pt.company = :company
    """)
    Page<ProductionTicket> findAllActiveByCriteriaAndCompany(
        @Param("search") String search,
        @Param("ticketStatus") List<ProductionTicketStatus> ticketStatus,
        @Param("company") Company company,
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
      AND pt.isActive = true AND pt.company = :company
    """)
    List<ProductionTicket> findAllBySearchAndCompany(@Param("search") String search, @Param("company") Company company);
    
    @Query("SELECT pt FROM ProductionTicket pt WHERE pt.status = :status AND pt.isActive = true")
    Page<ProductionTicket> findByStatusAndActive(@Param("status") ProductionTicketStatus status, Pageable pageable);
    
    @Query("SELECT pt FROM ProductionTicket pt WHERE pt.status = :status AND pt.isActive = true AND pt.company = :company")
    Page<ProductionTicket> findByStatusAndActiveAndCompany(@Param("status") ProductionTicketStatus status, @Param("company") Company company, Pageable pageable);
    
    Optional<ProductionTicket> findByIdAndIsActive(Long id, boolean isActive);
    
    // Search by name
    Page<ProductionTicket> findByNameContainingIgnoreCaseAndIsActive(String name, boolean isActive, Pageable pageable);
    
    // Search by name and company
    Page<ProductionTicket> findByNameContainingIgnoreCaseAndIsActiveAndCompany(String name, boolean isActive, Company company, Pageable pageable);
    
    // Filter by multiple statuses
    Page<ProductionTicket> findByStatusInAndIsActive(List<ProductionTicketStatus> statuses, boolean isActive, Pageable pageable);
    
    // Filter by multiple statuses and company
    Page<ProductionTicket> findByStatusInAndIsActiveAndCompany(List<ProductionTicketStatus> statuses, boolean isActive, Company company, Pageable pageable);
} 