package org.example.coretrack.repository;

import java.util.List;
import org.example.coretrack.model.material.inventory.MaterialInventoryLog;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialInventoryLogRepository extends JpaRepository<MaterialInventoryLog, Long>{
    @Query("SELECT mil FROM MaterialInventoryLog mil " +
           "WHERE mil.materialInventory.materialVariant.id = :variantId " +
           "ORDER BY mil.transactionTimestamp DESC")
    List<MaterialInventoryLog> findByMaterialVariant_IdOrderByTransactionTimestampDesc(@Param("variantId") Long variantId);
    
    @Query("SELECT mil FROM MaterialInventoryLog mil " +
           "WHERE mil.materialInventory.id = :materialInventoryId " +
           "ORDER BY mil.transactionTimestamp DESC")
    List<MaterialInventoryLog> findByMaterialInventory_IdOrderByTransactionTimestampDesc(@Param("materialInventoryId") Long materialInventoryId);
    
    @Query(value = "SELECT * FROM materialInventoryLog mil " +
           "JOIN MaterialInventory mi ON mi.id = mil.material_inventory_id " +
           "WHERE mi.material_variant_id = :variantId " +
           "ORDER BY mil.transaction_timestamp DESC", nativeQuery = true)
    List<MaterialInventoryLog> findByMaterialVariantIdNative(@Param("variantId") Long variantId);
    
    @Query(value = "SELECT * FROM `materialInventoryLog` WHERE material_inventory_id = :materialInventoryId ORDER BY transaction_timestamp DESC", nativeQuery = true)
    List<MaterialInventoryLog> findByMaterialInventoryIdNative(@Param("materialInventoryId") Long materialInventoryId);
    
    // Delete material inventory log by material inventory
    void deleteByMaterialInventory(MaterialInventory materialInventory);
}
