package org.example.coretrack.model.purchasingTicket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;

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
@Table(name = "purchasing_ticket")
public class PurchasingTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    // status and date 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchasingTicketStatus status;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "purchasingTicket", cascade = CascadeType.ALL)
    private List<PurchasingTicketDetail> ticketDetail = new ArrayList<>();

    @OneToMany(mappedBy = "purchasingTicket", cascade = CascadeType.ALL)
    private List<PurchasingTicketStatusLog> statusLogs = new ArrayList<>();

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

    public PurchasingTicket(Long id, String name, PurchasingTicketStatus status, boolean isActive,
            List<PurchasingTicketDetail> ticketDetail, LocalDateTime completed_date, LocalDateTime createdAt,
            LocalDateTime updatedAt, User createdBy, User updatedBy) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.isActive = isActive;
        this.ticketDetail = ticketDetail;
        this.completed_date = completed_date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
    
    public PurchasingTicket(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PurchasingTicketStatus getStatus() {
        return status;
    }

    public void setStatus(PurchasingTicketStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<PurchasingTicketDetail> getTicketDetail() {
        return ticketDetail;
    }

    public void setTicketDetail(List<PurchasingTicketDetail> ticketDetail) {
        this.ticketDetail = ticketDetail;
    }

    public List<PurchasingTicketStatusLog> getStatusLogs() {
        return statusLogs;
    }

    public void setStatusLogs(List<PurchasingTicketStatusLog> statusLogs) {
        this.statusLogs = statusLogs;
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
    
}
