package org.example.coretrack.repository;

import java.util.Optional;

import org.example.coretrack.model.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findBySku (String sku);
}
