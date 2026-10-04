package com.africahr.leave.service;

import com.africahr.leave.model.entity.CalendarEvent;
import com.africahr.leave.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarEventService {
    CalendarEvent createEvent(CalendarEvent event);
    CalendarEvent updateEvent(Long id, CalendarEvent event);
    void deleteEvent(Long id);
    CalendarEvent getEventById(Long id);
    List<CalendarEvent> getEventsByUser(User user);
    List<CalendarEvent> getEventsByUserAndDateRange(User user, LocalDateTime start, LocalDateTime end);
    List<CalendarEvent> getEventsByLeaveRequest(Long leaveRequestId);
    CalendarEvent createEventFromLeaveRequest(Long leaveRequestId);
} 