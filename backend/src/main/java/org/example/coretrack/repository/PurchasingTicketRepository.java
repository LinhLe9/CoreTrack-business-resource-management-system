package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.purchasingTicket.PurchasingTicket;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketStatus;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasingTicketRepository extends JpaRepository<PurchasingTicket, Long> {
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find purchasing ticket by ID and company
     */
    Optional<PurchasingTicket> findByIdAndCompany(Long id, Company company);
    
    /**
     * Find purchasing ticket by ID, active status and company
     */
    Optional<PurchasingTicket> findByIdAndIsActiveTrueAndCompany(Long id, Company company);
    
    /**
     * Find purchasing tickets by search criteria and company
     */
    @Query("SELECT pt FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "AND pt.company = :company " +
           "AND (:search IS NULL OR LOWER(pt.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:ticketStatus IS NULL OR pt.status IN :ticketStatus) " +
           "ORDER BY pt.createdAt DESC")
    Page<PurchasingTicket> findBySearchAndStatusAndCompany(@Param("search") String search, 
                                                          @Param("ticketStatus") List<PurchasingTicketStatus> ticketStatus,
                                                          @Param("company") Company company,
                                                          Pageable pageable);
    
    /**
     * Find purchasing tickets for autocomplete by company
     */
    @Query("SELECT pt FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "AND pt.company = :company " +
           "AND LOWER(pt.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY pt.createdAt DESC " +
           "LIMIT 10")
    List<PurchasingTicket> findBySearchForAutoCompleteAndCompany(@Param("search") String search, @Param("company") Company company);
    
    /**
     * Find all active purchasing tickets by company
     */
    @Query("SELECT pt FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "AND pt.company = :company " +
           "ORDER BY pt.createdAt DESC")
    List<PurchasingTicket> findAllActiveByCompany(@Param("company") Company company);
    
    /**
     * Count purchasing tickets by status and company
     */
    @Query("SELECT COUNT(pt) FROM PurchasingTicket pt WHERE pt.isActive = true " +
           "AND pt.company = :company " +
           "AND pt.status = :status")
    long countByStatusAndCompany(@Param("status") PurchasingTicketStatus status, @Param("company") Company company);
    
    // ===== LEGACY METHODS (for backward compatibility) =====
    
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