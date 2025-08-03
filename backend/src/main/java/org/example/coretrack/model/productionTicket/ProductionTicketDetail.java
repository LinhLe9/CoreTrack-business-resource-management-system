package org.example.coretrack.model.productionTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.product.ProductVariant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "production_ticket_detail")
public class ProductionTicketDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productVariant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100000, message = "Quantity exceeds maximum allowed")
    private BigDecimal quantity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productionTicket_id", nullable = false)
    private ProductionTicket productionTicket; 

    @OneToMany(mappedBy = "productionTicketDetail", cascade = CascadeType.ALL)
    private List<BomItemProductionTicketDetail> bomItem = new ArrayList<>();

    @OneToMany(mappedBy = "productionTicketDetail", cascade = CascadeType.ALL)
    private List<ProductionTicketDetailStatusLog> statusLogs = new ArrayList<>();

    // status and date 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionTicketDetailStatus status;

    @Column(nullable = false)
    private boolean isActive;

    private LocalDateTime expected_complete_date;
    private LocalDateTime completed_date;

    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    // constructor
    public ProductionTicketDetail(){}

    public ProductionTicketDetail(ProductVariant productVariant,
            @Min(value = 1, message = "Quantity must be at least 1") @Max(value = 100000, message = "Quantity exceeds maximum allowed") BigDecimal quantity,
            ProductionTicket productionTicket, List<BomItemProductionTicketDetail> bomItem,
            ProductionTicketDetailStatus status, boolean isActive,
            LocalDateTime expected_complete_date, LocalDateTime completed_date, LocalDateTime createdAt,
            LocalDateTime updatedAt, User createdBy, User updatedBy) {
        this.productVariant = productVariant;
        this.quantity = quantity;
        this.productionTicket = productionTicket;
        this.bomItem = bomItem;
        this.status = status;
        this.isActive = isActive;
        this.expected_complete_date = expected_complete_date;
        this.completed_date = completed_date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
    
    // getter & setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ProductionTicket getProductionTicket() {
        return productionTicket;
    }

    public void setProductionTicket(ProductionTicket productionTicket) {
        this.productionTicket = productionTicket;
    }

    public ProductionTicketDetailStatus getStatus() {
        return status;
    }

    public void setStatus(ProductionTicketDetailStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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

    public List<BomItemProductionTicketDetail> getBomItem() {
        return bomItem;
    }

    public void setBomItem(List<BomItemProductionTicketDetail> bomItem) {
        this.bomItem = bomItem;
    }

    public List<ProductionTicketDetailStatusLog> getStatusLogs() {
        return statusLogs;
    }

    public void setStatusLogs(List<ProductionTicketDetailStatusLog> statusLogs) {
        this.statusLogs = statusLogs;
    }

    
}
