package org.example.coretrack.repository;

import java.util.List;

import org.example.coretrack.model.product.inventory.ProductInventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductInventoryLogRepository extends JpaRepository<ProductInventoryLog, Long> {
    @Query("SELECT pil FROM ProductInventoryLog pil " +
           "JOIN pil.productInventory pi " +
           "WHERE pi.productVariant.id = :variantId " +
           "ORDER BY pil.transactionTimestamp DESC")
    List<ProductInventoryLog> findByProductVariant_IdOrderByTransactionTimestampDesc(@Param("variantId") Long variantId);
} 