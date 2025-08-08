package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

// import org.example.coretrack.model.product.BOMItem;
import org.example.coretrack.model.product.Product;
import org.example.coretrack.model.product.ProductStatus;
import org.example.coretrack.model.auth.Company;
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

        // ===== COMPANY-BASED QUERIES =====
        
        /**
         * Find product by ID and company
         */
        Optional<Product> findByIdAndCompany(Long id, Company company);
        
        /**
         * Find product by SKU and company
         */
        Optional<Product> findBySkuAndCompany(String sku, Company company);
        
        /**
         * Find all products by company
         */
        List<Product> findByCompany(Company company);
        
        /**
         * Find all active products by company
         */
        List<Product> findByCompanyAndIsActiveTrue(Company company);
        
        /**
         * Find products by name containing and company
         */
        List<Product> findByNameContainingIgnoreCaseAndCompany(String name, Company company);

        // search by SKU, Name, ShortDescription
        @Query("SELECT m FROM Product m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "m.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND " +
                "m.isActive = true")
        List<Product> findBySearchKeyword(@Param("search") String search);
        
        // search by SKU, Name, ShortDescription with company filter
        @Query("SELECT m FROM Product m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "m.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND " +
                "m.isActive = true AND m.company = :company")
        List<Product> findBySearchKeywordAndCompany(@Param("search") String search, @Param("company") Company company);
        
        // Search and filter with company
        @Query("SELECT m FROM Product m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "(:groupProducts IS NULL OR m.group.id IN :groupProducts) AND " +
                "(:statuses IS NULL OR m.status IN :statuses) AND " +
                "m.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND " +
                "m.isActive = true AND m.company = :company")
        Page<Product> findByCriteriaAndCompany(    
                @Param("search") String search,
                @Param("groupProducts") List<Long> groupProducts,
                @Param("statuses") List<ProductStatus> statuses,
                @Param("company") Company company,
                Pageable pageable);

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
        
        @Query("SELECT m FROM Product m WHERE " +
                "m.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND " +
                "m.isActive = true AND m.company = :company")
        Page<Product> findAllActiveByCompany(@Param("company") Company company, Pageable pageable);

        @Query("""
                SELECT p FROM Product p
                LEFT JOIN FETCH p.variants v
                LEFT JOIN FETCH v.productInventory
                WHERE p.id = :id
                """)
        Optional<Product> findByIdWithVariantsAndInventory(@Param("id") Long id);
        
        @Query("""
                SELECT p FROM Product p
                LEFT JOIN FETCH p.variants v
                LEFT JOIN FETCH v.productInventory
                WHERE p.id = :id AND p.company = :company
                """)
        Optional<Product> findByIdWithVariantsAndInventoryAndCompany(@Param("id") Long id, @Param("company") Company company);
}