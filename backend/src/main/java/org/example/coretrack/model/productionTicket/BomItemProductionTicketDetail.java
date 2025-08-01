package org.example.coretrack.model.productionTicket;

import java.math.BigDecimal;

import org.example.coretrack.model.material.MaterialVariant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bomItem_production_ticket_detail")
public class BomItemProductionTicketDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productionTicketDetail_id", nullable = false)
    private ProductionTicketDetail productionTicketDetail;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materialVariant_id", nullable = false)
    private MaterialVariant materialVariant;

    private BigDecimal actualQuantity;
    private BigDecimal plannedQuantity;

    public BomItemProductionTicketDetail(ProductionTicketDetail productionTicketDetail,
            MaterialVariant materialVariant, BigDecimal actualQuantity, BigDecimal plannedQuantity) {
        this.productionTicketDetail = productionTicketDetail;
        this.materialVariant = materialVariant;
        this.actualQuantity = actualQuantity;
        this.plannedQuantity = plannedQuantity;
    }

    public BomItemProductionTicketDetail(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductionTicketDetail getProductionTicketDetail() {
        return productionTicketDetail;
    }

    public void setProductionTicketDetail(ProductionTicketDetail productionTicketDetail) {
        this.productionTicketDetail = productionTicketDetail;
    }

    public MaterialVariant getMaterialVariant() {
        return materialVariant;
    }

    public void setMaterialVariant(MaterialVariant materialVariant) {
        this.materialVariant = materialVariant;
    }

    public BigDecimal getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(BigDecimal actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public BigDecimal getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(BigDecimal plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }
}
