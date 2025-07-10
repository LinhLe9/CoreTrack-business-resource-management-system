package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.product.productGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductGroupRepository extends JpaRepository<productGroup, Long>{
    Optional<productGroup> findById(Long id);
    Optional<productGroup> findByName(String name);

    @Query("SELECT DISTINCT p.name FROM productGroup p WHERE p.name IS NOT NULL")
    List<String> findDistinctGroupNames();
}
