package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.product.ProductGroup;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long>{
    Optional<ProductGroup> findByIdAndIsActiveTrue(Long id);
    
    Optional<ProductGroup> findByNameAndIsActiveTrue(String name);

    List<ProductGroup> findByIsActiveTrueAndNameIsNotNull();
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find product group by name and company
     */
    Optional<ProductGroup> findByNameAndCompany(String name, Company company);
    
    /**
     * Find product group by name, company and active status
     */
    Optional<ProductGroup> findByNameAndCompanyAndIsActiveTrue(String name, Company company);
    
    /**
     * Find all product groups by company
     */
    List<ProductGroup> findByCompany(Company company);
    
    /**
     * Find all active product groups by company
     */
    List<ProductGroup> findByCompanyAndIsActiveTrue(Company company);
    
    /**
     * Find product group by ID and company
     */
    Optional<ProductGroup> findByIdAndCompany(Long id, Company company);
    
    /**
     * Find product group by ID, company and active status
     */
    Optional<ProductGroup> findByIdAndCompanyAndIsActiveTrue(Long id, Company company);
}
