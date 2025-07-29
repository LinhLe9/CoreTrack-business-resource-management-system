package org.example.coretrack.model.product;

import java.time.LocalDateTime;
import org.example.coretrack.model.auth.User;
import jakarta.persistence.*;

@Entity
@Table(name = "ProductStatusAuditLog")
public class ProductStatusAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus newStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private String reason;

    public ProductStatusAuditLog() {
    }

    public ProductStatusAuditLog(Product product, User user, ProductStatus previousStatus, ProductStatus newStatus, LocalDateTime changedAt, String reason) {
        this.product = product;
        this.user = user;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProductStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ProductStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ProductStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ProductStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 