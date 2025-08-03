package org.example.coretrack.dto.sale;

import java.math.BigDecimal;

public class SaleDetailResponse {
    
    private Long id;
    private String sku;
    private String productVariantSku;
    private String productVariantName;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal total;
    private BigDecimal currentStock;
    private BigDecimal allocatedStock;
    private BigDecimal futureStock;
    private BigDecimal availableStock;

    private String status;

    public SaleDetailResponse(Long id, String sku,  String productVariantSku, String productVariantName,
                           BigDecimal quantity, BigDecimal price, BigDecimal total, String status,
                           BigDecimal currentStock, BigDecimal allocatedStock, BigDecimal futureStock, BigDecimal availableStock) {
        this.id = id;
        this.sku = sku;
        this.productVariantSku = productVariantSku;
        this.productVariantName = productVariantName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.status = status;
        this.currentStock = currentStock;
        this.allocatedStock = allocatedStock;
        this.futureStock = futureStock;
        this.availableStock = availableStock;
    }

    public SaleDetailResponse(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductVariantSku() {
        return productVariantSku;
    }

    public void setProductVariantSku(String productVariantSku) {
        this.productVariantSku = productVariantSku;
    }

    public String getProductVariantName() {
        return productVariantName;
    }

    public void setProductVariantName(String productVariantName) {
        this.productVariantName = productVariantName;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getAllocatedStock() {
        return allocatedStock;
    }

    public void setAllocatedStock(BigDecimal allocatedStock) {
        this.allocatedStock = allocatedStock;
    }

    public BigDecimal getFutureStock() {
        return futureStock;
    }

    public void setFutureStock(BigDecimal futureStock) {
        this.futureStock = futureStock;
    }

    public BigDecimal getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(BigDecimal availableStock) {
        this.availableStock = availableStock;
    }

    
}