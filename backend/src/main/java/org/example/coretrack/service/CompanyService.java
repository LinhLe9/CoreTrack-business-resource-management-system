package org.example.coretrack.service;

import org.example.coretrack.model.auth.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyService {
    
    /**
     * Create a new company
     */
    Company createCompany(String name, String code, String description);
    
    /**
     * Create a new company with auto-generated code
     */
    Company createCompany(String name, String description);
    
    /**
     * Find company by ID
     */
    Optional<Company> findById(Long id);
    
    /**
     * Find company by code
     */
    Optional<Company> findByCode(String code);
    
    /**
     * Find company by name
     */
    Optional<Company> findByName(String name);
    
    /**
     * Get all companies
     */
    List<Company> getAllCompanies();
    
    /**
     * Get all active companies
     */
    List<Company> getActiveCompanies();
    
    /**
     * Update company
     */
    Company updateCompany(Long id, String name, String description);
    
    /**
     * Deactivate company
     */
    void deactivateCompany(Long id);
    
    /**
     * Activate company
     */
    void activateCompany(Long id);
    
    /**
     * Generate a unique company code based on company name
     */
    String generateCompanyCode(String companyName);
    
    /**
     * Check if company code exists
     */
    boolean existsByCode(String code);
    
    /**
     * Check if company name exists
     */
    boolean existsByName(String name);
}
