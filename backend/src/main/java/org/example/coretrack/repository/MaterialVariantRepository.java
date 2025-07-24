package org.example.coretrack.repository;

import java.util.Optional;

import org.example.coretrack.model.material.MaterialVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialVariantRepository extends JpaRepository<MaterialVariant, Long> {
    Optional<MaterialVariant> findBySku (String sku);
}
