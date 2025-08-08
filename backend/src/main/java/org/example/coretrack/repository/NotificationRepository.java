package org.example.coretrack.repository;

import java.util.List;

import org.example.coretrack.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by product inventory
    List<Notification> findByProductInventoryOrderByCreatedAtDesc(org.example.coretrack.model.product.inventory.ProductInventory productInventory);
    
    // Find notifications by material inventory
    List<Notification> findByMaterialInventoryOrderByCreatedAtDesc(org.example.coretrack.model.material.inventory.MaterialInventory materialInventory);
    
    // Find notifications by type
    List<Notification> findByTypeOrderByCreatedAtDesc(org.example.coretrack.model.notification.NotificationType type);
} 