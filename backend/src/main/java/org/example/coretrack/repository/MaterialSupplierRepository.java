package org.example.coretrack.repository;

import org.example.coretrack.model.supplier.MaterialSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialSupplierRepository extends JpaRepository<MaterialSupplier, Long> {
    
}
