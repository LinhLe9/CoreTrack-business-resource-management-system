package org.example.coretrack.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.coretrack.dto.notification.NotificationResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.example.coretrack.model.notification.Notification;
import org.example.coretrack.model.notification.NotificationType;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.repository.NotificationRepository;
import org.example.coretrack.repository.NotificationUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationUserRepository notificationUserRepository;
    

    
    @Override
    @Transactional
    public void createInventoryNotification(User user, ProductInventory productInventory, 
                                        InventoryStatus oldStatus, InventoryStatus newStatus) {
        
        // Only create notification if status changed to alarm status
        if (isAlarmStatus(newStatus) && !newStatus.equals(oldStatus)) {
            
            NotificationType notificationType = getNotificationType(newStatus);
            String title = getNotificationTitle(newStatus, productInventory);
            String message = getNotificationMessage(newStatus, productInventory);
            
            Notification notification = new Notification(productInventory, notificationType, title, message);
            notification.addUser(user);
            notificationRepository.save(notification);
            
            // Notification created successfully
            
            System.out.println("Created notification: " + notificationType + " for product: " + 
                             productInventory.getProductVariant().getName() + " for user: " + user.getUsername());
        }
    }

    @Override
    @Transactional
    public void createInventoryNotification(List<User> users, ProductInventory productInventory, 
                                        InventoryStatus oldStatus, InventoryStatus newStatus) {
        
        // Only create notification if status changed to alarm status
        if (isAlarmStatus(newStatus) && !newStatus.equals(oldStatus)) {
            
            NotificationType notificationType = getNotificationType(newStatus);
            String title = getNotificationTitle(newStatus, productInventory);
            String message = getNotificationMessage(newStatus, productInventory);
            
            Notification notification = new Notification(productInventory, notificationType, title, message);
            notification.addUsers(users);
            notificationRepository.save(notification);
            
            // Notifications created successfully for all users
            
            System.out.println("Created notification: " + notificationType + " for product: " + 
                             productInventory.getProductVariant().getName() + " for " + users.size() + " users");
        }
    }

    @Override
    @Transactional
    public void createMaterialInventoryNotification(User user, MaterialInventory materialInventory, 
                                                InventoryStatus oldStatus, InventoryStatus newStatus) {
        
        // Only create notification if status changed to alarm status
        if (isAlarmStatus(newStatus) && !newStatus.equals(oldStatus)) {
            
            NotificationType notificationType = getNotificationType(newStatus);
            String title = getMaterialNotificationTitle(newStatus, materialInventory);
            String message = getMaterialNotificationMessage(newStatus, materialInventory);
            
            Notification notification = new Notification(materialInventory, notificationType, title, message);
            notification.addUser(user);
            notificationRepository.save(notification);
            
            // Notification created successfully
            
            System.out.println("Created notification: " + notificationType + " for material: " + 
                             materialInventory.getMaterialVariant().getName() + " for user: " + user.getUsername());
        }
    }

    @Override
    @Transactional
    public void createMaterialInventoryNotification(List<User> users, MaterialInventory materialInventory, 
                                                InventoryStatus oldStatus, InventoryStatus newStatus) {
        
        // Only create notification if status changed to alarm status
        if (isAlarmStatus(newStatus) && !newStatus.equals(oldStatus)) {
            
            NotificationType notificationType = getNotificationType(newStatus);
            String title = getMaterialNotificationTitle(newStatus, materialInventory);
            String message = getMaterialNotificationMessage(newStatus, materialInventory);
            
            Notification notification = new Notification(materialInventory, notificationType, title, message);
            notification.addUsers(users);
            notificationRepository.save(notification);
            
            // Notifications created successfully for all users
            
            System.out.println("Created notification: " + notificationType + " for material: " + 
                             materialInventory.getMaterialVariant().getName() + " for " + users.size() + " users");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(User user, Pageable pageable) {
        Page<org.example.coretrack.model.notification.NotificationUser> notificationUsers = 
            notificationUserRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return notificationUsers.map(nu -> convertToResponse(nu.getNotification(), user));
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationUserRepository.countByUserAndIsReadFalse(user);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        System.out.println("=== Starting markAsRead ===");
        System.out.println("Notification ID: " + notificationId);
        System.out.println("User: " + user.getUsername());
        
        try {
            // Find the notification first
            Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
            
            // Find the NotificationUser for this specific user and notification
            org.example.coretrack.model.notification.NotificationUser notificationUser = 
                notificationUserRepository.findByNotificationAndUser(notification, user);
            
            if (notificationUser == null) {
                throw new RuntimeException("NotificationUser not found for notification ID: " + notificationId + " and user: " + user.getUsername());
            }
            
            // Mark the NotificationUser as read using its ID
            notificationUserRepository.markAsRead(notificationUser.getId(), user, java.time.LocalDateTime.now());
            System.out.println("Successfully marked notification as read");
        } catch (Exception e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    @Transactional
    public void markAllAsRead(User user) {
        System.out.println("=== Starting markAllAsRead ===");
        System.out.println("User: " + user.getUsername());
        
        try {
            notificationUserRepository.markAllAsRead(user, java.time.LocalDateTime.now());
            System.out.println("Successfully marked all notifications as read");
        } catch (Exception e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        // Find the notification first
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        
        // Find the NotificationUser for this specific user and notification
        org.example.coretrack.model.notification.NotificationUser notificationUser = 
            notificationUserRepository.findByNotificationAndUser(notification, user);
        
        if (notificationUser == null) {
            throw new RuntimeException("NotificationUser not found for notification ID: " + notificationId + " and user: " + user.getUsername());
        }
        
        // Check if notification belongs to user before deleting
        if (!notificationUser.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this notification");
        }
        
        notificationUserRepository.deleteById(notificationUser.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByType(User user, NotificationType type) {
        List<org.example.coretrack.model.notification.NotificationUser> notificationUsers = 
            notificationUserRepository.findByUserAndNotification_TypeOrderByCreatedAtDesc(user, type);
        return notificationUsers.stream()
            .map(nu -> convertToResponse(nu.getNotification(), user))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void createTicketNotification(Notification notification) {
        System.out.println("=== DEBUG: Creating ticket notification ===");
        System.out.println("Notification type: " + notification.getType());
        System.out.println("Title: " + notification.getTitle());
        System.out.println("Message: " + notification.getMessage());
        System.out.println("Users count: " + notification.getNotificationUsers().size());
        System.out.println("==========================================");
        
        notificationRepository.save(notification);
        
        // Notifications sent successfully to all users
        for (org.example.coretrack.model.notification.NotificationUser nu : notification.getNotificationUsers()) {
            User user = nu.getUser();
            NotificationResponse notificationResponse = convertToResponse(notification, user);
            // Notification sent successfully
        }
        
        System.out.println("Created ticket notification: " + notification.getType() + " for " + 
                         notification.getNotificationUsers().size() + " users");
    }
    
    @Override
    @Transactional
    public void createTicketNotification(Notification notification, List<User> users) {
        notification.addUsers(users);
        createTicketNotification(notification);
    }
    
    @Override
    @Transactional
    public void createGeneralNotification(List<User> users, NotificationType type, String title, String message) {
        Notification notification = new Notification(type, title, message);
        notification.addUsers(users);
        notificationRepository.save(notification);
        
        // Notifications sent successfully to all users
        
        System.out.println("Created general notification: " + type + " for " + users.size() + " users");
    }
    
    @Override
    @Transactional
    public void createTestNotification(User user) {
        Notification notification = new Notification(
            NotificationType.INVENTORY_LOW_STOCK, 
            "Test Notification", 
            "This is a test notification to verify the notification system is working properly."
        );
        notification.addUser(user);
        notificationRepository.save(notification);
        
        // Notification created successfully
        
        System.out.println("Created test notification for user: " + user.getUsername());
    }
    
    // Helper methods
    private boolean isAlarmStatus(InventoryStatus status) {
        return status == InventoryStatus.OUT_OF_STOCK || 
               status == InventoryStatus.LOW_STOCK || 
               status == InventoryStatus.OVER_STOCK;
    }
    
    private NotificationType getNotificationType(InventoryStatus status) {
        switch (status) {
            case OUT_OF_STOCK:
                return NotificationType.INVENTORY_OUT_OF_STOCK;
            case LOW_STOCK:
                return NotificationType.INVENTORY_LOW_STOCK;
            case OVER_STOCK:
                return NotificationType.INVENTORY_OVER_STOCK;
            default:
                return NotificationType.INVENTORY_STATUS_CHANGE;
        }
    }
    
    private String getNotificationTitle(InventoryStatus status, ProductInventory productInventory) {
        String productName = productInventory.getProductVariant().getName();
        switch (status) {
            case OUT_OF_STOCK:
                return "Out of Stock Alert";
            case LOW_STOCK:
                return "Low Stock Alert";
            case OVER_STOCK:
                return "Over Stock Alert";
            default:
                return "Inventory Status Change";
        }
    }
    
    private String getNotificationMessage(InventoryStatus status, ProductInventory productInventory) {
        String productName = productInventory.getProductVariant().getName();
        String sku = productInventory.getProductVariant().getSku();
        String currentStock = productInventory.getCurrentStock().toString();
        
        switch (status) {
            case OUT_OF_STOCK:
                return String.format("Product '%s' (SKU: %s) is now OUT OF STOCK. Current stock: %s", 
                                  productName, sku, currentStock);
            case LOW_STOCK:
                String minAlert = productInventory.getMinAlertStock() != null ? 
                    productInventory.getMinAlertStock().toString() : "Not set";
                return String.format("Product '%s' (SKU: %s) is LOW STOCK. Current: %s, Min Alert: %s", 
                                  productName, sku, currentStock, minAlert);
            case OVER_STOCK:
                String maxLevel = productInventory.getMaxStockLevel() != null ? 
                    productInventory.getMaxStockLevel().toString() : "Not set";
                return String.format("Product '%s' (SKU: %s) is OVER STOCK. Current: %s, Max Level: %s", 
                                  productName, sku, currentStock, maxLevel);
            default:
                return String.format("Product '%s' (SKU: %s) status changed to %s", 
                                  productName, sku, status.name());
        }
    }
    
    private String getMaterialNotificationTitle(InventoryStatus status, MaterialInventory materialInventory) {
        String materialName = materialInventory.getMaterialVariant().getName();
        switch (status) {
            case OUT_OF_STOCK:
                return "Material Out of Stock Alert";
            case LOW_STOCK:
                return "Material Low Stock Alert";
            case OVER_STOCK:
                return "Material Over Stock Alert";
            default:
                return "Material Inventory Status Change";
        }
    }
    
    private String getMaterialNotificationMessage(InventoryStatus status, MaterialInventory materialInventory) {
        String materialName = materialInventory.getMaterialVariant().getName();
        String sku = materialInventory.getMaterialVariant().getSku();
        String currentStock = materialInventory.getCurrentStock().toString();
        
        switch (status) {
            case OUT_OF_STOCK:
                return String.format("Material '%s' (SKU: %s) is now OUT OF STOCK. Current stock: %s", 
                                  materialName, sku, currentStock);
            case LOW_STOCK:
                String minAlert = materialInventory.getMinAlertStock() != null ? 
                    materialInventory.getMinAlertStock().toString() : "Not set";
                return String.format("Material '%s' (SKU: %s) is LOW STOCK. Current: %s, Min Alert: %s", 
                                  materialName, sku, currentStock, minAlert);
            case OVER_STOCK:
                String maxLevel = materialInventory.getMaxStockLevel() != null ? 
                    materialInventory.getMaxStockLevel().toString() : "Not set";
                return String.format("Material '%s' (SKU: %s) is OVER STOCK. Current: %s, Max Level: %s", 
                                  materialName, sku, currentStock, maxLevel);
            default:
                return String.format("Material '%s' (SKU: %s) status changed to %s", 
                                  materialName, sku, status.name());
        }
    }
    
    private NotificationResponse convertToResponse(Notification notification, User user) {
        ProductInventory productInventory = notification.getProductInventory();
        MaterialInventory materialInventory = notification.getMaterialInventory();
        
        String productName = productInventory != null ? productInventory.getProductVariant().getName() : null;
        String productSku = productInventory != null ? productInventory.getProductVariant().getSku() : null;
        String productImageUrl = productInventory != null ? productInventory.getProductVariant().getImageUrl() : null;
        Long productInventoryId = productInventory != null ? productInventory.getId() : null;
        
        String materialName = materialInventory != null ? materialInventory.getMaterialVariant().getName() : null;
        String materialSku = materialInventory != null ? materialInventory.getMaterialVariant().getSku() : null;
        String materialImageUrl = materialInventory != null ? materialInventory.getMaterialVariant().getImageUrl() : null;
        Long materialInventoryId = materialInventory != null ? materialInventory.getId() : null;
        
        // Find the NotificationUser for this specific user
        org.example.coretrack.model.notification.NotificationUser notificationUser = 
            notification.getNotificationUsers().stream()
                .filter(nu -> nu.getUser().equals(user))
                .findFirst()
                .orElse(null);
        
        boolean isRead = notificationUser != null ? notificationUser.isRead() : false;
        java.time.LocalDateTime readAt = notificationUser != null ? notificationUser.getReadAt() : null;
        
        return new NotificationResponse(
            notification.getId(),
            notification.getType(),
            notification.getTitle(),
            notification.getMessage(),
            isRead,
            notification.getCreatedAt(),
            readAt,
            productInventoryId,
            productName,
            productSku,
            productImageUrl,
            materialInventoryId,
            materialName,
            materialSku,
            materialImageUrl
        );
    }
} 