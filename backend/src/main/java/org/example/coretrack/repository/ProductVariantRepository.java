package org.example.coretrack.repository;

import java.util.Optional;

import org.example.coretrack.model.product.productVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<productVariant, Long> {
    Optional<productVariant> findBySku (String sku);
}
