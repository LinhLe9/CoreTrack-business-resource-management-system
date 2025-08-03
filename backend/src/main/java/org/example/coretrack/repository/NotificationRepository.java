package org.example.coretrack.repository;

import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by user
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Find unread notifications by user
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    // Count unread notifications by user
    long countByUserAndIsReadFalse(User user);
    
    // Find notifications by user and type
    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, org.example.coretrack.model.notification.NotificationType type);
    
    // Find notifications by product inventory
    List<Notification> findByProductInventoryOrderByCreatedAtDesc(org.example.coretrack.model.product.inventory.ProductInventory productInventory);
    
    // Mark notifications as read
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user AND n.isRead = false")
    void markAllAsRead(@Param("user") User user);
    
    // Mark specific notification as read
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id AND n.user = :user")
    void markAsRead(@Param("id") Long id, @Param("user") User user);
} 