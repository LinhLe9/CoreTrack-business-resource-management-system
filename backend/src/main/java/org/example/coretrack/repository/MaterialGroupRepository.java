package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.material.MaterialGroup;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialGroupRepository extends JpaRepository<MaterialGroup, Long>{
    Optional<MaterialGroup> findByIdAndIsActiveTrue (Long id);
    Optional<MaterialGroup> findByNameAndIsActiveTrue(String name);

    List<MaterialGroup> findByIsActiveTrueAndNameIsNotNull();
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find material group by name and company
     */
    Optional<MaterialGroup> findByNameAndCompany(String name, Company company);
    
    /**
     * Find material group by name, company and active status
     */
    Optional<MaterialGroup> findByNameAndCompanyAndIsActiveTrue(String name, Company company);
    
    /**
     * Find all material groups by company
     */
    List<MaterialGroup> findByCompany(Company company);
    
    /**
     * Find all active material groups by company
     */
    List<MaterialGroup> findByCompanyAndIsActiveTrue(Company company);
    
    /**
     * Find material group by ID and company
     */
    Optional<MaterialGroup> findByIdAndCompany(Long id, Company company);
    
    /**
     * Find material group by ID, company and active status
     */
    Optional<MaterialGroup> findByIdAndCompanyAndIsActiveTrue(Long id, Company company);
}
