package org.example.coretrack.model.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String sku;

    private BigDecimal total;
    private BigDecimal promotion;
    private BigDecimal netTotal;

    private LocalDateTime expected_complete_date;
    private LocalDateTime completed_date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetail = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderStatusLog> statusLogs = new ArrayList<>();

    // Customer info embedded directly
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Order(Long id, String sku, BigDecimal total, BigDecimal promotion, BigDecimal netTotal,
            LocalDateTime expected_complete_date, LocalDateTime completed_date, OrderStatus status, boolean isActive,
            List<OrderDetail> orderDetail, List<OrderStatusLog> statusLogs, LocalDateTime createdAt,
            LocalDateTime updatedAt, User createdBy, User updatedBy, Company company) {
        this.id = id;
        this.sku = sku;
        this.total = total;
        this.promotion = promotion;
        this.netTotal = netTotal;
        this.expected_complete_date = expected_complete_date;
        this.completed_date = completed_date;
        this.status = status;
        this.isActive = isActive;
        this.orderDetail = orderDetail;
        this.statusLogs = statusLogs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.company = company;
    }

    public Order(){}

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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<OrderDetail> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OrderDetail> orderDetail) {
        this.orderDetail = orderDetail;
    }

    public List<OrderStatusLog> getStatusLogs() {
        return statusLogs;
    }

    public void setStatusLogs(List<OrderStatusLog> statusLogs) {
        this.statusLogs = statusLogs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
