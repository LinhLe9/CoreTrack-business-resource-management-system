package org.example.coretrack.repository;

import java.util.List;

import org.example.coretrack.model.productionTicket.ProductionTicketStatusLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionTicketStatusLogRepository extends JpaRepository<ProductionTicketStatusLog, Long> {
    
    List<ProductionTicketStatusLog> findByProductionTicketId(Long productionTicketId);
    
    @Query("SELECT ptsl FROM ProductionTicketStatusLog ptsl WHERE ptsl.productionTicket.id = :productionTicketId ORDER BY ptsl.updatedAt DESC")
    List<ProductionTicketStatusLog> findByProductionTicketIdOrderByUpdatedAtDesc(@Param("productionTicketId") Long productionTicketId);
    
    @Query("SELECT ptsl FROM ProductionTicketStatusLog ptsl WHERE ptsl.productionTicket.id = :productionTicketId")
    Page<ProductionTicketStatusLog> findByProductionTicketId(@Param("productionTicketId") Long productionTicketId, Pageable pageable);
} 