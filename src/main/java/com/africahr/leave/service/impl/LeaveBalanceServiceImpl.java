package com.africahr.leave.service.impl;

import com.africahr.leave.model.entity.LeaveBalance;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.repository.LeaveBalanceRepository;
import com.africahr.leave.service.LeaveBalanceService;
import com.africahr.leave.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveBalanceServiceImpl implements LeaveBalanceService {
    private static final Logger log = LoggerFactory.getLogger(LeaveBalanceServiceImpl.class);
    private static final double MONTHLY_ACCRUAL = 1.66; // 20 days per year
    private static final double MAX_CARRY_FORWARD = 5.0;

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserService userService;

    public LeaveBalanceServiceImpl(LeaveBalanceRepository leaveBalanceRepository, UserService userService) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.userService = userService;
    }

    @Override
    public List<LeaveBalance> getLeaveBalancesByUser(User user) {
        log.info("Fetching all leave balances for user: {}", user.getEmail());
        return leaveBalanceRepository.findByUser(user);
    }

    @Override
    public LeaveBalance getLeaveBalanceByUserAndType(User user, LeaveType leaveType) {
        log.info("Fetching {} balance for user: {}", leaveType, user.getEmail());
        return leaveBalanceRepository.findByUserAndLeaveType(user, leaveType)
                .orElseGet(() -> createNewBalance(user, leaveType));
    }

    @Override
    public void processMonthlyAccrual(LocalDate accrualDate) {
        log.info("Processing monthly leave accrual for date: {}", accrualDate);
        List<User> activeUsers = userService.findAllActiveUsers();
        for (User user : activeUsers) {
            LeaveBalance annualBalance = getLeaveBalanceByUserAndType(user, LeaveType.ANNUAL);
            annualBalance.setBalance(annualBalance.getBalance() + MONTHLY_ACCRUAL);
            leaveBalanceRepository.save(annualBalance);
            log.info("Accrued {} days of annual leave for user: {}", MONTHLY_ACCRUAL, user.getEmail());
        }
    }

    @Override
    public void processYearEndCarryForward(int year) {
        log.info("Processing year-end carry forward for year: {}", year);
        List<User> activeUsers = userService.findAllActiveUsers();
        for (User user : activeUsers) {
            LeaveBalance annualBalance = getLeaveBalanceByUserAndType(user, LeaveType.ANNUAL);
            double currentBalance = annualBalance.getBalance();
            double carryForward = Math.min(currentBalance, MAX_CARRY_FORWARD);
            
            // Create new balance for next year with carried forward days
            LeaveBalance newBalance = new LeaveBalance();
            newBalance.setUser(user);
            newBalance.setLeaveType(LeaveType.ANNUAL);
            newBalance.setBalance(carryForward);
            newBalance.setYear(year + 1);
            leaveBalanceRepository.save(newBalance);
            
            log.info("Carried forward {} days of annual leave for user: {} to year {}", 
                    carryForward, user.getEmail(), year + 1);
        }
    }

    @Override
    public Map<LeaveType, Double> getLeaveBalanceSummary(User user) {
        log.info("Fetching leave balance summary for user: {}", user.getEmail());
        return leaveBalanceRepository.findByUser(user).stream()
                .collect(Collectors.toMap(
                        LeaveBalance::getLeaveType,
                        LeaveBalance::getBalance
                ));
    }

    @Override
    public LeaveBalance adjustLeaveBalance(User user, LeaveType leaveType, double adjustment, String reason) {
        log.info("Adjusting {} balance by {} for user: {} with reason: {}", 
                leaveType, adjustment, user.getEmail(), reason);
        LeaveBalance balance = getLeaveBalanceByUserAndType(user, leaveType);
        balance.setBalance(balance.getBalance() + adjustment);
        return leaveBalanceRepository.save(balance);
    }

    @Override
    public List<LeaveBalance> getLeaveBalanceHistory(User user, LeaveType leaveType, 
            LocalDate startDate, LocalDate endDate) {
        log.info("Fetching {} balance history for user: {} between {} and {}", 
                leaveType, user.getEmail(), startDate, endDate);
        return leaveBalanceRepository.findByUserAndLeaveTypeAndLastUpdatedBetween(
                user, leaveType, startDate, endDate);
    }

    private LeaveBalance createNewBalance(User user, LeaveType leaveType) {
        log.info("Creating new {} balance for user: {}", leaveType, user.getEmail());
        LeaveBalance balance = new LeaveBalance();
        balance.setUser(user);
        balance.setLeaveType(leaveType);
        balance.setBalance(0.0);
        balance.setYear(LocalDate.now().getYear());
        return leaveBalanceRepository.save(balance);
    }

    @Override
    @Transactional
    public LeaveBalance createLeaveBalance(LeaveBalance leaveBalance) {
        leaveBalance.setLastUpdated(LocalDate.now());
        return leaveBalanceRepository.save(leaveBalance);
    }

    @Override
    @Transactional
    public LeaveBalance updateLeaveBalance(Long id, LeaveBalance leaveBalance) {
        LeaveBalance existingBalance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave balance not found with id: " + id));

        existingBalance.setTotalAccrued(leaveBalance.getTotalAccrued());
        existingBalance.setUsedDays(leaveBalance.getUsedDays());
        existingBalance.setCarriedForward(leaveBalance.getCarriedForward());
        existingBalance.setLastUpdated(LocalDate.now());

        return leaveBalanceRepository.save(existingBalance);
    }

    @Override
    @Transactional
    public void deleteLeaveBalance(Long id) {
        if (!leaveBalanceRepository.existsById(id)) {
            throw new EntityNotFoundException("Leave balance not found with id: " + id);
        }
        leaveBalanceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalance getLeaveBalanceById(Long id) {
        return leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave balance not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalance getLeaveBalanceByUserAndLeaveTypeAndYear(User user, LeaveType leaveType, Integer year) {
        return leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, leaveType, year)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Leave balance not found for user %s, leave type %s, year %d",
                                user.getEmail(), leaveType.getName(), year)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalance> getLeaveBalancesByUserAndYear(User user, Integer year) {
        return leaveBalanceRepository.findByUserAndYear(user, year);
    }

    @Override
    @Transactional
    public void updateLeaveBalanceAfterRequest(LeaveRequest leaveRequest) {
        if (leaveRequest.getStatus() != LeaveRequestStatus.APPROVED) {
            return;
        }

        Integer year = leaveRequest.getStartDate().getYear();
        LeaveBalance balance = getLeaveBalanceByUserAndLeaveTypeAndYear(
                leaveRequest.getUser(),
                leaveRequest.getLeaveType(),
                year
        );

        double daysToDeduct = calculateDaysToDeduct(leaveRequest);
        balance.setUsedDays(balance.getUsedDays() + daysToDeduct);
        balance.setLastUpdated(LocalDate.now());

        leaveBalanceRepository.save(balance);
    }

    @Override
    @Transactional
    public void recalculateLeaveBalance(User user, LeaveType leaveType, Integer year) {
        LeaveBalance balance = getLeaveBalanceByUserAndLeaveTypeAndYear(user, leaveType, year);
        
        // Recalculate total accrued based on service months and accrual rate
        if (leaveType.isAutoAccrual()) {
            double newTotalAccrued = calculateTotalAccrued(user, leaveType, year);
            balance.setTotalAccrued(newTotalAccrued);
        }

        // Recalculate carried forward from previous year
        if (year > 2023) { // Assuming 2023 is the first year
            LeaveBalance previousYearBalance = leaveBalanceRepository
                    .findByUserAndLeaveTypeAndYear(user, leaveType, year - 1)
                    .orElse(null);

            if (previousYearBalance != null) {
                double carriedForward = Math.min(
                        previousYearBalance.getTotalAccrued() - previousYearBalance.getUsedDays(),
                        leaveType.getMaxDaysPerYear() * 0.5 // Max 50% can be carried forward
                );
                balance.setCarriedForward(carriedForward);
            }
        }

        balance.setLastUpdated(LocalDate.now());
        leaveBalanceRepository.save(balance);
    }

    private double calculateDaysToDeduct(LeaveRequest leaveRequest) {
        if (leaveRequest.isHalfDay()) {
            return 0.5;
        }
        return leaveRequest.getStartDate().until(leaveRequest.getEndDate()).getDays() + 1;
    }

    private double calculateTotalAccrued(User user, LeaveType leaveType, Integer year) {
        // This is a simplified calculation. You might want to adjust based on your business rules
        double baseAccrual = leaveType.getAccrualRate() * 12; // Annual accrual
        return Math.min(baseAccrual, leaveType.getMaxDaysPerYear());
    }

    @Override
    @Transactional
    public void initializeUserLeaveBalances(User user) {
        log.info("Initializing leave balances for user: {}", user.getEmail());
        
        for (LeaveType leaveType : LeaveType.values()) {
            // Check if a balance already exists for this leave type
            if (leaveBalanceRepository.findByUserAndLeaveType(user, leaveType).isEmpty()) {
                LeaveBalance leaveBalance = new LeaveBalance();
                leaveBalance.setUser(user);
                leaveBalance.setLeaveType(leaveType);
                leaveBalance.setYear(LocalDate.now().getYear());
                
                // Set initial values based on leave type configuration
                if (leaveType.isAutoAccrual()) {
                    // For auto-accruing leave types, set initial balance based on accrual rate
                    double initialBalance = leaveType.getAccrualRate() * 12; // Monthly accrual for a year
                    leaveBalance.setBalance(initialBalance);
                    leaveBalance.setTotalAccrued(initialBalance);
                } else {
                    // For non-auto-accruing leave types, set balance to max days
                    leaveBalance.setBalance((double) leaveType.getMaxDaysPerYear());
                    leaveBalance.setTotalAccrued((double) leaveType.getMaxDaysPerYear());
                }
                
                leaveBalance.setUsedDays(0.0);
                leaveBalance.setCarriedForward(0.0);
                
                leaveBalanceRepository.save(leaveBalance);
                log.info("Created initial {} balance for user: {}", leaveType, user.getEmail());
            }
        }
    }

    @Override
    @Transactional
    public void updateBalanceAfterLeaveRequestStatusChange(LeaveRequest leaveRequest, LeaveRequestStatus oldStatus) {
        if (leaveRequest.getStatus() == LeaveRequestStatus.APPROVED && 
            oldStatus != LeaveRequestStatus.APPROVED) {
            // Deduct days from balance when request is newly approved
            updateLeaveBalanceAfterRequest(leaveRequest);
        } else if (leaveRequest.getStatus() != LeaveRequestStatus.APPROVED && 
                   oldStatus == LeaveRequestStatus.APPROVED) {
            // Add back days to balance when approval is revoked
            LeaveBalance balance = getLeaveBalanceByUserAndLeaveTypeAndYear(
                leaveRequest.getUser(),
                leaveRequest.getLeaveType(),
                leaveRequest.getStartDate().getYear()
            );
            double daysToAdd = calculateDaysToDeduct(leaveRequest);
            balance.setUsedDays(balance.getUsedDays() - daysToAdd);
            balance.setLastUpdated(LocalDate.now());
            leaveBalanceRepository.save(balance);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalance> getLeaveBalancesByUserId(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return getLeaveBalancesByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalance getLeaveBalanceByUserIdAndType(Long userId, LeaveType leaveType) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return getLeaveBalanceByUserAndType(user, leaveType);
    }
} 