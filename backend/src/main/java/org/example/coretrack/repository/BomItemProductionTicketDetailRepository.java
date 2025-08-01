package org.example.coretrack.repository;

import java.util.List;
import java.util.Optional;

import org.example.coretrack.model.productionTicket.BomItemProductionTicketDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BomItemProductionTicketDetailRepository extends JpaRepository<BomItemProductionTicketDetail, Long> {
    
    List<BomItemProductionTicketDetail> findByProductionTicketDetailId(Long productionTicketDetailId);
    
    List<BomItemProductionTicketDetail> findByMaterialVariantId(Long materialVariantId);
    
    @Query("SELECT bptd FROM BomItemProductionTicketDetail bptd WHERE bptd.productionTicketDetail.id = :productionTicketDetailId")
    List<BomItemProductionTicketDetail> findByProductionTicketDetailIdQuery(@Param("productionTicketDetailId") Long productionTicketDetailId);
    
    @Query("SELECT bptd FROM BomItemProductionTicketDetail bptd WHERE bptd.materialVariant.id = :materialVariantId")
    List<BomItemProductionTicketDetail> findByMaterialVariantIdQuery(@Param("materialVariantId") Long materialVariantId);
    
    Optional<BomItemProductionTicketDetail> findByIdAndProductionTicketDetailId(Long id, Long productionTicketDetailId);
} 