package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

// import org.example.coretrack.model.product.BOMItem;
import org.example.coretrack.model.product.product;
import org.example.coretrack.model.product.productStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<product, Long> {
    Optional<product> findBySku(String sku);
    List<product> findByNameContainingIgnoreCase(String name);

    // search by SKU, Name, ShortDescription
    @Query("SELECT m FROM product m WHERE " +
           "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))"+
            "AND m.status <> 'DELETE'")
    List<product> findBySearchKeyword(@Param("search") String search);

    // Phương thức tìm kiếm và lọc kết hợp
    @Query("SELECT m FROM product m WHERE " +
           "(:search IS NULL OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:groupProducts IS NULL OR m.group IN :groupProducts) AND " +
           "(:statuses IS NULL OR m.status IN :statuses)")
    Page<product> findByCriteria(
            @Param("search") String search,
            @Param("groupProducts") List<String> groupProducts,
            @Param("statuses") List<productStatus> statuses,
            Pageable pageable);

    List<product> findAll(); 
}