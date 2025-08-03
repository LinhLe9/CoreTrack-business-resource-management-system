package org.example.coretrack.dto.sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Size;

public class SaleCreateRequest {
     @Size(max = 16, message = "SKU cannot exceed 16 characters") // Added size constraint
    private String sku;

    private BigDecimal total;
    private BigDecimal promotion;
    private BigDecimal netTotal;

    private LocalDate expected_complete_date;

    List<SaleCreateDetailRequest> details;

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    public SaleCreateRequest(@Size(max = 16, message = "SKU cannot exceed 16 characters") String sku, BigDecimal total,
            BigDecimal promotion, BigDecimal netTotal, LocalDate expected_complete_date,
            List<SaleCreateDetailRequest> details, String customerName, String customerEmail, String customerPhone,
            String customerAddress) {
        this.sku = sku;
        this.total = total;
        this.promotion = promotion;
        this.netTotal = netTotal;
        this.expected_complete_date = expected_complete_date;
        this.details = details;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public BigDecimal getPromotion() {
        return promotion;
    }
    public void setPromotion(BigDecimal promotion) {
        this.promotion = promotion;
    }
    public BigDecimal getNetTotal() {
        return netTotal;
    }
    public void setNetTotal(BigDecimal netTotal) {
        this.netTotal = netTotal;
    }
    public LocalDate getExpected_complete_date() {
        return expected_complete_date;
    }
    public void setExpected_complete_date(LocalDate expected_complete_date) {
        this.expected_complete_date = expected_complete_date;
    }
    public List<SaleCreateDetailRequest> getDetails() {
        return details;
    }
    public void setDetails(List<SaleCreateDetailRequest> details) {
        this.details = details;
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
