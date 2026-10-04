package com.africahr.leave.service;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.repository.LeaveRequestRepository;
import com.africahr.leave.service.impl.LeaveRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private HolidayService holidayService;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    private User employee;
    private User manager;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        employee = new User();
        employee.setId(1L);
        employee.setEmail("employee@example.com");
        employee.setActive(true);

        manager = new User();
        manager.setId(2L);
        manager.setEmail("manager@example.com");
        manager.setActive(true);

        employee.setManager(manager);

        leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setUser(employee);
        leaveRequest.setLeaveType(LeaveType.ANNUAL);
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(5));
        leaveRequest.setReason("Vacation");
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
    }

    @Test
    void createLeaveRequest_Success() {
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequest createdRequest = leaveRequestService.createLeaveRequest(
            employee, LeaveType.ANNUAL, leaveRequest.getStartDate(), 
            leaveRequest.getEndDate(), leaveRequest.getReason()
        );

        assertNotNull(createdRequest);
        assertEquals(LeaveRequestStatus.PENDING, createdRequest.getStatus());
        assertEquals(employee, createdRequest.getUser());
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void updateLeaveRequest_Success() {
        when(leaveRequestRepository.findByIdAndUser(1L, employee)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LocalDate newStartDate = LocalDate.now().plusDays(10);
        LocalDate newEndDate = LocalDate.now().plusDays(15);
        
        LeaveRequest updatedRequest = leaveRequestService.updateLeaveRequest(
            1L, employee, LeaveType.SICK, newStartDate, newEndDate, "Updated reason"
        );

        assertNotNull(updatedRequest);
        assertEquals(LeaveType.SICK, updatedRequest.getLeaveType());
        assertEquals(newStartDate, updatedRequest.getStartDate());
        assertEquals(newEndDate, updatedRequest.getEndDate());
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void updateLeaveRequest_NotFound() {
        when(leaveRequestRepository.findByIdAndUser(1L, employee)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            leaveRequestService.updateLeaveRequest(
                1L, employee, LeaveType.ANNUAL, 
                LocalDate.now(), LocalDate.now(), "Reason"
            )
        );
    }

    @Test
    void approveLeaveRequest_Success() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequest approvedRequest = leaveRequestService.approveLeaveRequest(1L, manager, "Approved");

        assertNotNull(approvedRequest);
        assertEquals(LeaveRequestStatus.APPROVED, approvedRequest.getStatus());
        assertEquals("Approved", approvedRequest.getComments());
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void approveLeaveRequest_Unauthorized() {
        User unauthorizedManager = new User();
        unauthorizedManager.setId(3L);
        unauthorizedManager.setEmail("unauthorized@example.com");

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(RuntimeException.class, () -> 
            leaveRequestService.approveLeaveRequest(1L, unauthorizedManager, "Approved")
        );
    }

    @Test
    void rejectLeaveRequest_Success() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequest rejectedRequest = leaveRequestService.rejectLeaveRequest(1L, manager, "Rejected");

        assertNotNull(rejectedRequest);
        assertEquals(LeaveRequestStatus.REJECTED, rejectedRequest.getStatus());
        assertEquals("Rejected", rejectedRequest.getComments());
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void cancelLeaveRequest_Success() {
        when(leaveRequestRepository.findByIdAndUser(1L, employee)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequest cancelledRequest = leaveRequestService.cancelLeaveRequest(1L, employee, "Cancelled");

        assertNotNull(cancelledRequest);
        assertEquals(LeaveRequestStatus.CANCELLED, cancelledRequest.getStatus());
        assertEquals("Cancelled", cancelledRequest.getComments());
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void getTeamLeaveRequests_Success() {
        List<LeaveRequest> teamRequests = Arrays.asList(leaveRequest);
        when(leaveRequestRepository.findByUserManager(manager)).thenReturn(teamRequests);

        List<LeaveRequest> result = leaveRequestService.getTeamLeaveRequests(manager);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(leaveRequestRepository).findByUserManager(manager);
    }

    @Test
    void getLeaveRequestsByUserAndStatus_Success() {
        List<LeaveRequest> pendingRequests = Arrays.asList(leaveRequest);
        when(leaveRequestRepository.findByUserAndStatus(employee, LeaveRequestStatus.PENDING))
            .thenReturn(pendingRequests);

        List<LeaveRequest> result = leaveRequestService.getLeaveRequestsByUserAndStatus(
            employee, LeaveRequestStatus.PENDING
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LeaveRequestStatus.PENDING, result.get(0).getStatus());
        verify(leaveRequestRepository).findByUserAndStatus(employee, LeaveRequestStatus.PENDING);
    }

    @Test
    void getLeaveRequestsByDateRange_Success() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<LeaveRequest> requests = Arrays.asList(leaveRequest);
        
        when(leaveRequestRepository.findByStartDateBetween(startDate, endDate))
            .thenReturn(requests);

        List<LeaveRequest> result = leaveRequestService.getLeaveRequestsByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(leaveRequestRepository).findByStartDateBetween(startDate, endDate);
    }
} 