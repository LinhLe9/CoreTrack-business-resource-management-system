package org.example.coretrack.repository;

import org.example.coretrack.model.product.BOM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<BOM, Long>{
    
}
