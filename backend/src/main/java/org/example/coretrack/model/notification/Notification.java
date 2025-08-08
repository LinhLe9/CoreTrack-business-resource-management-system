package org.example.coretrack.model.notification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.model.material.inventory.MaterialInventory;

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
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "notification", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<NotificationUser> notificationUsers = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "product_inventory_id")
    private ProductInventory productInventory;
    
    @ManyToOne
    @JoinColumn(name = "material_inventory_id")
    private MaterialInventory materialInventory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType type;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public Notification(ProductInventory productInventory, NotificationType type, String title, String message) {
        this.productInventory = productInventory;
        this.materialInventory = null;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
    
    public Notification(MaterialInventory materialInventory, NotificationType type, String title, String message) {
        this.productInventory = null;
        this.materialInventory = materialInventory;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
    
    public Notification(NotificationType type, String title, String message) {
        this.productInventory = null;
        this.materialInventory = null;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<NotificationUser> getNotificationUsers() {
        return notificationUsers;
    }

    public void setNotificationUsers(List<NotificationUser> notificationUsers) {
        this.notificationUsers = notificationUsers;
    }
    
    // Helper methods for managing users
    public void addUser(User user) {
        NotificationUser notificationUser = new NotificationUser(this, user);
        notificationUsers.add(notificationUser);
    }
    
    public void addUsers(List<User> users) {
        for (User user : users) {
            addUser(user);
        }
    }
    
    public void removeUser(User user) {
        notificationUsers.removeIf(nu -> nu.getUser().equals(user));
    }

    public ProductInventory getProductInventory() {
        return productInventory;
    }

    public void setProductInventory(ProductInventory productInventory) {
        this.productInventory = productInventory;
    }
    
    public MaterialInventory getMaterialInventory() {
        return materialInventory;
    }

    public void setMaterialInventory(MaterialInventory materialInventory) {
        this.materialInventory = materialInventory;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 