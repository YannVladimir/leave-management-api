package com.africahr.leave.service.impl;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.Department;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.repository.LeaveRequestRepository;
import com.africahr.leave.service.LeaveReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveReportServiceImpl implements LeaveReportService {

    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<LeaveType, Double> getUserLeaveDays(User user, LocalDate startDate, LocalDate endDate) {
        log.info("Generating leave report for user {} between {} and {}", 
                user.getEmail(), startDate, endDate);
        
        List<LeaveRequest> leaveRequests = leaveRequestRepository
                .findByUserAndStartDateBetweenAndStatus(user, startDate, endDate, LeaveRequestStatus.APPROVED);
        
        return leaveRequests.stream()
                .collect(Collectors.groupingBy(
                        LeaveRequest::getLeaveType,
                        Collectors.summingDouble(this::calculateLeaveDays)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LeaveType, Double> getDepartmentLeaveSummary(Department department, LocalDate startDate, LocalDate endDate) {
        log.info("Generating department leave summary for {} between {} and {}", 
                department.getName(), startDate, endDate);
        
        List<LeaveRequest> leaveRequests = leaveRequestRepository
                .findByUserDepartmentAndStartDateBetweenAndStatus(department, startDate, endDate, LeaveRequestStatus.APPROVED);
        
        return leaveRequests.stream()
                .collect(Collectors.groupingBy(
                        LeaveRequest::getLeaveType,
                        Collectors.summingDouble(this::calculateLeaveDays)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LeaveType, Long> getMostCommonLeaveTypes(LocalDate startDate, LocalDate endDate) {
        log.info("Finding most common leave types between {} and {}", startDate, endDate);
        
        List<LeaveRequest> leaveRequests = leaveRequestRepository
                .findByStartDateBetweenAndStatus(startDate, endDate, LeaveRequestStatus.APPROVED);
        
        return leaveRequests.stream()
                .collect(Collectors.groupingBy(
                        LeaveRequest::getLeaveType,
                        Collectors.counting()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Double> getBusiestLeaveMonths(LocalDate startDate, LocalDate endDate) {
        log.info("Finding busiest leave months between {} and {}", startDate, endDate);
        
        List<LeaveRequest> leaveRequests = leaveRequestRepository
                .findByStartDateBetweenAndStatus(startDate, endDate, LeaveRequestStatus.APPROVED);
        
        return leaveRequests.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getStartDate().getMonthValue(),
                        Collectors.summingDouble(this::calculateLeaveDays)
                ));
    }

    private double calculateLeaveDays(LeaveRequest request) {
        // Calculate the number of days between start and end dates (inclusive)
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        // If it's a half-day leave, multiply by 0.5
        return request.isHalfDay() ? days * 0.5 : days;
    }
} 