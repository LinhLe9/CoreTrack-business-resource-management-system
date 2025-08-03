package org.example.coretrack.controller;

import org.example.coretrack.dto.notification.NotificationResponse;
import org.example.coretrack.model.notification.NotificationType;
import org.example.coretrack.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test/websocket")
public class TestWebSocketController {

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @PostMapping("/send-notification")
    public ResponseEntity<String> sendTestNotification(@RequestParam String username) {
        try {
            NotificationResponse notification = new NotificationResponse();
            notification.setId(1L);
            notification.setType(NotificationType.INVENTORY_OUT_OF_STOCK);
            notification.setTitle("Test Notification");
            notification.setMessage("This is a test notification sent at " + LocalDateTime.now());
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            
            webSocketNotificationService.sendNotificationToUser(username, notification);
            return ResponseEntity.ok("Test notification sent to user: " + username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending notification: " + e.getMessage());
        }
    }

    @PostMapping("/send-unread-count")
    public ResponseEntity<String> sendTestUnreadCount(@RequestParam String username, @RequestParam int count) {
        try {
            webSocketNotificationService.sendUnreadCountToUser(username, count);
            return ResponseEntity.ok("Unread count " + count + " sent to user: " + username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending unread count: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("TestWebSocketController is working!");
    }
} 