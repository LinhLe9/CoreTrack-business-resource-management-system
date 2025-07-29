package org.example.coretrack.repository;

import org.example.coretrack.model.material.MaterialStatusAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialStatusAuditLogRepository extends JpaRepository<MaterialStatusAuditLog, Long> {
    List<MaterialStatusAuditLog> findByMaterialIdOrderByChangedAtDesc(Long materialId);
} 