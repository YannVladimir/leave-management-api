package com.africahr.leave.service;

import com.africahr.leave.model.entity.LeaveBalance;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaveBalanceService {
    LeaveBalance createLeaveBalance(LeaveBalance leaveBalance);
    LeaveBalance updateLeaveBalance(Long id, LeaveBalance leaveBalance);
    void deleteLeaveBalance(Long id);
    LeaveBalance getLeaveBalanceById(Long id);
    LeaveBalance getLeaveBalanceByUserAndLeaveTypeAndYear(User user, LeaveType leaveType, Integer year);
    List<LeaveBalance> getLeaveBalancesByUser(User user);
    List<LeaveBalance> getLeaveBalancesByUserAndYear(User user, Integer year);
    void updateLeaveBalanceAfterRequest(LeaveRequest leaveRequest);
    void recalculateLeaveBalance(User user, LeaveType leaveType, Integer year);
    LeaveBalance getLeaveBalanceByUserAndType(User user, LeaveType leaveType);
    void processMonthlyAccrual(LocalDate accrualDate);
    void processYearEndCarryForward(int year);
    Map<LeaveType, Double> getLeaveBalanceSummary(User user);
    LeaveBalance adjustLeaveBalance(User user, LeaveType leaveType, double adjustment, String reason);
    List<LeaveBalance> getLeaveBalanceHistory(User user, LeaveType leaveType, LocalDate startDate, LocalDate endDate);
    
    // New methods
    void initializeUserLeaveBalances(User user);
    void updateBalanceAfterLeaveRequestStatusChange(LeaveRequest leaveRequest, LeaveRequestStatus oldStatus);
    List<LeaveBalance> getLeaveBalancesByUserId(Long userId);
    LeaveBalance getLeaveBalanceByUserIdAndType(Long userId, LeaveType leaveType);
} 