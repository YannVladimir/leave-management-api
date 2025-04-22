package com.africahr.leave.event;

import com.africahr.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedEventListener {
    private final LeaveBalanceService leaveBalanceService;

    @EventListener
    @Transactional
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Initializing leave balances for new user: {}", event.getUser().getEmail());
        leaveBalanceService.initializeUserLeaveBalances(event.getUser());
    }
} 