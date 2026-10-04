package com.africahr.leave.service;

import com.africahr.leave.model.entity.Notification;
import com.africahr.leave.model.User;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    Notification getNotificationById(Long id);
    List<Notification> getNotificationsByUser(User user);
    List<Notification> getUnreadNotificationsByUser(User user);
    void markAsRead(Long id);
    void markAllAsRead(User user);
    void deleteNotification(Long id);
    Notification createLeaveRequestNotification(Long leaveRequestId, String notificationType);
    long getUnreadNotificationCount(User user);
} 