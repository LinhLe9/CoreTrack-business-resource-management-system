package org.example.coretrack.repository;

import org.example.coretrack.model.auth.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCode(String code);
    Optional<Company> findByName(String name);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
