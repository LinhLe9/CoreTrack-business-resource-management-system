package org.example.coretrack.model.material;

import java.time.LocalDateTime;

import org.example.coretrack.model.auth.User;

import jakarta.persistence.*;

@Entity
@Table(name = "MaterialStatusAuditLog")
public class MaterialStatusAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialStatus newStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private String reason;

    public MaterialStatusAuditLog() {
    }

    public MaterialStatusAuditLog(Material material, User user, MaterialStatus previousStatus, 
                                MaterialStatus newStatus, String reason) {
        this.material = material;
        this.user = user;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = LocalDateTime.now();
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MaterialStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(MaterialStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public MaterialStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(MaterialStatus newStatus) {
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