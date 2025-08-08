package org.example.coretrack.controller;

import java.util.List;

import org.example.coretrack.dto.notification.NotificationResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.notification.NotificationType;
import org.example.coretrack.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    

    
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(user, pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/{id}/mark-as-read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }
    
    // Alternative endpoint for frontend compatibility
    @PostMapping("/{id}/mark-read")
    public ResponseEntity<Void> markAsReadAlternative(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/mark-all-as-read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        notificationService.deleteNotification(id, user);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/by-type")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            Authentication authentication,
            @RequestParam NotificationType type) {
        
        User user = (User) authentication.getPrincipal();
        List<NotificationResponse> notifications = notificationService.getNotificationsByType(user, type);
        return ResponseEntity.ok(notifications);
    }
    
    // Test endpoint to create a notification
    @PostMapping("/test")
    public ResponseEntity<Void> createTestNotification(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        notificationService.createTestNotification(user);
        return ResponseEntity.ok().build();
    }
} 