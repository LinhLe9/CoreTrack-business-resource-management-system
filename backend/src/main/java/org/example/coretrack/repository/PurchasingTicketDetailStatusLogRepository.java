package org.example.coretrack.repository;

import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetailStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasingTicketDetailStatusLogRepository extends JpaRepository<PurchasingTicketDetailStatusLog, Long> {
    
} 