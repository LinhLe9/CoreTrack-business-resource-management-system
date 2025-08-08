package org.example.coretrack.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.model.Sale.Order;
import org.example.coretrack.model.Sale.OrderStatus;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long>{
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find order by ID and company
     */
    Optional<Order> findByIdAndCompany(Long id, Company company);
    
    /**
     * Find order by SKU and company
     */
    Optional<Order> findBySkuAndCompany(String sku, Company company);
    
    /**
     * Find order by ID, active status and company
     */
    Optional<Order> findByIdAndIsActiveAndCompany(Long id, boolean isActive, Company company);
    
    /**
     * Find orders by search criteria and company
     */
    @Query("""
    SELECT DISTINCT o
    FROM Order o
    WHERE o.company = :company
      AND (:search IS NULL OR 
           LOWER(o.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR
           CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR
           LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerPhone) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerAddress) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:ticketStatus IS NULL OR o.status IN :ticketStatus)
      AND o.isActive = true
    """)
    Page<Order> findAllActiveByCriteriaAndCompany(
        @Param("search") String search,
        @Param("ticketStatus") List<OrderStatus> ticketStatus,
        @Param("company") Company company,    
        Pageable pageable);

    /**
     * Find orders for autocomplete by company
     */
    @Query("""
    SELECT o
    FROM Order o
    WHERE o.company = :company
      AND (:search IS NULL OR 
           LOWER(o.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR
           CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR
           LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerPhone) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerAddress) LIKE LOWER(CONCAT('%', :search, '%')))
      AND o.isActive = true
    """)
    List<Order> findAllBySearchAndCompany(
        @Param("search") String search,
        @Param("company") Company company
    );
    
    // ===== LEGACY METHODS (for backward compatibility) =====
    
    Optional<Order> findBySku(String Sku);
    Optional<Order> findByIdAndIsActive(Long id, boolean isActive);
    
    List<Order> findByCreatedByAndCreatedAtAfter(User createdBy, LocalDateTime createdAt);
    
    // Method for BusinessAnalyticsService
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
    SELECT DISTINCT o
    FROM Order o
    WHERE (:search IS NULL OR 
           LOWER(o.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR
           CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR
           LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerPhone) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerAddress) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:ticketStatus IS NULL OR o.status IN :ticketStatus)
      AND o.isActive = true
    """)
    Page<Order> findAllActiveByCriteria(
        @Param("search") String search,
        @Param("ticketStatus") List<OrderStatus> ticketStatus,    
        Pageable pageable);

    @Query("""
    SELECT o
    FROM Order o
    WHERE (:search IS NULL OR 
           LOWER(o.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR
           CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR
           LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerPhone) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(o.customerAddress) LIKE LOWER(CONCAT('%', :search, '%')))
      AND o.isActive = true
    """)
    List<Order> findAllBySearch(
        @Param("search") String search
    );
}
