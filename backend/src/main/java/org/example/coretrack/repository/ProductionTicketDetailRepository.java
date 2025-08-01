package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.productionTicket.ProductionTicketDetail;
import org.example.coretrack.model.productionTicket.ProductionTicketDetailStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionTicketDetailRepository extends JpaRepository<ProductionTicketDetail, Long> {
    
    List<ProductionTicketDetail> findByStatus(ProductionTicketDetailStatus status);
    
    List<ProductionTicketDetail> findByIsActive(boolean isActive);
    
    List<ProductionTicketDetail> findByStatusAndIsActive(ProductionTicketDetailStatus status, boolean isActive);
    
    List<ProductionTicketDetail> findByProductionTicketId(Long productionTicketId);
    
    List<ProductionTicketDetail> findByProductionTicketIdAndIsActive(Long productionTicketId, boolean isActive);
    
    @Query("SELECT ptd FROM ProductionTicketDetail ptd WHERE ptd.isActive = true")
    Page<ProductionTicketDetail> findAllActive(Pageable pageable);
    
    @Query("SELECT ptd FROM ProductionTicketDetail ptd WHERE ptd.status = :status AND ptd.isActive = true")
    Page<ProductionTicketDetail> findByStatusAndActive(@Param("status") ProductionTicketDetailStatus status, Pageable pageable);
    
    Optional<ProductionTicketDetail> findByIdAndIsActive(Long id, boolean isActive);
} 