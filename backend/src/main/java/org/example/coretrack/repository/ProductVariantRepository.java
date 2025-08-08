package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.product.ProductStatus;
import org.example.coretrack.model.product.ProductVariant;
import org.example.coretrack.model.auth.Company;
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
    
    // Search by keyword with company filter
    @Query("""
        SELECT pv FROM ProductVariant pv 
        JOIN pv.product p 
        WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) 
           OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')) 
           OR LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) 
           OR LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')))
           AND p.status = 'ACTIVE' 
           AND p.isActive = true
           AND p.company = :company
        """)
    List<ProductVariant> findBySearchKeywordAndCompany(@Param("search") String search, @Param("company") Company company);
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find product variant by SKU and company
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE pv.sku = :sku AND p.company = :company")
    Optional<ProductVariant> findBySkuAndCompany(@Param("sku") String sku, @Param("company") Company company);
    
    /**
     * Find product variant by SKU, company and active status
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE pv.sku = :sku AND p.company = :company AND pv.isActive = true")
    Optional<ProductVariant> findBySkuAndCompanyAndIsActiveTrue(@Param("sku") String sku, @Param("company") Company company);
    
    /**
     * Find all product variants by company
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE p.company = :company")
    List<ProductVariant> findByCompany(@Param("company") Company company);
    
    /**
     * Find all active product variants by company
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE p.company = :company AND pv.isActive = true")
    List<ProductVariant> findByCompanyAndIsActiveTrue(@Param("company") Company company);
    
    /**
     * Find product variant by ID and company
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE pv.id = :id AND p.company = :company")
    Optional<ProductVariant> findByIdAndCompany(@Param("id") Long id, @Param("company") Company company);
    
    /**
     * Find product variant by ID, company and active status
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE pv.id = :id AND p.company = :company AND pv.isActive = true")
    Optional<ProductVariant> findByIdAndCompanyAndIsActiveTrue(@Param("id") Long id, @Param("company") Company company);
    
    /**
     * Find product variants by product and company
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE pv.product = :product AND p.company = :company")
    List<ProductVariant> findByProductAndCompany(@Param("product") org.example.coretrack.model.product.Product product, @Param("company") Company company);
    
    /**
     * Find active product variants by product and company
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p WHERE pv.product = :product AND p.company = :company AND pv.isActive = true")
    List<ProductVariant> findByProductAndCompanyAndIsActiveTrue(@Param("product") org.example.coretrack.model.product.Product product, @Param("company") Company company);
}
