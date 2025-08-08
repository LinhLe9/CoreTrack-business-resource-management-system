package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.material.MaterialVariant;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialVariantRepository extends JpaRepository<MaterialVariant, Long> {
    Optional<MaterialVariant> findBySku (String sku);
    List<MaterialVariant> findByMaterial(Material material);
    
    // Get all active material variants
    List<MaterialVariant> findByIsActiveTrue();
    
    @Query("SELECT mv FROM MaterialVariant mv " +
           "JOIN mv.material m " +
           "LEFT JOIN m.group g " +
           "WHERE mv.isActive = true " +
           "AND (LOWER(mv.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(mv.sku) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR (g IS NOT NULL AND LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))))")
    List<MaterialVariant> findBySearchKeyword(@Param("search") String search);
    
    // Search by keyword with company filter
    @Query("SELECT mv FROM MaterialVariant mv " +
           "JOIN mv.material m " +
           "LEFT JOIN m.group g " +
           "WHERE mv.isActive = true " +
           "AND m.company = :company " +
           "AND (LOWER(mv.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(mv.sku) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR (g IS NOT NULL AND LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))))")
    List<MaterialVariant> findBySearchKeywordAndCompany(@Param("search") String search, @Param("company") Company company);
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find material variant by SKU and company
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE mv.sku = :sku AND m.company = :company")
    Optional<MaterialVariant> findBySkuAndCompany(@Param("sku") String sku, @Param("company") Company company);
    
    /**
     * Find material variant by SKU, company and active status
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE mv.sku = :sku AND m.company = :company AND mv.isActive = true")
    Optional<MaterialVariant> findBySkuAndCompanyAndIsActiveTrue(@Param("sku") String sku, @Param("company") Company company);
    
    /**
     * Find all material variants by company
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE m.company = :company")
    List<MaterialVariant> findByCompany(@Param("company") Company company);
    
    /**
     * Find all active material variants by company
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE m.company = :company AND mv.isActive = true")
    List<MaterialVariant> findByCompanyAndIsActiveTrue(@Param("company") Company company);
    
    /**
     * Find material variant by ID and company
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE mv.id = :id AND m.company = :company")
    Optional<MaterialVariant> findByIdAndCompany(@Param("id") Long id, @Param("company") Company company);
    
    /**
     * Find material variant by ID, company and active status
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE mv.id = :id AND m.company = :company AND mv.isActive = true")
    Optional<MaterialVariant> findByIdAndCompanyAndIsActiveTrue(@Param("id") Long id, @Param("company") Company company);
    
    /**
     * Find material variants by material and company
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE mv.material = :material AND m.company = :company")
    List<MaterialVariant> findByMaterialAndCompany(@Param("material") Material material, @Param("company") Company company);
    
    /**
     * Find active material variants by material and company
     */
    @Query("SELECT mv FROM MaterialVariant mv JOIN mv.material m WHERE mv.material = :material AND m.company = :company AND mv.isActive = true")
    List<MaterialVariant> findByMaterialAndCompanyAndIsActiveTrue(@Param("material") Material material, @Param("company") Company company);
}
