package com.africahr.leave.controller;

import com.africahr.leave.model.entity.Department;
import com.africahr.leave.model.User;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.service.DepartmentService;
import com.africahr.leave.service.LeaveReportService;
import com.africahr.leave.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class LeaveReportController {

    private final LeaveReportService leaveReportService;
    private final UserService userService;
    private final DepartmentService departmentService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Map<LeaveType, Double>> getUserLeaveDays(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            Map<LeaveType, Double> leaveDays = leaveReportService.getUserLeaveDays(user, startDate, endDate);
            return ResponseEntity.ok(leaveDays);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @GetMapping("/department-leaves")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Map<LeaveType, Double>> getDepartmentLeaveSummary(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        try {
            Department department = departmentService.findById(departmentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found with id: " + departmentId));
            
            LocalDate startDate = period.atDay(1);
            LocalDate endDate = period.atEndOfMonth();
            
            return ResponseEntity.ok(leaveReportService.getDepartmentLeaveSummary(department, startDate, endDate));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/common-leave-types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Map<LeaveType, Long>> getMostCommonLeaveTypes(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();
        
        return ResponseEntity.ok(leaveReportService.getMostCommonLeaveTypes(startDate, endDate));
    }

    @GetMapping("/busiest-months")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Map<Integer, Double>> getBusiestLeaveMonths(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();
        
        return ResponseEntity.ok(leaveReportService.getBusiestLeaveMonths(startDate, endDate));
    }
} 