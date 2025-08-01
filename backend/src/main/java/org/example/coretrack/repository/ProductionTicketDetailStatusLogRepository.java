package org.example.coretrack.repository;

import java.util.List;

import org.example.coretrack.model.productionTicket.ProductionTicketDetailStatusLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionTicketDetailStatusLogRepository extends JpaRepository<ProductionTicketDetailStatusLog, Long> {
    
    List<ProductionTicketDetailStatusLog> findByProductionTicketDetailId(Long productionTicketDetailId);
    
    @Query("SELECT ptdsl FROM ProductionTicketDetailStatusLog ptdsl WHERE ptdsl.productionTicketDetail.id = :productionTicketDetailId ORDER BY ptdsl.updatedAt DESC")
    List<ProductionTicketDetailStatusLog> findByProductionTicketDetailIdOrderByUpdatedAtDesc(@Param("productionTicketDetailId") Long productionTicketDetailId);
    
    @Query("SELECT ptdsl FROM ProductionTicketDetailStatusLog ptdsl WHERE ptdsl.productionTicketDetail.id = :productionTicketDetailId")
    Page<ProductionTicketDetailStatusLog> findByProductionTicketDetailId(@Param("productionTicketDetailId") Long productionTicketDetailId, Pageable pageable);
}