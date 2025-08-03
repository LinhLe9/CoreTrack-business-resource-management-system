package org.example.coretrack.dto.productionTicket;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProductVariantBomRequest {
    private String productVariantSku;
    private BigDecimal quantity;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedCompleteDate;
    private List<BomItemProductionTicketRequest> boms;

    public ProductVariantBomRequest() {}

    public ProductVariantBomRequest(String productVariantSku, BigDecimal quantity, 
                                   LocalDate expectedCompleteDate, List<BomItemProductionTicketRequest> boms) {
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

    public LocalDate getExpectedCompleteDate() {
        return expectedCompleteDate;
    }

    public void setExpectedCompleteDate(LocalDate expectedCompleteDate) {
        this.expectedCompleteDate = expectedCompleteDate;
    }

    public List<BomItemProductionTicketRequest> getBoms() {
        return boms;
    }

    public void setBoms(List<BomItemProductionTicketRequest> boms) {
        this.boms = boms;
    }
} 