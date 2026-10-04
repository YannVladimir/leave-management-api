package com.africahr.leave.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.africahr.leave.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id")
    private LeaveRequest leaveRequest;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "notification_type", nullable = false)
    private String notificationType;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    // Custom getters and setters for Lombok compatibility
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public LeaveRequest getLeaveRequest() { return leaveRequest; }
    public void setLeaveRequest(LeaveRequest leaveRequest) { this.leaveRequest = leaveRequest; }
} 