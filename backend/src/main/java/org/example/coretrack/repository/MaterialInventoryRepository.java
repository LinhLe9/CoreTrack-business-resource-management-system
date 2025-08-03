package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.SearchInventoryResponse;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.material.MaterialVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialInventoryRepository extends JpaRepository<MaterialInventory, Long>{
    Optional<MaterialInventory> findByMaterialVariant_Id(Long variantId);
    
    // Delete material inventory by material variant
    void deleteByMaterialVariant(MaterialVariant materialVariant);
    
    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        mv.id,
        mv.sku,
        mv.name,
        m.group.name,
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        mv.imageUrl
    )
    FROM MaterialVariant mv
    LEFT JOIN Material m ON mv.material.id = m.id
    JOIN MaterialInventory inv ON mv.id = inv.materialVariant.id
    WHERE (:search IS NULL OR 
           LOWER(mv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(mv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupMaterials IS NULL OR m.group.id IN :groupMaterials)
      AND (:inventoryStatus IS NULL OR inv.inventoryStatus IN :inventoryStatus)
      AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED
      AND m.isActive = true
    """)
    Page<SearchInventoryResponse> searchInventoryByCriteria(
        @Param("search") String search,
        @Param("groupMaterials") List<Long> groupMaterials,
        @Param("inventoryStatus") List<InventoryStatus> inventoryStatus,
        Pageable pageable
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse(
        mv.id,
        mv.sku,
        mv.name,
        inv.inventoryStatus,
        inv.currentStock,
        mv.imageUrl
    )
    FROM MaterialVariant mv
    LEFT JOIN Material m ON mv.material.id = m.id
    JOIN MaterialInventory inv ON mv.id = inv.materialVariant.id
    WHERE (:search IS NULL OR 
           LOWER(mv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(mv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED
      AND m.isActive = true
    """)
    List<AllSearchInventoryResponse> searchInventory(
        @Param("search") String search
    );

    @Query("""
    SELECT inv
    FROM MaterialInventory inv
    JOIN MaterialVariant mv ON inv.materialVariant.id = mv.id
    JOIN Material m ON mv.material.id = m.id
    WHERE (:search IS NULL OR 
           LOWER(mv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(mv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupMaterials IS NULL OR m.group.id IN :groupMaterials)
      AND (:status IS NULL OR inv.inventoryStatus IN :status)
      AND m.status <> org.example.coretrack.model.material.MaterialStatus.DELETED
      AND m.isActive = true
    ORDER BY 
        CASE WHEN :sortByOldest = true THEN inv.updatedAt END ASC,
        CASE WHEN :sortByOldest = false THEN inv.updatedAt END DESC
    """)
    Page<Object[]> searchAlarmInventoryWithUpdatedAt(
        @Param("search") String search,
        @Param("groupMaterials") List<Long> groupMaterials,
        @Param("status") List<InventoryStatus> status,
        @Param("sortByOldest") boolean sortByOldest,
        Pageable pageable
    );
}
