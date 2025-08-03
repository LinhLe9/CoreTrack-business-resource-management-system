package org.example.coretrack.dto.sale;

import java.math.BigDecimal;

public class SaleCreateDetailRequest {
    private String productVariantSku;
    private BigDecimal quantity;
    public SaleCreateDetailRequest(String productVariantSku, BigDecimal quantity) {
        this.productVariantSku = productVariantSku;
        this.quantity = quantity;
    }

    public SaleCreateDetailRequest(){}
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
}
