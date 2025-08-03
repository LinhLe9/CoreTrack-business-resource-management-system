package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.SearchInventoryResponse;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long>{
    Optional<ProductInventory> findByProductVariant_Id(Long variantId);

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        p.group.name,
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR p.group.id IN :groupProducts)
      AND (:inventoryStatus IS NULL OR inv.inventoryStatus IN :inventoryStatus)
      AND p.status <> org.example.coretrack.model.product.ProductStatus.DELETED
      AND p.isActive = true
    """)
    Page<SearchInventoryResponse> searchInventoryByCriteria(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("inventoryStatus") List<InventoryStatus> inventoryStatus,
        Pageable pageable
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        inv.inventoryStatus,
        inv.currentStock,
        pv.imageUrl
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND p.status <> org.example.coretrack.model.product.ProductStatus.DELETED
      AND p.isActive = true
    """)
    List<AllSearchInventoryResponse> searchInventory(
        @Param("search") String search
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        p.group.name,
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR p.group.id IN :groupProducts)
      AND inv.inventoryStatus IN :inventoryStatus
      AND p.status <> org.example.coretrack.model.product.ProductStatus.DELETED
      AND p.isActive = true
    """)
    Page<SearchInventoryResponse> searchAlarmInventoryByCriteria(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("inventoryStatus") List<InventoryStatus> inventoryStatus,
        Pageable pageable
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        p.group.name,
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    LEFT JOIN ProductInventoryLog pil ON inv.id = pil.productInventory.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR p.group.id IN :groupProducts)
      AND inv.inventoryStatus IN :inventoryStatus
      AND p.status <> org.example.coretrack.model.product.ProductStatus.DELETED
      AND p.isActive = true
    GROUP BY pv.id, pv.sku, pv.name, p.group.name, inv.inventoryStatus, inv.currentStock, inv.minAlertStock, inv.maxStockLevel, pv.imageUrl
    """)
    Page<SearchInventoryResponse> searchAlarmInventoryWithLogSorting(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("inventoryStatus") List<InventoryStatus> inventoryStatus,
        Pageable pageable
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        p.group.name,
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR p.group.id IN :groupProducts)
      AND (:status IS NULL OR inv.inventoryStatus IN :status)
      AND p.status <> org.example.coretrack.model.product.ProductStatus.DELETED
      AND p.isActive = true
    """)
    Page<SearchInventoryResponse> searchAlarmInventoryWithStatus(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("status") List<InventoryStatus> status,
        Pageable pageable
    );

    @Query("""
    SELECT pv.id, pv.sku, pv.name, p.group.name, inv.inventoryStatus, 
           inv.currentStock, inv.minAlertStock, inv.maxStockLevel, pv.imageUrl, inv.updatedAt
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR p.group.id IN :groupProducts)
      AND (:status IS NULL OR inv.inventoryStatus IN :status)
      AND p.status <> org.example.coretrack.model.product.ProductStatus.DELETED
      AND p.isActive = true
    """)
    Page<Object[]> searchAlarmInventoryWithUpdatedAt(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("status") List<InventoryStatus> status,
        Pageable pageable
    );
}
