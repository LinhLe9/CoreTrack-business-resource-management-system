package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.model.Sale.Order;
import org.example.coretrack.model.Sale.OrderStatus;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long>{
    Optional<Order> findBySku(String Sku);
    Optional<Order> findByIdAndIsActive(Long id, boolean isActive);

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
