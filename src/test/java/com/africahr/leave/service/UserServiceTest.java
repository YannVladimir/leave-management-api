package com.africahr.leave.service;

import com.africahr.leave.event.UserCreatedEvent;
import com.africahr.leave.model.entity.User;
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
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setMicrosoftId("microsoft123");
        testUser.setActive(true);

        activeUser = new User();
        activeUser.setId(2L);
        activeUser.setEmail("active@example.com");
        activeUser.setActive(true);

        inactiveUser = new User();
        inactiveUser.setId(3L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setActive(false);
    }

    @Test
    void saveUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publishEvent(any(UserCreatedEvent.class));

        User savedUser = userService.save(testUser);

        assertNotNull(savedUser);
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getMicrosoftId(), savedUser.getMicrosoftId());
        verify(userRepository).save(testUser);
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void saveUser_Exception() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userService.save(testUser));
        verify(userRepository).save(testUser);
        verify(eventPublisher, never()).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void findByMicrosoftId_UserExists() {
        when(userRepository.findByMicrosoftId("microsoft123")).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.findByMicrosoftId("microsoft123");

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        verify(userRepository).findByMicrosoftId("microsoft123");
    }

    @Test
    void findByMicrosoftId_UserNotFound() {
        when(userRepository.findByMicrosoftId("nonexistent")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findByMicrosoftId("nonexistent");

        assertFalse(foundUser.isPresent());
        verify(userRepository).findByMicrosoftId("nonexistent");
    }

    @Test
    void findByEmail_UserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getMicrosoftId(), foundUser.get().getMicrosoftId());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void findAllActiveUsers() {
        List<User> activeUsers = Arrays.asList(testUser, activeUser);
        when(userRepository.findByActiveTrue()).thenReturn(activeUsers);

        List<User> foundUsers = userService.findAllActiveUsers();

        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.stream().allMatch(User::isActive));
        verify(userRepository).findByActiveTrue();
    }

    @Test
    void findById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.findById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findById(999L);

        assertFalse(foundUser.isPresent());
        verify(userRepository).findById(999L);
    }
} 