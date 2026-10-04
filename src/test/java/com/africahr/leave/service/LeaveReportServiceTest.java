package com.africahr.leave.service;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.Department;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.repository.LeaveRequestRepository;
import com.africahr.leave.service.impl.LeaveReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveReportServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private LeaveReportServiceImpl leaveReportService;

    private User user;
    private Department department;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Test Department");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setFullName("Test User");
        user.setDepartment(department);

        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
    }

    @Test
    void getUserLeaveDays_WithApprovedRequests_ReturnsCorrectCount() {
        // Given
        LeaveRequest request1 = createLeaveRequest(LeaveType.ANNUAL, 5, LocalDate.now());
        LeaveRequest request2 = createLeaveRequest(LeaveType.SICK, 3, LocalDate.now());
        List<LeaveRequest> requests = Arrays.asList(request1, request2);

        when(leaveRequestRepository.findByUserAndStartDateBetweenAndStatus(
                any(User.class), any(LocalDate.class), any(LocalDate.class), any(LeaveRequestStatus.class)))
                .thenReturn(requests);

        // When
        Map<LeaveType, Double> result = leaveReportService.getUserLeaveDays(user, startDate, endDate);

        // Then
        assertEquals(2, result.size());
        assertEquals(5.0, result.get(LeaveType.ANNUAL));
        assertEquals(3.0, result.get(LeaveType.SICK));
    }

    @Test
    void getUserLeaveDays_WithNoRequests_ReturnsEmptyMap() {
        // Given
        when(leaveRequestRepository.findByUserAndStartDateBetweenAndStatus(
                any(User.class), any(LocalDate.class), any(LocalDate.class), any(LeaveRequestStatus.class)))
                .thenReturn(Collections.emptyList());

        // When
        Map<LeaveType, Double> result = leaveReportService.getUserLeaveDays(user, startDate, endDate);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getDepartmentLeaveSummary_WithMultipleUsers_ReturnsAggregatedCount() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@example.com");
        user2.setFirstName("Test2");
        user2.setLastName("User2");
        user2.setFullName("Test2 User2");
        user2.setDepartment(department);

        LeaveRequest request1 = createLeaveRequest(LeaveType.ANNUAL, 5, LocalDate.now());
        LeaveRequest request2 = createLeaveRequest(LeaveType.ANNUAL, 3, LocalDate.now());
        List<LeaveRequest> requests = Arrays.asList(request1, request2);

        when(leaveRequestRepository.findByUserDepartmentAndStartDateBetweenAndStatus(
                any(Department.class), any(LocalDate.class), any(LocalDate.class), any(LeaveRequestStatus.class)))
                .thenReturn(requests);

        // When
        Map<LeaveType, Double> result = leaveReportService.getDepartmentLeaveSummary(department, startDate, endDate);

        // Then
        assertEquals(1, result.size());
        assertEquals(8.0, result.get(LeaveType.ANNUAL));
    }

    @Test
    void getMostCommonLeaveTypes_ReturnsCorrectCount() {
        // Given
        LeaveRequest request1 = createLeaveRequest(LeaveType.ANNUAL, 5, LocalDate.now());
        LeaveRequest request2 = createLeaveRequest(LeaveType.ANNUAL, 3, LocalDate.now());
        LeaveRequest request3 = createLeaveRequest(LeaveType.SICK, 2, LocalDate.now());
        List<LeaveRequest> requests = Arrays.asList(request1, request2, request3);

        when(leaveRequestRepository.findByStartDateBetweenAndStatus(
                any(LocalDate.class), any(LocalDate.class), any(LeaveRequestStatus.class)))
                .thenReturn(requests);

        // When
        Map<LeaveType, Long> result = leaveReportService.getMostCommonLeaveTypes(startDate, endDate);

        // Then
        assertEquals(2, result.size());
        assertEquals(2L, result.get(LeaveType.ANNUAL));
        assertEquals(1L, result.get(LeaveType.SICK));
    }

    @Test
    void getBusiestLeaveMonths_ReturnsCorrectMonthlyTotals() {
        // Given
        LeaveRequest request1 = createLeaveRequest(LeaveType.ANNUAL, 5, LocalDate.of(2024, 1, 1));
        LeaveRequest request2 = createLeaveRequest(LeaveType.ANNUAL, 3, LocalDate.of(2024, 1, 15));
        List<LeaveRequest> requests = Arrays.asList(request1, request2);

        when(leaveRequestRepository.findByStartDateBetweenAndStatus(
                any(LocalDate.class), any(LocalDate.class), any(LeaveRequestStatus.class)))
                .thenReturn(requests);

        // When
        Map<Integer, Double> result = leaveReportService.getBusiestLeaveMonths(startDate, endDate);

        // Then
        assertEquals(1, result.size());
        assertEquals(8.0, result.get(1)); // January is month 1
    }

    private LeaveRequest createLeaveRequest(LeaveType type, int days, LocalDate startDate) {
        LeaveRequest request = new LeaveRequest();
        request.setLeaveType(type);
        request.setStartDate(startDate);
        request.setEndDate(startDate.plusDays(days - 1));
        request.setStatus(LeaveRequestStatus.APPROVED);
        request.setUser(user);
        request.setReason("Test reason");
        return request;
    }
} 