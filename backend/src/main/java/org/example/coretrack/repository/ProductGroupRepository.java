package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.product.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long>{
    Optional<ProductGroup> findByIdAndIsActiveTrue(Long id);
    
    Optional<ProductGroup> findByNameAndIsActiveTrue(String name);

    List<ProductGroup> findByIsActiveTrueAndNameIsNotNull();
}
