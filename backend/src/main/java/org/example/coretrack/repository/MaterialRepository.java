package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.material.MaterialStatus;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialRepository extends JpaRepository<Material, Long>{
        Optional<Material> findById (Long id);
        Optional<Material> findBySku (String sku);

        // ===== COMPANY-BASED QUERIES =====
        
        /**
         * Find material by ID and company
         */
        Optional<Material> findByIdAndCompany(Long id, Company company);
        
        /**
         * Find material by SKU and company
         */
        Optional<Material> findBySkuAndCompany(String sku, Company company);
        
        /**
         * Find all materials by company
         */
        List<Material> findByCompany(Company company);
        
        /**
         * Find all active materials by company
         */
        List<Material> findByCompanyAndIsActiveTrue(Company company);

        // search by SKU, Name, ShortDescription
        @Query("SELECT m FROM Material m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.shortDes) LIKE LOWER(CONCAT('%', :search, '%'))) "+
                "AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED " +
                "AND m.isActive = true")
        List<Material> findBySearchKeyword(@Param("search") String search);
        
        // search by SKU, Name, ShortDescription with company filter
        @Query("SELECT m FROM Material m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.shortDes) LIKE LOWER(CONCAT('%', :search, '%'))) "+
                "AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED " +
                "AND m.isActive = true AND m.company = :company")
        List<Material> findBySearchKeywordAndCompany(@Param("search") String search, @Param("company") Company company);

        // search and filter with company
        @Query("SELECT m FROM Material m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.shortDes) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "(:groupMaterials IS NULL OR m.group.id IN :groupMaterials) AND " +
                "(:statuses IS NULL OR m.status IN :statuses) " +
                "AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED " +
                "AND m.isActive = true AND m.company = :company")
        Page<Material> findByCriteriaAndCompany(    
                @Param("search") String search,
                @Param("groupMaterials") List<Long> groupMaterials,
                @Param("statuses") List<MaterialStatus> statuses,
                @Param("company") Company company,
                Pageable pageable);

        // search and filter
        @Query("SELECT m FROM Material m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.shortDes) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "(:groupMaterials IS NULL OR m.group.id IN :groupMaterials) AND " +
                "(:statuses IS NULL OR m.status IN :statuses) " +
                "AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED " +
                "AND m.isActive = true")
        Page<Material> findByCriteria(    
                @Param("search") String search,
                @Param("groupMaterials") List<Long> groupMaterials,
                @Param("statuses") List<MaterialStatus> statuses,
                Pageable pageable);
        
        // query all
        @Query("SELECT m FROM Material m WHERE " +
                "m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED " +
                "AND m.isActive = true")
        Page<Material> findAllActive(Pageable pageable); 
        
        @Query("SELECT m FROM Material m WHERE " +
                "m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED " +
                "AND m.isActive = true AND m.company = :company")
        Page<Material> findAllActiveByCompany(@Param("company") Company company, Pageable pageable);

        @Query("""
                SELECT m FROM Material m
                LEFT JOIN FETCH m.variants v
                LEFT JOIN FETCH v.materialInventory
                WHERE m.id = :id
                """)
        Optional<Material> findByIdWithVariantsAndInventory(@Param("id") Long id);
        
        @Query("""
                SELECT m FROM Material m
                LEFT JOIN FETCH m.variants v
                LEFT JOIN FETCH v.materialInventory
                WHERE m.id = :id AND m.company = :company
                """)
        Optional<Material> findByIdWithVariantsAndInventoryAndCompany(@Param("id") Long id, @Param("company") Company company);
}
