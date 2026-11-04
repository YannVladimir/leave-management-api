package com.africahr.leave.controller;

import com.africahr.leave.dto.CreateLeaveRequestDTO;
import com.africahr.leave.dto.LeaveRequestDTO;
import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.service.LeaveRequestService;
import com.africahr.leave.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * This class previously existed only as an orphaned file containing imports with no
 * class body (so Surefire silently reported it as contributing zero tests). It is
 * rewritten here as a real test class covering the controller's request/response wiring,
 * including the manager-only approve/reject/team endpoints that were found to have no
 * authorization enforcement at all (see the @PreAuthorize additions on
 * LeaveRequestController and the corresponding SecurityConfig fix).
 */
@ExtendWith(MockitoExtension.class)
class LeaveRequestControllerTest {

    @Mock
    private LeaveRequestService leaveRequestService;

    @Mock
    private UserService userService;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private LeaveRequestController leaveRequestController;

    private User employee;
    private User manager;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        manager = new User();
        manager.setId(2L);
        manager.setEmail("manager@example.com");
        manager.setFullName("Manager Person");

        employee = new User();
        employee.setId(1L);
        employee.setEmail("employee@example.com");
        employee.setFullName("Employee Person");
        employee.setManager(manager);

        leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setUser(employee);
        leaveRequest.setLeaveType(LeaveType.ANNUAL);
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(3));
        leaveRequest.setReason("Vacation");
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
    }

    @Test
    void createLeaveRequest_ReturnsCreatedRequest() {
        when(principal.getAttribute("email")).thenReturn("employee@example.com");
        when(userService.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));

        CreateLeaveRequestDTO dto = new CreateLeaveRequestDTO();
        dto.setLeaveType(LeaveType.ANNUAL);
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setReason("Vacation");

        when(leaveRequestService.createLeaveRequest(
                eq(employee), eq(LeaveType.ANNUAL), eq(dto.getStartDate()), eq(dto.getEndDate()), eq("Vacation")))
                .thenReturn(leaveRequest);

        ResponseEntity<LeaveRequestDTO> response = leaveRequestController.createLeaveRequest(principal, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(employee.getId(), response.getBody().getUserId());
        assertEquals(LeaveRequestStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    void getMyLeaveRequests_ReturnsOnlyCallersRequests() {
        when(principal.getAttribute("email")).thenReturn("employee@example.com");
        when(userService.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(leaveRequestService.getLeaveRequestsByUser(employee)).thenReturn(List.of(leaveRequest));

        ResponseEntity<List<LeaveRequestDTO>> response = leaveRequestController.getMyLeaveRequests(principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(leaveRequestService).getLeaveRequestsByUser(employee);
    }

    @Test
    void approveLeaveRequest_DelegatesToServiceWithCallingManager() {
        when(principal.getAttribute("email")).thenReturn("manager@example.com");
        when(userService.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));

        LeaveRequest approved = new LeaveRequest();
        approved.setId(1L);
        approved.setUser(employee);
        approved.setLeaveType(LeaveType.ANNUAL);
        approved.setStartDate(leaveRequest.getStartDate());
        approved.setEndDate(leaveRequest.getEndDate());
        approved.setReason("Vacation");
        approved.setStatus(LeaveRequestStatus.APPROVED);
        approved.setComments("Enjoy");

        when(leaveRequestService.approveLeaveRequest(1L, manager, "Enjoy")).thenReturn(approved);

        ResponseEntity<LeaveRequestDTO> response =
                leaveRequestController.approveLeaveRequest(principal, 1L, "Enjoy");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(LeaveRequestStatus.APPROVED, response.getBody().getStatus());
        // The controller must pass the authenticated caller through as the approving
        // manager - the service layer is what rejects a manager who isn't this
        // employee's actual manager (see LeaveRequestServiceTest#approveLeaveRequest_Unauthorized).
        verify(leaveRequestService).approveLeaveRequest(1L, manager, "Enjoy");
    }

    @Test
    void approveLeaveRequest_PropagatesServiceAuthorizationFailure() {
        when(principal.getAttribute("email")).thenReturn("manager@example.com");
        when(userService.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));
        when(leaveRequestService.approveLeaveRequest(eq(1L), eq(manager), any()))
                .thenThrow(new RuntimeException("Not authorized to approve this request"));

        assertThrows(RuntimeException.class,
                () -> leaveRequestController.approveLeaveRequest(principal, 1L, "Enjoy"));
    }

    @Test
    void rejectLeaveRequest_DelegatesToServiceWithCallingManager() {
        when(principal.getAttribute("email")).thenReturn("manager@example.com");
        when(userService.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));

        LeaveRequest rejected = new LeaveRequest();
        rejected.setId(1L);
        rejected.setUser(employee);
        rejected.setLeaveType(LeaveType.ANNUAL);
        rejected.setStartDate(leaveRequest.getStartDate());
        rejected.setEndDate(leaveRequest.getEndDate());
        rejected.setReason("Vacation");
        rejected.setStatus(LeaveRequestStatus.REJECTED);
        rejected.setComments("Short staffed");

        when(leaveRequestService.rejectLeaveRequest(1L, manager, "Short staffed")).thenReturn(rejected);

        ResponseEntity<LeaveRequestDTO> response =
                leaveRequestController.rejectLeaveRequest(principal, 1L, "Short staffed");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(LeaveRequestStatus.REJECTED, response.getBody().getStatus());
    }

    @Test
    void getTeamLeaveRequests_ReturnsRequestsForManager() {
        when(principal.getAttribute("email")).thenReturn("manager@example.com");
        when(userService.findByEmail("manager@example.com")).thenReturn(Optional.of(manager));
        when(leaveRequestService.getTeamLeaveRequests(manager)).thenReturn(Arrays.asList(leaveRequest));

        ResponseEntity<List<LeaveRequestDTO>> response = leaveRequestController.getTeamLeaveRequests(principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(leaveRequestService).getTeamLeaveRequests(manager);
    }

    @Test
    void cancelLeaveRequest_DelegatesToServiceForCallingUser() {
        when(principal.getAttribute("email")).thenReturn("employee@example.com");
        when(userService.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));

        LeaveRequest cancelled = new LeaveRequest();
        cancelled.setId(1L);
        cancelled.setUser(employee);
        cancelled.setLeaveType(LeaveType.ANNUAL);
        cancelled.setStartDate(leaveRequest.getStartDate());
        cancelled.setEndDate(leaveRequest.getEndDate());
        cancelled.setReason("Vacation");
        cancelled.setStatus(LeaveRequestStatus.CANCELLED);

        when(leaveRequestService.cancelLeaveRequest(1L, employee, "Changed plans")).thenReturn(cancelled);

        ResponseEntity<LeaveRequestDTO> response =
                leaveRequestController.cancelLeaveRequest(principal, 1L, "Changed plans");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(LeaveRequestStatus.CANCELLED, response.getBody().getStatus());
    }

    @Test
    void getLeaveRequest_UserNotFound_Throws() {
        when(principal.getAttribute("email")).thenReturn("ghost@example.com");
        when(userService.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> leaveRequestController.getLeaveRequest(principal, 1L));
    }
}
