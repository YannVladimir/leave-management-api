package com.africahr.leave.config;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.africahr.leave.service.LeaveBalanceService;
import com.africahr.leave.service.impl.LeaveSchedulerService;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @InjectMocks
    private LeaveSchedulerService leaveSchedulerService;

    @Test
    void processMonthlyAccrual_ShouldCallLeaveBalanceService() {
        // When
        leaveSchedulerService.processMonthlyAccrual();

        // Then
        verify(leaveBalanceService).processMonthlyAccrual(any(LocalDate.class));
    }

    @Test
    void processYearEndCarryForward_ShouldCallLeaveBalanceService() {
        // When
        leaveSchedulerService.processYearEndCarryForward();

        // Then
        verify(leaveBalanceService).processYearEndCarryForward(any(Integer.class));
    }
} 