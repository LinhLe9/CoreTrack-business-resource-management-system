package org.example.coretrack.service;

import org.example.coretrack.dto.notification.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(String username, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(
            username,
            "/topic/notifications",
            notification
        );
    }

    /**
     * Send notification to all connected users
     */
    public void sendNotificationToAll(NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Send unread count update to specific user
     */
    public void sendUnreadCountToUser(String username, int unreadCount) {
        messagingTemplate.convertAndSendToUser(
            username,
            "/topic/unread-count",
            unreadCount
        );
    }
} 