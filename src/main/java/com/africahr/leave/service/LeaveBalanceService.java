package com.africahr.leave.service;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.LeaveBalance;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.dto.LeaveBalanceDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LeaveBalanceService {
    @Transactional(readOnly = true)
    List<LeaveBalance> getLeaveBalancesByUser(User user);

    @Transactional(readOnly = true)
    Optional<LeaveBalance> getLeaveBalanceByUserAndType(User user, LeaveType leaveType);

    @Transactional
    void processMonthlyAccrual(LocalDate accrualDate);

    @Transactional
    void processYearEndCarryForward(int year);

    @Transactional(readOnly = true)
    Map<LeaveType, Double> getLeaveBalanceSummary(User user);

    @Transactional
    LeaveBalance adjustLeaveBalance(User user, LeaveType leaveType, double adjustment, String reason);

    @Transactional(readOnly = true)
    List<LeaveBalance> getLeaveBalanceHistory(User user, LeaveType leaveType, LocalDate startDate, LocalDate endDate);

    @Transactional
    LeaveBalance createLeaveBalance(User user, LeaveType leaveType, int year);

    @Transactional
    LeaveBalance updateLeaveBalance(LeaveBalance leaveBalance);

    @Transactional(readOnly = true)
    LeaveBalanceDTO getLeaveBalanceDTO(User user, LeaveType leaveType, int year);

    @Transactional(readOnly = true)
    List<LeaveBalanceDTO> getLeaveBalanceDTOs(User user, int year);

    @Transactional
    void deleteLeaveBalance(Long id);

    @Transactional(readOnly = true)
    LeaveBalance getLeaveBalanceById(Long id);

    @Transactional(readOnly = true)
    LeaveBalance getLeaveBalanceByUserAndLeaveTypeAndYear(User user, LeaveType leaveType, Integer year);

    @Transactional(readOnly = true)
    List<LeaveBalance> getLeaveBalancesByUserAndYear(User user, Integer year);

    @Transactional
    void updateLeaveBalanceAfterRequest(LeaveRequest leaveRequest);

    @Transactional
    void recalculateLeaveBalance(User user, LeaveType leaveType, Integer year);

    @Transactional
    void initializeUserLeaveBalances(User user);

    @Transactional
    void updateBalanceAfterLeaveRequestStatusChange(LeaveRequest leaveRequest, LeaveRequestStatus oldStatus);

    @Transactional(readOnly = true)
    List<LeaveBalance> getLeaveBalancesByUserId(Long userId);

    @Transactional(readOnly = true)
    LeaveBalance getLeaveBalanceByUserIdAndType(Long userId, LeaveType leaveType);
} 