package com.africahr.leave.service;

import com.africahr.leave.model.User;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendLeaveRequestSubmittedEmail(User requester, User manager, String leaveType, String startDate, String endDate);
    void sendLeaveRequestApprovedEmail(User requester, String leaveType, String startDate, String endDate);
    void sendLeaveRequestRejectedEmail(User requester, String leaveType, String startDate, String endDate, String rejectionReason);
    void sendEmail(String to, String subject, String htmlContent, String textContent) throws MessagingException;
} 