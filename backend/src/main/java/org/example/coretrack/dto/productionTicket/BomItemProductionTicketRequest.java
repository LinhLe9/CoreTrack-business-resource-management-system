package org.example.coretrack.dto.productionTicket;

import java.math.BigDecimal;

public class BomItemProductionTicketRequest {
    private String materialVariantSku;

    private BigDecimal actualQuantity;
    
    private BigDecimal plannedQuantity;

    public BomItemProductionTicketRequest(String materialVariantSku, BigDecimal actualQuantity,
            BigDecimal plannedQuantity) {
        this.materialVariantSku = materialVariantSku;
        this.actualQuantity = actualQuantity;
        this.plannedQuantity = plannedQuantity;
    }
    
    public BomItemProductionTicketRequest(){}

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
