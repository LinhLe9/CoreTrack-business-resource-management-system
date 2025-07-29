package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.product.ProductStatus;
import org.example.coretrack.model.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findBySku (String sku);
    
    List<ProductVariant> findByProductStatusAndProductIsActiveTrue(ProductStatus status);
    
    @Query("""
        SELECT pv FROM ProductVariant pv 
        JOIN pv.product p 
        WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) 
           OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')) 
           OR LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) 
           OR LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')))
           AND p.status = 'ACTIVE' 
           AND p.isActive = true
        """)
    List<ProductVariant> findByProductNameContainingIgnoreCaseOrProductSkuContainingIgnoreCaseOrSkuContainingIgnoreCaseOrNameContainingIgnoreCase(
        @Param("search") String search);
}
