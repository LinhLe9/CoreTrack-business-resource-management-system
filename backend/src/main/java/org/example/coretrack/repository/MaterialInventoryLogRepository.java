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
    
    // Delete material inventory log by material inventory
    void deleteByMaterialInventory(MaterialInventory materialInventory);
}
