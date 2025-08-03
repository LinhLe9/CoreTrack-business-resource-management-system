package org.example.coretrack.model.purchasingTicket;

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
@Table(name = "purchasing_ticket_detail_status_log")
public class PurchasingTicketDetailStatusLog {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchasingTicketDetail_id", nullable = false)
    private PurchasingTicketDetail purchasingTicketDetail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchasingTicketDetailStatus new_status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchasingTicketDetailStatus old_status;

    private String note;

    // Audit Fields
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    public PurchasingTicketDetailStatusLog(Long id, PurchasingTicketDetail purchasingTicketDetail,
            PurchasingTicketDetailStatus new_status, PurchasingTicketDetailStatus old_status, String note,
            LocalDateTime updatedAt, User updatedBy) {
        this.id = id;
        this.purchasingTicketDetail = purchasingTicketDetail;
        this.new_status = new_status;
        this.old_status = old_status;
        this.note = note;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public PurchasingTicketDetailStatusLog(){}
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchasingTicketDetail getPurchasingTicketDetail() {
        return purchasingTicketDetail;
    }

    public void setPurchasingTicketDetail(PurchasingTicketDetail purchasingTicketDetail) {
        this.purchasingTicketDetail = purchasingTicketDetail;
    }

    public PurchasingTicketDetailStatus getNew_status() {
        return new_status;
    }

    public void setNew_status(PurchasingTicketDetailStatus new_status) {
        this.new_status = new_status;
    }

    public PurchasingTicketDetailStatus getOld_status() {
        return old_status;
    }

    public void setOld_status(PurchasingTicketDetailStatus old_status) {
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
