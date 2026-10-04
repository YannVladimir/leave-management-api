package com.africahr.leave.service;

import com.africahr.leave.event.UserCreatedEvent;
import com.africahr.leave.model.User;
import com.africahr.leave.repository.UserRepository;
import com.africahr.leave.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .name("Test User")
            .providerId("microsoft123")
            .provider("microsoft")
            .active(true)
            .createdAt(java.time.LocalDateTime.now())
            .lastUpdated(java.time.LocalDateTime.now())
            .build();

        activeUser = User.builder()
            .id(2L)
            .email("active@example.com")
            .name("Active User")
            .providerId("microsoft456")
            .provider("microsoft")
            .active(true)
            .createdAt(java.time.LocalDateTime.now())
            .lastUpdated(java.time.LocalDateTime.now())
            .build();

        inactiveUser = User.builder()
            .id(3L)
            .email("inactive@example.com")
            .name("Inactive User")
            .providerId("microsoft789")
            .provider("microsoft")
            .active(false)
            .createdAt(java.time.LocalDateTime.now())
            .lastUpdated(java.time.LocalDateTime.now())
            .build();
    }

    @Test
    void saveUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        User savedUser = userService.save(testUser);
        assertNotNull(savedUser);
        assertEquals(testUser.getId(), savedUser.getId());
        verify(userRepository).save(testUser);
    }

    @Test
    void saveUser_Exception() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> userService.save(testUser));
    }

    @Test
    void findByMicrosoftId_UserExists() {
        when(userRepository.findByMicrosoftId("microsoft123")).thenReturn(Optional.of(testUser));
        Optional<User> foundUser = userService.findByMicrosoftId("microsoft123");
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
    }

    @Test
    void findByMicrosoftId_UserNotFound() {
        when(userRepository.findByMicrosoftId("nonexistent")).thenReturn(Optional.empty());
        Optional<User> foundUser = userService.findByMicrosoftId("nonexistent");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmail_UserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        Optional<User> foundUser = userService.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
    }

    @Test
    void findByEmail_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        Optional<User> foundUser = userService.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findAllActiveUsers() {
        List<User> activeUsers = Arrays.asList(activeUser);
        when(userRepository.findAllActiveUsers()).thenReturn(activeUsers);
        List<User> result = userService.findAllActiveUsers();
        assertEquals(1, result.size());
        assertEquals(activeUser.getId(), result.get(0).getId());
    }

    @Test
    void findById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Optional<User> foundUser = userService.findById(1L);
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
    }

    @Test
    void findById_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<User> foundUser = userService.findById(999L);
        assertFalse(foundUser.isPresent());
    }
} 