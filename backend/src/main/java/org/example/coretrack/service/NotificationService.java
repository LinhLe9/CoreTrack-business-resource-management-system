package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.notification.NotificationResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.notification.Notification;
import org.example.coretrack.model.notification.NotificationType;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    
    // Create notification for product inventory status change (single user)
    void createInventoryNotification(User user, ProductInventory productInventory, InventoryStatus oldStatus, InventoryStatus newStatus);
    
    // Create notification for product inventory status change (multiple users)
    void createInventoryNotification(List<User> users, ProductInventory productInventory, InventoryStatus oldStatus, InventoryStatus newStatus);
    
    // Create notification for material inventory status change (single user)
    void createMaterialInventoryNotification(User user, MaterialInventory materialInventory, InventoryStatus oldStatus, InventoryStatus newStatus);
    
    // Create notification for material inventory status change (multiple users)
    void createMaterialInventoryNotification(List<User> users, MaterialInventory materialInventory, InventoryStatus oldStatus, InventoryStatus newStatus);
    
    // Get notifications for user
    Page<NotificationResponse> getUserNotifications(User user, Pageable pageable);
    
    // Get unread notifications count
    long getUnreadCount(User user);
    
    // Mark notification as read
    void markAsRead(Long notificationId, User user);
    
    // Mark all notifications as read
    void markAllAsRead(User user);
    
    // Delete notification
    void deleteNotification(Long notificationId, User user);
    
    // Get notifications by type
    List<NotificationResponse> getNotificationsByType(User user, NotificationType type);
    
    // Create ticket notification (single user)
    void createTicketNotification(Notification notification);
    
    // Create ticket notification (multiple users)
    void createTicketNotification(Notification notification, List<User> users);
    
    // Create general notification for multiple users
    void createGeneralNotification(List<User> users, NotificationType type, String title, String message);
    
    // Create test notification
    void createTestNotification(User user);
} 