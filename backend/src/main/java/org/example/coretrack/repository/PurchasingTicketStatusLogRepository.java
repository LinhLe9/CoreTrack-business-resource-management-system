package org.example.coretrack.repository;

import org.example.coretrack.model.purchasingTicket.PurchasingTicketStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasingTicketStatusLogRepository extends JpaRepository<PurchasingTicketStatusLog, Long> {
    
} 