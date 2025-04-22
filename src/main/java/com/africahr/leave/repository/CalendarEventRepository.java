package com.africahr.leave.repository;

import com.africahr.leave.model.entity.CalendarEvent;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByUser(User user);
    List<CalendarEvent> findByLeaveRequest(LeaveRequest leaveRequest);
    List<CalendarEvent> findByUserAndStartDateTimeBetween(User user, LocalDateTime start, LocalDateTime end);
    List<CalendarEvent> findByUserAndActive(User user, boolean active);
} 