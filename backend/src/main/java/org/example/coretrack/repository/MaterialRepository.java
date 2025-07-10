package org.example.coretrack.repository;

import java.util.Optional;

import org.example.coretrack.model.material.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long>{
    Optional<Material> findById (Long id);
}
