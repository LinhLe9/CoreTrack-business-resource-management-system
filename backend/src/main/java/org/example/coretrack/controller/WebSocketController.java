package org.example.coretrack.controller;

import org.example.coretrack.dto.notification.NotificationResponse;
import org.example.coretrack.service.NotificationService;
import org.example.coretrack.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/subscribe")
    @SendTo("/topic/notifications")
    public String subscribe(SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser().getName();
        System.out.println("User " + username + " subscribed to notifications");
        return "Subscribed to notifications";
    }

    @MessageMapping("/unread-count")
    @SendTo("/topic/unread-count")
    public Integer getUnreadCount(SimpMessageHeaderAccessor headerAccessor) {
        // This is a placeholder - in real implementation, you'd get the user from the session
        // For now, we'll return 0
        return 0;
    }
} 