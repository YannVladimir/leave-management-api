package com.africahr.leave.service.impl;

import com.africahr.leave.model.User;
import com.africahr.leave.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${email.from}")
    private String fromEmail;
    
    @Value("${email.reply-to}")
    private String replyToEmail;

    @Override
    public void sendLeaveRequestSubmittedEmail(User requester, User manager, String leaveType, String startDate, String endDate) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("requesterName", requester.getFullName());
        variables.put("managerName", manager.getFullName());
        variables.put("leaveType", leaveType);
        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        
        String subject = "New Leave Request from " + requester.getFullName();
        sendEmail(manager.getEmail(), subject, "leave-request-submitted.html", "leave-request-submitted.txt", variables);
    }

    @Override
    public void sendLeaveRequestApprovedEmail(User requester, String leaveType, String startDate, String endDate) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("requesterName", requester.getFullName());
        variables.put("leaveType", leaveType);
        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        
        String subject = "Your Leave Request has been Approved";
        sendEmail(requester.getEmail(), subject, "leave-request-approved.html", "leave-request-approved.txt", variables);
    }

    @Override
    public void sendLeaveRequestRejectedEmail(User requester, String leaveType, String startDate, String endDate, String rejectionReason) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("requesterName", requester.getFullName());
        variables.put("leaveType", leaveType);
        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        variables.put("rejectionReason", rejectionReason);
        
        String subject = "Your Leave Request has been Rejected";
        sendEmail(requester.getEmail(), subject, "leave-request-rejected.html", "leave-request-rejected.txt", variables);
    }

    @Override
    public void sendEmail(String to, String subject, String htmlContent, String textContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setReplyTo(replyToEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(textContent, htmlContent);
        
        try {
            mailSender.send(message);
            log.info("Email sent successfully to {} at {}", to, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw e;
        }
    }

    private void sendEmail(String to, String subject, String htmlTemplate, String textTemplate, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process(htmlTemplate, context);
            String textContent = templateEngine.process(textTemplate, context);
            
            sendEmail(to, subject, htmlContent, textContent);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
} 