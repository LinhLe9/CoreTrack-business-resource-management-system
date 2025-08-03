package org.example.coretrack.model.Sale;
import jakarta.persistence.Table;

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

@Entity
@Table(name = "order_status_log")
public class OrderStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus new_status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus old_status;

    private String note;

    // Audit Fields
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;


    public OrderStatusLog(Long id, Order order, OrderStatus new_status, OrderStatus old_status, String note,
            LocalDateTime updatedAt, User updatedBy) {
        this.id = id;
        this.order = order;
        this.new_status = new_status;
        this.old_status = old_status;
        this.note = note;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public OrderStatusLog(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getNew_status() {
        return new_status;
    }

    public void setNew_status(OrderStatus new_status) {
        this.new_status = new_status;
    }

    public OrderStatus getOld_status() {
        return old_status;
    }

    public void setOld_status(OrderStatus old_status) {
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
