package org.example.coretrack.repository;

import org.example.coretrack.model.auth.Role;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    
    /**
     * Find all users with specific role created by a specific owner
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.createdBy.email = :ownerEmail AND u.enabled = true")
    List<User> findByRoleAndCreatedByEmail(@Param("role") String role, @Param("ownerEmail") String ownerEmail);
    
    /**
     * Find all warehouse staff users created by a specific owner
     */
    @Query("SELECT u FROM User u WHERE u.role = 'WAREHOUSE_STAFF' AND u.createdBy = :owner AND u.enabled = true")
    List<User> findWarehouseStaffUsersByOwner(@Param("owner") User owner);

    /**
     * Find all sale staff users created by a specific owner
     */
    @Query("SELECT u FROM User u WHERE u.role = 'SALE_STAFF' AND u.createdBy = :owner AND u.enabled = true")
    List<User> findSaleStaffUsersByOwner(@Param("owner") User owner);

    /**
     * Find all production staff users created by a specific owner
     */
    @Query("SELECT u FROM User u WHERE u.role = 'PRODUCTION_STAFF' AND u.createdBy = :owner AND u.enabled = true")
    List<User> findProductionStaffUsersByOwner(@Param("owner") User owner);

    /**
     * Find all warehouse staff emails created by a specific owner
     */
    @Query("SELECT u.email FROM User u WHERE u.role = 'WAREHOUSE_STAFF' AND u.createdBy.email = :ownerEmail AND u.enabled = true")
    List<String> findWarehouseStaffEmailsByOwner(@Param("ownerEmail") String ownerEmail);

    /**
     * Find all sale staff emails created by a specific owner
     */
    @Query("SELECT u.email FROM User u WHERE u.role = 'SALE_STAFF' AND u.createdBy.email = :ownerEmail AND u.enabled = true")
    List<String> findSaleStaffEmailsByOwner(@Param("ownerEmail") String ownerEmail);

    /**
     * Find all production staff emails created by a specific owner
     */
    @Query("SELECT u.email FROM User u WHERE u.role = 'PRODUCTION_STAFF' AND u.createdBy.email = :ownerEmail AND u.enabled = true")
    List<String> findProductionStaffEmailsByOwner(@Param("ownerEmail") String ownerEmail);
    
    /**
     * Find all users created by a specific owner OR the owner itself
     */
    @Query("SELECT u FROM User u WHERE u.createdBy = :createdBy OR u.id = :ownerId")
    List<User> findByCreatedByOrId(@Param("createdBy") User createdBy, @Param("ownerId") Long ownerId);
    
    /**
     * Find all users with specific role and enabled
     */
    List<User> findByRoleAndEnabledTrue(Role role);
    
    /**
     * Find all enabled users
     */
    List<User> findByEnabledTrue();
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find all users by company
     */
    List<User> findByCompany(Company company);
    
    /**
     * Find all enabled users by company
     */
    List<User> findByCompanyAndEnabledTrue(Company company);
    
    /**
     * Find user by email and company
     */
    Optional<User> findByEmailAndCompany(String email, Company company);
    
    /**
     * Find all users with specific role in a company
     */
    List<User> findByRoleAndCompany(Role role, Company company);
    
    /**
     * Find all users with specific role and enabled in a company
     */
    List<User> findByRoleAndCompanyAndEnabledTrue(Role role, Company company);
    
    /**
     * Find all users created by a specific user in the same company
     */
    @Query("SELECT u FROM User u WHERE u.createdBy = :createdBy AND u.company = :company")
    List<User> findByCreatedByAndCompany(@Param("createdBy") User createdBy, @Param("company") Company company);
    
    /**
     * Find all users in a company (created by owner OR the owner itself)
     */
    @Query("SELECT u FROM User u WHERE (u.createdBy = :owner OR u.id = :ownerId) AND u.company = :company")
    List<User> findByCreatedByOrIdAndCompany(@Param("owner") User owner, @Param("ownerId") Long ownerId, @Param("company") Company company);
}
