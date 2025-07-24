package org.example.coretrack.repository;

import org.example.coretrack.model.product.BOMItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomItemRepository extends JpaRepository<BOMItem, Long>{  
}
