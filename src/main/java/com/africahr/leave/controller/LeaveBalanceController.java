package com.africahr.leave.controller;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.LeaveBalance;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.dto.LeaveBalanceDTO;
import com.africahr.leave.service.LeaveBalanceService;
import com.africahr.leave.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave/balance")
@RequiredArgsConstructor
@Slf4j
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;
    private final UserService userService;

    @GetMapping("/my-balances")
    public ResponseEntity<List<LeaveBalanceDTO>> getMyLeaveBalances(
            @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching leave balances for user: {}", userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<LeaveBalance> leaveBalances = leaveBalanceService.getLeaveBalancesByUser(user);
        List<LeaveBalanceDTO> dtos = leaveBalances.stream()
                .map(LeaveBalanceDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getUserLeaveBalances(
            @PathVariable Long userId) {
        log.info("Fetching leave balances for user ID: {}", userId);
        List<LeaveBalance> leaveBalances = leaveBalanceService.getLeaveBalancesByUserId(userId);
        List<LeaveBalanceDTO> dtos = leaveBalances.stream()
                .map(LeaveBalanceDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-balances/{leaveType}")
    public ResponseEntity<LeaveBalanceDTO> getMyLeaveBalanceByType(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable LeaveType leaveType) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching {} balance for user: {}", leaveType, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        LeaveBalance balance = leaveBalanceService.getLeaveBalanceByUserAndType(user, leaveType)
                .orElseThrow(() -> new EntityNotFoundException("Leave balance not found"));
        return ResponseEntity.ok(LeaveBalanceDTO.fromEntity(balance));
    }

    @GetMapping("/user/{userId}/{leaveType}")
    public ResponseEntity<LeaveBalanceDTO> getUserLeaveBalanceByType(
            @PathVariable Long userId,
            @PathVariable LeaveType leaveType) {
        log.info("Fetching {} balance for user ID: {}", leaveType, userId);
        LeaveBalance balance = leaveBalanceService.getLeaveBalanceByUserIdAndType(userId, leaveType);
        return ResponseEntity.ok(LeaveBalanceDTO.fromEntity(balance));
    }

    @PostMapping("/accrue")
    public ResponseEntity<Void> accrueMonthlyLeave(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate accrualDate) {
        log.info("Processing monthly leave accrual for date: {}", accrualDate);
        leaveBalanceService.processMonthlyAccrual(accrualDate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/carry-forward")
    public ResponseEntity<Void> processYearEndCarryForward(
            @RequestParam int year) {
        log.info("Processing year-end carry forward for year: {}", year);
        leaveBalanceService.processYearEndCarryForward(year);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<LeaveType, Double>> getLeaveBalanceSummary(
            @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching leave balance summary for user: {}", userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(leaveBalanceService.getLeaveBalanceSummary(user));
    }

    @PostMapping("/adjust")
    public ResponseEntity<LeaveBalanceDTO> adjustLeaveBalance(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam LeaveType leaveType,
            @RequestParam double adjustment,
            @RequestParam String reason) {
        String userEmail = principal.getAttribute("email");
        log.info("Adjusting leave balance for user: {} by {} days", userEmail, adjustment);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LeaveBalance adjustedBalance = leaveBalanceService.adjustLeaveBalance(
                user, leaveType, adjustment, reason);
        return ResponseEntity.ok(LeaveBalanceDTO.fromEntity(adjustedBalance));
    }

    @GetMapping("/history")
    public ResponseEntity<List<LeaveBalanceDTO>> getLeaveBalanceHistory(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam LeaveType leaveType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching leave balance history for user: {} between {} and {}", 
                userEmail, startDate, endDate);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<LeaveBalance> history = leaveBalanceService.getLeaveBalanceHistory(
                user, leaveType, startDate, endDate);
        List<LeaveBalanceDTO> dtos = history.stream()
                .map(LeaveBalanceDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
} 