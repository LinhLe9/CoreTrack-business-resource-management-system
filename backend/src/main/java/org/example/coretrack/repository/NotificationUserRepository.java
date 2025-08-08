package org.example.coretrack.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.notification.Notification;
import org.example.coretrack.model.notification.NotificationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
    
    // Find notification users by user
    Page<NotificationUser> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Find unread notification users by user
    List<NotificationUser> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    // Count unread notification users by user
    long countByUserAndIsReadFalse(User user);
    
    // Find notification users by notification
    List<NotificationUser> findByNotification(Notification notification);
    
    // Find notification user by notification and user
    NotificationUser findByNotificationAndUser(Notification notification, User user);
    
    // Find notification users by user and notification type
    List<NotificationUser> findByUserAndNotification_TypeOrderByCreatedAtDesc(User user, org.example.coretrack.model.notification.NotificationType type);
    
    // Mark notification user as read
    @Modifying
    @Query("UPDATE NotificationUser nu SET nu.isRead = true, nu.readAt = :readAt WHERE nu.user = :user AND nu.isRead = false")
    void markAllAsRead(@Param("user") User user, @Param("readAt") LocalDateTime readAt);
    
    // Mark specific notification user as read
    @Modifying
    @Query("UPDATE NotificationUser nu SET nu.isRead = true, nu.readAt = :readAt WHERE nu.id = :id AND nu.user = :user")
    void markAsRead(@Param("id") Long id, @Param("user") User user, @Param("readAt") LocalDateTime readAt);
    
    // Delete notification users by notification
    void deleteByNotification(Notification notification);
}
