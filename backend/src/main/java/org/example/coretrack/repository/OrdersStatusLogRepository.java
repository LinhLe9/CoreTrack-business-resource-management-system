package org.example.coretrack.repository;

import org.example.coretrack.model.Sale.OrderStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersStatusLogRepository extends JpaRepository<OrderStatusLog, Long>{
    
}
