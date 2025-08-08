package org.example.coretrack.repository;

import java.util.Optional;

import org.example.coretrack.model.Sale.OrderDetail;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
    Optional<OrderDetail> findBySku(String Sku);
    Optional<OrderDetail> findByIdAndIsActive(Long id, boolean isActive);
    
    /**
     * Find order detail by SKU and company (through order relationship)
     */
    @Query("SELECT od FROM OrderDetail od JOIN od.order o WHERE od.sku = :sku AND o.company = :company")
    Optional<OrderDetail> findBySkuAndCompany(@Param("sku") String sku, @Param("company") Company company);
}