package com.africahr.leave.controller;

import com.africahr.leave.dto.LeaveBalanceDTO;
import com.africahr.leave.model.entity.LeaveBalance;
import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.service.LeaveBalanceService;
import com.africahr.leave.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveBalanceControllerTest {

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @Mock
    private UserService userService;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private LeaveBalanceController leaveBalanceController;

    private User user;
    private LeaveBalance leaveBalance;
    private LeaveBalanceDTO leaveBalanceDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        leaveBalance = LeaveBalance.builder()
                .id(1L)
                .user(user)
                .leaveType(LeaveType.ANNUAL)
                .balance(20.0)
                .totalAccrued(20.0)
                .usedDays(0.0)
                .carriedForward(0.0)
                .year(2024)
                .build();

        leaveBalanceDTO = LeaveBalanceDTO.builder()
                .id(1L)
                .userId(1L)
                .userFullName("John Doe")
                .leaveType(LeaveType.ANNUAL)
                .balance(20.0)
                .totalAccrued(20.0)
                .usedDays(0.0)
                .carriedForward(0.0)
                .year(2024)
                .build();
    }

    @Test
    void getMyLeaveBalances_ShouldReturnBalances() {
        // Given
        when(principal.getAttribute("email")).thenReturn("john.doe@example.com");
        when(userService.findByEmail(eq("john.doe@example.com"))).thenReturn(Optional.of(user));
        when(leaveBalanceService.getLeaveBalancesByUser(eq(user)))
                .thenReturn(Arrays.asList(leaveBalance));

        // When
        ResponseEntity<List<LeaveBalanceDTO>> response = leaveBalanceController.getMyLeaveBalances(principal);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(leaveBalanceDTO.getId(), response.getBody().get(0).getId());
        verify(userService).findByEmail(eq("john.doe@example.com"));
        verify(leaveBalanceService).getLeaveBalancesByUser(eq(user));
    }

    @Test
    void getLeaveBalanceSummary_ShouldReturnSummary() {
        // Given
        when(principal.getAttribute("email")).thenReturn("john.doe@example.com");
        when(userService.findByEmail(eq("john.doe@example.com"))).thenReturn(Optional.of(user));
        Map<LeaveType, Double> summary = Map.of(LeaveType.ANNUAL, 20.0);
        when(leaveBalanceService.getLeaveBalanceSummary(eq(user)))
                .thenReturn(summary);

        // When
        ResponseEntity<Map<LeaveType, Double>> response = leaveBalanceController.getLeaveBalanceSummary(principal);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(summary, response.getBody());
        verify(userService).findByEmail(eq("john.doe@example.com"));
        verify(leaveBalanceService).getLeaveBalanceSummary(eq(user));
    }

    @Test
    void getUserLeaveBalances_ShouldReturnBalances() {
        // Given
        when(leaveBalanceService.getLeaveBalancesByUserId(eq(1L)))
                .thenReturn(Arrays.asList(leaveBalance));

        // When
        ResponseEntity<List<LeaveBalanceDTO>> response = leaveBalanceController.getUserLeaveBalances(1L);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(leaveBalanceDTO.getId(), response.getBody().get(0).getId());
        verify(leaveBalanceService).getLeaveBalancesByUserId(eq(1L));
    }
} 