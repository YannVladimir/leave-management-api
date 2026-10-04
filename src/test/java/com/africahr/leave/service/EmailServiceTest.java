package com.africahr.leave.service;

import com.africahr.leave.model.User;
import com.africahr.leave.service.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private User requester;
    private User manager;

    @BeforeEach
    void setUp() {
        requester = User.builder()
                .email("requester@example.com")
                .fullName("John Doe")
                .build();

        manager = User.builder()
                .email("manager@example.com")
                .fullName("Jane Smith")
                .build();

        // Set required email properties
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@africahr.com");
        ReflectionTestUtils.setField(emailService, "replyToEmail", "support@africahr.com");

        // Common setup for all tests
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        lenient().when(templateEngine.process(anyString(), any(Context.class))).thenReturn("Email content");
    }

    @Test
    void sendLeaveRequestSubmittedEmail_Success() throws MessagingException {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendLeaveRequestSubmittedEmail(
                requester,
                manager,
                "Annual Leave",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(5).toString()
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendLeaveRequestApprovedEmail_Success() throws MessagingException {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendLeaveRequestApprovedEmail(
                requester,
                "Annual Leave",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(5).toString()
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendLeaveRequestRejectedEmail_Success() throws MessagingException {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendLeaveRequestRejectedEmail(
                requester,
                "Annual Leave",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(5).toString(),
                "Insufficient leave balance"
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_WithMessagingException_ThrowsException() throws MessagingException {
        doThrow(new RuntimeException("Failed to send email")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> 
            emailService.sendEmail(
                "test@example.com",
                "Test Subject",
                "HTML Content",
                "Text Content"
            )
        );

        verify(mailSender).send(any(MimeMessage.class));
    }
} 