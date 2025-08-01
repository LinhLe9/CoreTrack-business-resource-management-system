package org.example.coretrack.repository;

import org.example.coretrack.model.material.Material;
import org.example.coretrack.model.supplier.MaterialSupplier;
import org.example.coretrack.model.supplier.MaterialSupplierId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialSupplierRepository extends JpaRepository<MaterialSupplier, MaterialSupplierId> {
    
    List<MaterialSupplier> findByMaterial(Material material);
    
}
