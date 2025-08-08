package org.example.coretrack.service;

import org.example.coretrack.model.auth.Company;
import org.example.coretrack.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyServiceImpl implements CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Override
    public Company createCompany(String name, String code, String description) {
        if (companyRepository.existsByCode(code)) {
            throw new RuntimeException("Company code already exists: " + code);
        }
        
        if (companyRepository.existsByName(name)) {
            throw new RuntimeException("Company name already exists: " + name);
        }
        
        Company company = new Company(name, code, description);
        return companyRepository.save(company);
    }
    
    @Override
    public Company createCompany(String name, String description) {
        String code = generateCompanyCode(name);
        return createCompany(name, code, description);
    }
    
    @Override
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }
    
    @Override
    public Optional<Company> findByCode(String code) {
        return companyRepository.findByCode(code);
    }
    
    @Override
    public Optional<Company> findByName(String name) {
        return companyRepository.findByName(name);
    }
    
    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
    
    @Override
    public List<Company> getActiveCompanies() {
        return companyRepository.findAll().stream()
                .filter(Company::isActive)
                .toList();
    }
    
    @Override
    public Company updateCompany(Long id, String name, String description) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        
        // Check if new name conflicts with other companies
        if (!company.getName().equals(name) && companyRepository.existsByName(name)) {
            throw new RuntimeException("Company name already exists: " + name);
        }
        
        company.setName(name);
        company.setDescription(description);
        return companyRepository.save(company);
    }
    
    @Override
    public void deactivateCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        
        company.setActive(false);
        companyRepository.save(company);
    }
    
    @Override
    public void activateCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        
        company.setActive(true);
        companyRepository.save(company);
    }
    
    @Override
    public String generateCompanyCode(String companyName) {
        // Generate a unique code based on company name
        String baseCode = companyName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String code = baseCode.length() > 8 ? baseCode.substring(0, 8) : baseCode;
        
        // Add random suffix if code already exists
        String finalCode = code;
        int suffix = 1;
        while (companyRepository.existsByCode(finalCode)) {
            finalCode = code + "_" + suffix;
            suffix++;
        }
        
        return finalCode;
    }
    
    @Override
    public boolean existsByCode(String code) {
        return companyRepository.existsByCode(code);
    }
    
    @Override
    public boolean existsByName(String name) {
        return companyRepository.existsByName(name);
    }
}
