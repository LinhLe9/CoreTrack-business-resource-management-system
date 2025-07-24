package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.supplier.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{
    Optional<Supplier> findById(Long id);
    List<Supplier> findByNameContainingIgnoreCase(String name);
    Optional<Supplier> findByPhone(String phone);

    // search by name, description, email, address, phone, contact person
    @Query("SELECT s FROM Supplier s WHERE " +
            "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.country) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.website) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(CONCAT(s.address, ' ', s.city, ' ', s.country)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND s.isActive = true")
    List<Supplier> findBySearchKeyword(@Param("search") String search);

    // search and filter 
    @Query("SELECT s FROM Supplier s WHERE " +
            "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.country) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(s.website) LIKE LOWER(CONCAT('%', :search, '%')) OR "+
            "LOWER(CONCAT(s.address, ' ', s.city, ' ', s.country)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND s.isActive = true " +
            "AND (:groupCountries IS NULL OR s.country IN :groupCountries)")
    Page<Supplier> findByCriteria(
            @Param("search") String search,
            @Param("groupCountries") List<String> groupCountries,
            Pageable pageable);

    // return a list of countries that is stored in database
    @Query("SELECT DISTINCT s.country FROM Supplier s WHERE s.country IS NOT NULL")
    List<String> findDistinctCountries();

    // return a list of available suppliers
    @Query("SELECT s FROM Supplier s WHERE s.isActive = true")
    Page<Supplier> findAllActive(Pageable pageable);
}
