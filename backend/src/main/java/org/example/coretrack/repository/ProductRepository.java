package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

// import org.example.coretrack.model.product.BOMItem;
import org.example.coretrack.model.product.Product;
import org.example.coretrack.model.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
        Optional<Product> findBySku(String sku);
        List<Product> findByNameContainingIgnoreCase(String name);

        // search by SKU, Name, ShortDescription
        @Query("SELECT m FROM Product m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "m.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND " +
                "m.isActive = true")
        List<Product> findBySearchKeyword(@Param("search") String search);
        
        // Search and filter
        @Query("SELECT m FROM Product m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "(:groupProducts IS NULL OR m.group.id IN :groupProducts) AND " +
                "(:statuses IS NULL OR m.status IN :statuses) AND " +
                "m.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND " +
                "m.isActive = true")
        Page<Product> findByCriteria(    
                @Param("search") String search,
                @Param("groupProducts") List<Long> groupProducts,
                @Param("statuses") List<ProductStatus> statuses,
                Pageable pageable);

        @Query("SELECT m FROM Product m WHERE " +
                "m.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND " +
                "m.isActive = true")
        Page<Product> findAllActive(Pageable pageable); 

        @Query("""
                SELECT p FROM Product p
                LEFT JOIN FETCH p.variants v
                LEFT JOIN FETCH v.productInventory
                WHERE p.id = :id
                """)
        Optional<Product> findByIdWithVariantsAndInventory(@Param("id") Long id);
}