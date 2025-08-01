package org.example.coretrack.dto.productionTicket;

import java.math.BigDecimal;

public class BomItemProductionTicketResponse {
    private Long id;
    private String materialVariantSku;
    private BigDecimal actualQuantity;
    private BigDecimal plannedQuantity;

    public BomItemProductionTicketResponse(){}

    public BomItemProductionTicketResponse(Long id, String materialVariantSku, BigDecimal actualQuantity,
            BigDecimal plannedQuantity) {
        this.id = id;
        this.materialVariantSku = materialVariantSku;
        this.actualQuantity = actualQuantity;
        this.plannedQuantity = plannedQuantity;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMaterialVariantSku() {
        return materialVariantSku;
    }
    public void setMaterialVariantSku(String materialVariantSku) {
        this.materialVariantSku = materialVariantSku;
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
