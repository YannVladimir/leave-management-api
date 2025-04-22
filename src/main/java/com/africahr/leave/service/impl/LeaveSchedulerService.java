package com.africahr.leave.service.impl;

import com.africahr.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveSchedulerService {

    private final LeaveBalanceService leaveBalanceService;

    // Run at midnight on the first day of each month
    @Scheduled(cron = "0 0 0 1 * ?")
    public void processMonthlyAccrual() {
        log.info("Starting monthly leave accrual process");
        try {
            leaveBalanceService.processMonthlyAccrual(LocalDate.now());
            log.info("Monthly leave accrual process completed successfully");
        } catch (Exception e) {
            log.error("Error during monthly leave accrual process", e);
        }
    }

    // Run at midnight on December 31st
    @Scheduled(cron = "0 0 0 31 12 ?")
    public void processYearEndCarryForward() {
        log.info("Starting year-end carry forward process");
        try {
            leaveBalanceService.processYearEndCarryForward(LocalDate.now().getYear());
            log.info("Year-end carry forward process completed successfully");
        } catch (Exception e) {
            log.error("Error during year-end carry forward process", e);
        }
    }
} 