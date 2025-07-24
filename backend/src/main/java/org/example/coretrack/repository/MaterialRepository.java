package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.material.MaterialStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialRepository extends JpaRepository<Material, Long>{
        Optional<Material> findById (Long id);
        Optional<Material> findBySku (String sku);

        // search by SKU, Name, ShortDescription
        @Query("SELECT m FROM Material m WHERE " +
                "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(m.shortDes) LIKE LOWER(CONCAT('%', :search, '%'))) "+
                "AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED " +
                "AND m.isActive = true")
        List<Material> findBySearchKeyword(@Param("search") String search);

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

        @Query("""
                SELECT m FROM Material m
                LEFT JOIN FETCH m.variants v
                LEFT JOIN FETCH v.materialInventory
                WHERE m.id = :id
                """)
        Optional<Material> findByIdWithVariantsAndInventory(@Param("id") Long id);
}
