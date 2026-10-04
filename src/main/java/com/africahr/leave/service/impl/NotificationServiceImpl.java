package com.africahr.leave.service.impl;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.Notification;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.repository.NotificationRepository;
import com.africahr.leave.repository.LeaveRequestRepository;
import com.africahr.leave.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                 LeaveRequestRepository leaveRequestRepository) {
        this.notificationRepository = notificationRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notification.setActive(true);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByRecipient(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUser(User user) {
        return notificationRepository.findByRecipientAndRead(user, false);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notification notification = getNotificationById(id);
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = getUnreadNotificationsByUser(user);
        LocalDateTime now = LocalDateTime.now();
        
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(now);
        });
        
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = getNotificationById(id);
        notification.setActive(false);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Notification createLeaveRequestNotification(Long leaveRequestId, String notificationType) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));

        String title = "";
        String message = "";

        switch (notificationType) {
            case "SUBMITTED" -> {
                title = "New Leave Request Submitted";
                message = String.format("A new leave request has been submitted by %s from %s to %s",
                        leaveRequest.getUser().getEmail(),
                        leaveRequest.getStartDate(),
                        leaveRequest.getEndDate());
            }
            case "APPROVED" -> {
                title = "Leave Request Approved";
                message = String.format("Your leave request from %s to %s has been approved",
                        leaveRequest.getStartDate(),
                        leaveRequest.getEndDate());
            }
            case "REJECTED" -> {
                title = "Leave Request Rejected";
                message = String.format("Your leave request from %s to %s has been rejected. Reason: %s",
                        leaveRequest.getStartDate(),
                        leaveRequest.getEndDate(),
                        leaveRequest.getRejectionReason());
            }
            case "CANCELLED" -> {
                title = "Leave Request Cancelled";
                message = String.format("Your leave request from %s to %s has been cancelled",
                        leaveRequest.getStartDate(),
                        leaveRequest.getEndDate());
            }
            default -> throw new IllegalArgumentException("Invalid notification type");
        }

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .recipient(leaveRequest.getUser())
                .leaveRequest(leaveRequest)
                .notificationType(notificationType)
                .createdAt(LocalDateTime.now())
                .read(false)
                .active(true)
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByRecipientAndRead(user, false);
    }
} 