package org.example.coretrack.dto.productionTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductVariantBomRequest {
    private String productVariantSku;
    private BigDecimal quantity;
    private LocalDateTime expectedCompleteDate;
    private List<BomItemProductionTicketRequest> boms;

    public ProductVariantBomRequest() {}

    public ProductVariantBomRequest(String productVariantSku, BigDecimal quantity, 
                                   LocalDateTime expectedCompleteDate, List<BomItemProductionTicketRequest> boms) {
        this.productVariantSku = productVariantSku;
        this.quantity = quantity;
        this.expectedCompleteDate = expectedCompleteDate;
        this.boms = boms;
    }

    public String getProductVariantSku() {
        return productVariantSku;
    }

    public void setProductVariantSku(String productVariantSku) {
        this.productVariantSku = productVariantSku;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getExpectedCompleteDate() {
        return expectedCompleteDate;
    }

    public void setExpectedCompleteDate(LocalDateTime expectedCompleteDate) {
        this.expectedCompleteDate = expectedCompleteDate;
    }

    public List<BomItemProductionTicketRequest> getBoms() {
        return boms;
    }

    public void setBoms(List<BomItemProductionTicketRequest> boms) {
        this.boms = boms;
    }
} 