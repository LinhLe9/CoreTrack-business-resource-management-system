package org.example.coretrack.repository;

import org.example.coretrack.model.product.ProductStatusAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductStatusAuditLogRepository extends JpaRepository<ProductStatusAuditLog, Long> {
    List<ProductStatusAuditLog> findByProductIdOrderByChangedAtDesc(Long productId);
} 