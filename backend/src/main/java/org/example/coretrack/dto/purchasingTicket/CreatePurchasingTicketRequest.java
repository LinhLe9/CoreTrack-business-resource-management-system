package org.example.coretrack.dto.purchasingTicket;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CreatePurchasingTicketRequest {
    private String materialVariantSku;
    private BigDecimal quantity;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedReadyDate;
    
    public CreatePurchasingTicketRequest(String materialVariantSku, BigDecimal quantity, LocalDate expectedReadyDate) {
        this.materialVariantSku = materialVariantSku;
        this.quantity = quantity;
        this.expectedReadyDate = expectedReadyDate;
    }

    public CreatePurchasingTicketRequest(){}

    public String getMaterialVariantSku() {
        return materialVariantSku;
    }

    public void setMaterialVariantSku(String materialVariantSku) {
        this.materialVariantSku = materialVariantSku;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpectedCompleteDate() {
        return expectedReadyDate;
    }

    public void setExpectedCompleteDate(LocalDate expectedReadyDate) {
        this.expectedReadyDate = expectedReadyDate;
    }
}
