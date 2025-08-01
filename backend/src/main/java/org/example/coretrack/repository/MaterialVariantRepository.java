package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.material.MaterialVariant;
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
}
