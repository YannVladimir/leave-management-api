package com.africahr.leave.controller;

import com.africahr.leave.dto.CreateLeaveRequestDTO;
import com.africahr.leave.dto.LeaveRequestDTO;
import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.service.LeaveRequestService;
import com.africahr.leave.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> createLeaveRequest(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody CreateLeaveRequestDTO requestDTO) {
        String userEmail = principal.getAttribute("email");
        log.info("Creating leave request for user: {}", userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LeaveRequest leaveRequest = leaveRequestService.createLeaveRequest(
                user,
                requestDTO.getLeaveType(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getReason()
        );
        
        return ResponseEntity.ok(LeaveRequestDTO.fromEntity(leaveRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> updateLeaveRequest(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id,
            @Valid @RequestBody CreateLeaveRequestDTO requestDTO) {
        String userEmail = principal.getAttribute("email");
        log.info("Updating leave request {} for user: {}", id, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LeaveRequest leaveRequest = leaveRequestService.updateLeaveRequest(
                id,
                user,
                requestDTO.getLeaveType(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getReason()
        );
        
        return ResponseEntity.ok(LeaveRequestDTO.fromEntity(leaveRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id) {
        String userEmail = principal.getAttribute("email");
        log.info("Deleting leave request {} for user: {}", id, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        leaveRequestService.deleteLeaveRequest(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getLeaveRequest(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching leave request {} for user: {}", id, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(id, user);
        return ResponseEntity.ok(LeaveRequestDTO.fromEntity(leaveRequest));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<LeaveRequestDTO>> getMyLeaveRequests(
            @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching leave requests for user: {}", userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByUser(user);
        List<LeaveRequestDTO> dtos = leaveRequests.stream()
                .map(LeaveRequestDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/team")
    public ResponseEntity<List<LeaveRequestDTO>> getTeamLeaveRequests(
            @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching team leave requests for manager: {}", userEmail);
        User manager = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<LeaveRequest> leaveRequests = leaveRequestService.getTeamLeaveRequests(manager);
        List<LeaveRequestDTO> dtos = leaveRequests.stream()
                .map(LeaveRequestDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestDTO> approveLeaveRequest(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id,
            @RequestParam(required = false) String comments) {
        String userEmail = principal.getAttribute("email");
        log.info("Approving leave request {} by manager: {}", id, userEmail);
        User manager = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LeaveRequest leaveRequest = leaveRequestService.approveLeaveRequest(id, manager, comments);
        return ResponseEntity.ok(LeaveRequestDTO.fromEntity(leaveRequest));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestDTO> rejectLeaveRequest(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id,
            @RequestParam(required = false) String comments) {
        String userEmail = principal.getAttribute("email");
        log.info("Rejecting leave request {} by manager: {}", id, userEmail);
        User manager = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LeaveRequest leaveRequest = leaveRequestService.rejectLeaveRequest(id, manager, comments);
        return ResponseEntity.ok(LeaveRequestDTO.fromEntity(leaveRequest));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<LeaveRequestDTO> cancelLeaveRequest(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable Long id,
            @RequestParam(required = false) String comments) {
        String userEmail = principal.getAttribute("email");
        log.info("Canceling leave request {} by user: {}", id, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LeaveRequest leaveRequest = leaveRequestService.cancelLeaveRequest(id, user, comments);
        return ResponseEntity.ok(LeaveRequestDTO.fromEntity(leaveRequest));
    }

    @GetMapping("/my-requests/status/{status}")
    public ResponseEntity<List<LeaveRequest>> getMyLeaveRequestsByStatus(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable LeaveRequestStatus status) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching {} leave requests for user: {}", status, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(leaveRequestService.getLeaveRequestsByUserAndStatus(user, status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching leave requests between {} and {}", startDate, endDate);
        return ResponseEntity.ok(leaveRequestService.getLeaveRequestsByDateRange(startDate, endDate));
    }
} 