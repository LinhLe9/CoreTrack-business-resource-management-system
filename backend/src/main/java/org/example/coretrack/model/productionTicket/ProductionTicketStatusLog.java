package org.example.coretrack.model.productionTicket;

import java.time.LocalDateTime;

import org.example.coretrack.model.auth.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "production_ticket_status_log")
public class ProductionTicketStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productionTicket_id", nullable = false)
    private ProductionTicket productionTicket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionTicketStatus new_status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionTicketStatus old_status;

    private String note;

    // Audit Fields
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    public ProductionTicketStatusLog(Long id, ProductionTicket productionTicket, ProductionTicketStatus new_status,
            ProductionTicketStatus old_status, String note, LocalDateTime updatedAt, User updatedBy) {
        this.id = id;
        this.productionTicket = productionTicket;
        this.new_status = new_status;
        this.old_status = old_status;
        this.note = note;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public ProductionTicketStatusLog(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductionTicket getProductionTicket() {
        return productionTicket;
    }

    public void setProductionTicket(ProductionTicket productionTicket) {
        this.productionTicket = productionTicket;
    }

    public ProductionTicketStatus getNew_status() {
        return new_status;
    }

    public void setNew_status(ProductionTicketStatus new_status) {
        this.new_status = new_status;
    }

    public ProductionTicketStatus getOld_status() {
        return old_status;
    }

    public void setOld_status(ProductionTicketStatus old_status) {
        this.old_status = old_status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    
}
