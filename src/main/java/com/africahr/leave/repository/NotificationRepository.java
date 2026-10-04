package com.africahr.leave.repository;

import com.africahr.leave.model.entity.Notification;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
    List<Notification> findByLeaveRequest(LeaveRequest leaveRequest);
    List<Notification> findByRecipientAndRead(User recipient, boolean read);
    List<Notification> findByRecipientAndActive(User recipient, boolean active);
    long countByRecipientAndRead(User recipient, boolean read);
} 