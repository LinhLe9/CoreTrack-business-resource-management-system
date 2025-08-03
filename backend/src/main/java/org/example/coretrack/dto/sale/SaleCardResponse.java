package org.example.coretrack.dto.sale;

import java.time.LocalDateTime;

import org.example.coretrack.model.Sale.Order;

public class SaleCardResponse {
    private Long id;
    private String sku;
    private LocalDateTime createdAt;
    private String status;
    private int detailNumber;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    public SaleCardResponse(Long id, String sku, LocalDateTime createdAt, String status, int detailNumber,
            String customerName, String customerEmail, String customerPhone, String customerAddress) {
        this.id = id;
        this.sku = sku;
        this.createdAt = createdAt;
        this.status = status;
        this.detailNumber = detailNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
    }

    public SaleCardResponse(Order order){
        this.id = order.getId();
        this.sku = order.getSku();
        this.createdAt = order.getCreatedAt();
        this.status = order.getStatus().getDisplayName();
        this.detailNumber = order.getOrderDetail().size();
        this.customerName = order.getCustomerName();
        this.customerEmail = order.getCustomerEmail();
        this.customerPhone = order.getCustomerPhone();
        this.customerAddress = order.getCustomerAddress();
    }

    public SaleCardResponse(){}
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getDetailNumber() {
        return detailNumber;
    }
    public void setDetailNumber(int detailNumber) {
        this.detailNumber = detailNumber;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    public String getCustomerPhone() {
        return customerPhone;
    }
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    public String getCustomerAddress() {
        return customerAddress;
    }
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }


}