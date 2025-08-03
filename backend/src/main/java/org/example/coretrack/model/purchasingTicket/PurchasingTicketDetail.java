package org.example.coretrack.model.purchasingTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.MaterialVariant;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Entity
@Table(name = "purchasing_ticket_detail")
public class PurchasingTicketDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materialVariant_id", nullable = false)
    private MaterialVariant materialVariant;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100000, message = "Quantity exceeds maximum allowed")
    private BigDecimal quantity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchasingTicket_id", nullable = false)
    private PurchasingTicket purchasingTicket; 

    @OneToMany(mappedBy = "purchasingTicketDetail", cascade = CascadeType.ALL)
    private List<PurchasingTicketDetailStatusLog> statusLogs = new ArrayList<>();

    // status and date 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchasingTicketDetailStatus status;

    @Column(nullable = false)
    private boolean isActive;

    private LocalDateTime expected_ready_date;
    private LocalDateTime ready_date;

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
    public PurchasingTicketDetail(Long id, MaterialVariant materialVariant,
            @Min(value = 1, message = "Quantity must be at least 1") @Max(value = 100000, message = "Quantity exceeds maximum allowed") BigDecimal quantity,
            PurchasingTicket purchasingTicket, List<PurchasingTicketDetailStatusLog> statusLogs,
            PurchasingTicketDetailStatus status, boolean isActive, LocalDateTime expected_ready_date,
            LocalDateTime ready_date, LocalDateTime createdAt, LocalDateTime updatedAt, User createdBy,
            User updatedBy) {
        this.id = id;
        this.materialVariant = materialVariant;
        this.quantity = quantity;
        this.purchasingTicket = purchasingTicket;
        this.statusLogs = statusLogs;
        this.status = status;
        this.isActive = isActive;
        this.expected_ready_date = expected_ready_date;
        this.ready_date = ready_date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public PurchasingTicketDetail(){}

    //getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaterialVariant getMaterialVariant() {
        return materialVariant;
    }

    public void setMaterialVariant(MaterialVariant materialVariant) {
        this.materialVariant = materialVariant;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public PurchasingTicket getPurchasingTicket() {
        return purchasingTicket;
    }

    public void setPurchasingTicket(PurchasingTicket purchasingTicket) {
        this.purchasingTicket = purchasingTicket;
    }

    public List<PurchasingTicketDetailStatusLog> getStatusLogs() {
        return statusLogs;
    }

    public void setStatusLogs(List<PurchasingTicketDetailStatusLog> statusLogs) {
        this.statusLogs = statusLogs;
    }

    public PurchasingTicketDetailStatus getStatus() {
        return status;
    }

    public void setStatus(PurchasingTicketDetailStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getExpected_ready_date() {
        return expected_ready_date;
    }

    public void setExpected_ready_date(LocalDateTime expected_ready_date) {
        this.expected_ready_date = expected_ready_date;
    }

    public LocalDateTime getReady_date() {
        return ready_date;
    }

    public void setReady_date(LocalDateTime ready_date) {
        this.ready_date = ready_date;
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
    
    
}
