package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.material.MaterialGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialGroupRepository extends JpaRepository<MaterialGroup, Long>{
    Optional<MaterialGroup> findByIdAndIsActiveTrue (Long id);
    Optional<MaterialGroup> findByNameAndIsActiveTrue(String name);

    List<MaterialGroup> findByIsActiveTrueAndNameIsNotNull();
}
