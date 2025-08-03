package org.example.coretrack.repository;

import java.util.Optional;

import org.example.coretrack.model.Sale.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
    Optional<OrderDetail> findBySku(String Sku);
    Optional<OrderDetail> findByIdAndIsActive(Long id, boolean isActive);
}