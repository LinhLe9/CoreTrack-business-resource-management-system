package org.example.coretrack.model.productionTicket;

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
import jakarta.persistence.FetchType;

@Entity
@Table(name = "production_ticket")
public class ProductionTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    // status and date 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionTicketStatus status;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "productionTicket", cascade = CascadeType.ALL)
    private List<ProductionTicketDetail> ticketDetail = new ArrayList<>();

    @OneToMany(mappedBy = "productionTicket", cascade = CascadeType.ALL)
    private List<ProductionTicketStatusLog> statusLogs = new ArrayList<>();

    private LocalDateTime completed_date;
    
    // Multi-tenancy: Company relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    public ProductionTicket(){}
    
    public ProductionTicket(String name, ProductionTicketStatus status, boolean isActive,
            List<ProductionTicketDetail> ticketDetail, LocalDateTime completed_date, LocalDateTime createdAt,
            LocalDateTime updatedAt, User createdBy, User updatedBy, Company company) {
        
        this.name = name;
        this.status = status;
        this.isActive = isActive;
        this.ticketDetail = ticketDetail;
        this.completed_date = completed_date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductionTicketStatus getStatus() {
        return status;
    }

    public void setStatus(ProductionTicketStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCompleted_date() {
        return completed_date;
    }

    public void setCompleted_date(LocalDateTime completed_date) {
        this.completed_date = completed_date;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public List<ProductionTicketDetail> getTicketDetail() {
        return ticketDetail;
    }

    public void setTicketDetail(List<ProductionTicketDetail> ticketDetail) {
        this.ticketDetail = ticketDetail;
    }

    public List<ProductionTicketStatusLog> getStatusLogs() {
        return statusLogs;
    }

    public void setStatusLogs(List<ProductionTicketStatusLog> statusLogs) {
        this.statusLogs = statusLogs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
