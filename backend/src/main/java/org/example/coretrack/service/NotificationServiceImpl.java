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
import org.example.coretrack.service.WebSocketNotificationService;
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
    private WebSocketNotificationService webSocketNotificationService;
    
    @Override
    @Transactional
    public void createInventoryNotification(User user, ProductInventory productInventory, 
                                        InventoryStatus oldStatus, InventoryStatus newStatus) {
        
        // Only create notification if status changed to alarm status
        if (isAlarmStatus(newStatus) && !newStatus.equals(oldStatus)) {
            
            NotificationType notificationType = getNotificationType(newStatus);
            String title = getNotificationTitle(newStatus, productInventory);
            String message = getNotificationMessage(newStatus, productInventory);
            
            Notification notification = new Notification(user, productInventory, notificationType, title, message);
            notificationRepository.save(notification);
            
            // Send real-time notification via WebSocket
            NotificationResponse notificationResponse = convertToResponse(notification);
            webSocketNotificationService.sendNotificationToUser(user.getUsername(), notificationResponse);
            webSocketNotificationService.sendUnreadCountToUser(user.getUsername(), (int) getUnreadCount(user));
            
            System.out.println("Created notification: " + notificationType + " for product: " + 
                             productInventory.getProductVariant().getName());
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
            
            Notification notification = new Notification(user, materialInventory, notificationType, title, message);
            notificationRepository.save(notification);
            
            // Send real-time notification via WebSocket
            NotificationResponse notificationResponse = convertToResponse(notification);
            webSocketNotificationService.sendNotificationToUser(user.getUsername(), notificationResponse);
            webSocketNotificationService.sendUnreadCountToUser(user.getUsername(), (int) getUnreadCount(user));
            
            System.out.println("Created notification: " + notificationType + " for material: " + 
                             materialInventory.getMaterialVariant().getName());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(User user, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return notifications.map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        notificationRepository.markAsRead(notificationId, user);
    }
    
    @Override
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsRead(user);
    }
    
    @Override
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        // Check if notification belongs to user before deleting
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
            
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this notification");
        }
        
        notificationRepository.deleteById(notificationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByType(User user, NotificationType type) {
        List<Notification> notifications = notificationRepository.findByUserAndTypeOrderByCreatedAtDesc(user, type);
        return notifications.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
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
    
    @Override
    @Transactional
    public void createTicketNotification(Notification notification) {
        notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        NotificationResponse notificationResponse = convertToResponse(notification);
        webSocketNotificationService.sendNotificationToUser(notification.getUser().getUsername(), notificationResponse);
        webSocketNotificationService.sendUnreadCountToUser(notification.getUser().getUsername(), (int) getUnreadCount(notification.getUser()));
        
        System.out.println("Created ticket notification: " + notification.getType() + " for user: " + 
                         notification.getUser().getUsername());
    }
    
    private NotificationResponse convertToResponse(Notification notification) {
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
        
        return new NotificationResponse(
            notification.getId(),
            notification.getType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.isRead(),
            notification.getCreatedAt(),
            notification.getReadAt(),
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