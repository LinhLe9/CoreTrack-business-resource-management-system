package org.example.coretrack.dto.sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleTicketResponse {
    private Long id;
    private String sku;

    private BigDecimal total;
    private BigDecimal promotion;
    private BigDecimal netTotal;

    private LocalDateTime expected_complete_date;
    private LocalDateTime completed_date;

    private String status;
    private boolean isActive;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    //Audit Fields
    private LocalDateTime createdAt;
    private String createdBy_Username;
    private String createdBy_role;

    private LocalDateTime updatedAt;
    private String updatedBy_Username;
    private String updatedBy_role;

    // log
    List<SaleOrderStatusLogResponse> logs;

    // Detail
    List<SaleDetailResponse> details;

    public SaleTicketResponse(Long id, String sku, BigDecimal total, BigDecimal promotion, BigDecimal netTotal,
            LocalDateTime expected_complete_date, LocalDateTime completed_date, String status, boolean isActive,
            String customerName, String customerEmail, String customerPhone, String customerAddress,
            LocalDateTime createdAt, String createdBy_Username, String createdBy_role, LocalDateTime updatedAt,
            String updatedBy_Username, String updatedBy_role, List<SaleOrderStatusLogResponse> logs,
            List<SaleDetailResponse> details) {
        this.id = id;
        this.sku = sku;
        this.total = total;
        this.promotion = promotion;
        this.netTotal = netTotal;
        this.expected_complete_date = expected_complete_date;
        this.completed_date = completed_date;
        this.status = status;
        this.isActive = isActive;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.createdAt = createdAt;
        this.createdBy_Username = createdBy_Username;
        this.createdBy_role = createdBy_role;
        this.updatedAt = updatedAt;
        this.updatedBy_Username = updatedBy_Username;
        this.updatedBy_role = updatedBy_role;
        this.logs = logs;
        this.details = details;
    }

    public SaleTicketResponse(){}

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

    public LocalDateTime getExpected_complete_date() {
        return expected_complete_date;
    }

    public void setExpected_complete_date(LocalDateTime expected_complete_date) {
        this.expected_complete_date = expected_complete_date;
    }

    public LocalDateTime getCompleted_date() {
        return completed_date;
    }

    public void setCompleted_date(LocalDateTime completed_date) {
        this.completed_date = completed_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy_Username() {
        return createdBy_Username;
    }

    public void setCreatedBy_Username(String createdBy_Username) {
        this.createdBy_Username = createdBy_Username;
    }

    public String getCreatedBy_role() {
        return createdBy_role;
    }

    public void setCreatedBy_role(String createdBy_role) {
        this.createdBy_role = createdBy_role;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy_Username() {
        return updatedBy_Username;
    }

    public void setUpdatedBy_Username(String updatedBy_Username) {
        this.updatedBy_Username = updatedBy_Username;
    }

    public String getUpdatedBy_role() {
        return updatedBy_role;
    }

    public void setUpdatedBy_role(String updatedBy_role) {
        this.updatedBy_role = updatedBy_role;
    }

    public List<SaleOrderStatusLogResponse> getLogs() {
        return logs;
    }

    public void setLogs(List<SaleOrderStatusLogResponse> logs) {
        this.logs = logs;
    }

    public List<SaleDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetailResponse> details) {
        this.details = details;
    }

}
