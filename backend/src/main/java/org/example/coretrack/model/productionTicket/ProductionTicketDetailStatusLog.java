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
@Table(name = "production_ticket_detail_status_log")
public class ProductionTicketDetailStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productionTicketDetail_id", nullable = false)
    private ProductionTicketDetail productionTicketDetail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionTicketDetailStatus new_status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionTicketDetailStatus old_status;

    private String note;

    // Audit Fields
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    public ProductionTicketDetailStatusLog(Long id, ProductionTicketDetail productionTicketDetail,
            ProductionTicketDetailStatus new_status, ProductionTicketDetailStatus old_status, String note,
            LocalDateTime updatedAt, User updatedBy) {
        this.id = id;
        this.productionTicketDetail = productionTicketDetail;
        this.new_status = new_status;
        this.old_status = old_status;
        this.note = note;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public ProductionTicketDetailStatusLog(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductionTicketDetail getProductionTicketDetail() {
        return productionTicketDetail;
    }

    public void setProductionTicketDetail(ProductionTicketDetail productionTicketDetail) {
        this.productionTicketDetail = productionTicketDetail;
    }

    public ProductionTicketDetailStatus getNew_status() {
        return new_status;
    }

    public void setNew_status(ProductionTicketDetailStatus new_status) {
        this.new_status = new_status;
    }

    public ProductionTicketDetailStatus getOld_status() {
        return old_status;
    }

    public void setOld_status(ProductionTicketDetailStatus old_status) {
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
