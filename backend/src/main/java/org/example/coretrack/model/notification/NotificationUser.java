package org.example.coretrack.model.notification;

import java.time.LocalDateTime;

import org.example.coretrack.model.auth.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_users")
public class NotificationUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public NotificationUser(Notification notification, User user) {
        this.notification = notification;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    public NotificationUser() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Notification getNotification() {
        return notification;
    }
    
    public void setNotification(Notification notification) {
        this.notification = notification;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
