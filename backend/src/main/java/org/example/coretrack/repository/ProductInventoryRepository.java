package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.dto.product.inventory.AllSearchInventoryResponse;
import org.example.coretrack.dto.product.inventory.SearchInventoryResponse;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.model.auth.Company;
import org.example.coretrack.model.product.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long>{
    Optional<ProductInventory> findByProductVariant_Id(Long variantId);
    
    // ===== COMPANY-BASED QUERIES =====
    
    /**
     * Find product inventory by product variant ID and company
     */
    Optional<ProductInventory> findByProductVariant_IdAndProductVariant_Product_Company(Long variantId, Company company);
    
    /**
     * Find product inventory by product variant and company
     */
    Optional<ProductInventory> findByProductVariantAndProductVariant_Product_Company(ProductVariant productVariant, Company company);

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        COALESCE(pg.name, ''),
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl,
        inv.updatedAt
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    LEFT JOIN ProductGroup pg ON p.group.id = pg.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR pg.id IN :groupProducts)
      AND (:inventoryStatus IS NULL OR inv.inventoryStatus IN :inventoryStatus)
      AND (p IS NULL OR (p.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND p.isActive = true))
      AND p.company = :company
    """)
    Page<SearchInventoryResponse> searchInventoryByCriteria(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("inventoryStatus") List<InventoryStatus> inventoryStatus,
        @Param("company") Company company,
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
      AND (p IS NULL OR (p.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND p.isActive = true))
      AND p.company = :company
    """)
    List<AllSearchInventoryResponse> searchInventory(
        @Param("search") String search,
        @Param("company") Company company
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        COALESCE(pg.name, ''),
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl,
        inv.updatedAt
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    LEFT JOIN ProductGroup pg ON p.group.id = pg.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR pg.id IN :groupProducts)
      AND (:inventoryStatus IS NULL OR inv.inventoryStatus IN :inventoryStatus)
      AND (p IS NULL OR (p.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND p.isActive = true))
      AND p.company = :company
    """)
    Page<SearchInventoryResponse> searchAlarmInventoryByCriteria(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("inventoryStatus") List<InventoryStatus> inventoryStatus,
        @Param("company") Company company,
        Pageable pageable
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        COALESCE(pg.name, ''),
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl,
        inv.updatedAt
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    LEFT JOIN ProductGroup pg ON p.group.id = pg.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR pg.id IN :groupProducts)
      AND (:inventoryStatus IS NULL OR inv.inventoryStatus IN :inventoryStatus)
      AND (p IS NULL OR (p.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND p.isActive = true))
      AND p.company = :company
    """)
    Page<SearchInventoryResponse> searchAlarmInventoryWithLogSorting(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("inventoryStatus") List<InventoryStatus> inventoryStatus,
        @Param("company") Company company,
        Pageable pageable
    );

    @Query("""
    SELECT new org.example.coretrack.dto.product.inventory.SearchInventoryResponse(
        pv.id,
        pv.sku,
        pv.name,
        COALESCE(pg.name, ''),
        inv.inventoryStatus,
        inv.currentStock,
        inv.minAlertStock,
        inv.maxStockLevel,
        pv.imageUrl,
        inv.updatedAt
    )
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    LEFT JOIN ProductGroup pg ON p.group.id = pg.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR pg.id IN :groupProducts)
      AND (:status IS NULL OR inv.inventoryStatus IN :status)
      AND (p IS NULL OR (p.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND p.isActive = true))
      AND p.company = :company
    """)
    Page<SearchInventoryResponse> searchAlarmInventoryWithStatus(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("status") List<InventoryStatus> status,
        @Param("company") Company company,
        Pageable pageable
    );

    @Query("""
    SELECT pv.id, pv.sku, pv.name, COALESCE(pg.name, ''), inv.inventoryStatus, 
           inv.currentStock, inv.minAlertStock, inv.maxStockLevel, pv.imageUrl, inv.updatedAt
    FROM ProductVariant pv
    LEFT JOIN Product p ON pv.product.id = p.id
    LEFT JOIN ProductGroup pg ON p.group.id = pg.id
    JOIN ProductInventory inv ON pv.id = inv.productVariant.id
    WHERE (:search IS NULL OR 
           LOWER(pv.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(pv.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
           LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:groupProducts IS NULL OR pg.id IN :groupProducts)
      AND (:status IS NULL OR inv.inventoryStatus IN :status)
      AND (p IS NULL OR (p.status <> org.example.coretrack.model.product.ProductStatus.DELETED AND p.isActive = true))
      AND p.company = :company
    """)
    Page<Object[]> searchAlarmInventoryWithUpdatedAt(
        @Param("search") String search,
        @Param("groupProducts") List<Long> groupProducts,
        @Param("status") List<InventoryStatus> status,
        @Param("company") Company company,
        Pageable pageable
    );
}
