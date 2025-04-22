package com.africahr.leave.repository;

import com.africahr.leave.model.entity.LeaveBalance;
import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    List<LeaveBalance> findByUser(User user);
    Optional<LeaveBalance> findByUserAndLeaveType(User user, LeaveType leaveType);
    List<LeaveBalance> findByUserAndLeaveTypeAndLastUpdatedBetween(
            User user, LeaveType leaveType, LocalDate startDate, LocalDate endDate);
    List<LeaveBalance> findByUserAndYear(User user, int year);
    Optional<LeaveBalance> findByUserAndLeaveTypeAndYear(User user, LeaveType leaveType, Integer year);
} 