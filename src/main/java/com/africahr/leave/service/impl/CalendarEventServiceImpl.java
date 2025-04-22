package com.africahr.leave.service.impl;

import com.africahr.leave.model.entity.CalendarEvent;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.entity.User;
import com.africahr.leave.repository.CalendarEventRepository;
import com.africahr.leave.repository.LeaveRequestRepository;
import com.africahr.leave.service.CalendarEventService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarEventServiceImpl implements CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public CalendarEventServiceImpl(CalendarEventRepository calendarEventRepository, 
                                  LeaveRequestRepository leaveRequestRepository) {
        this.calendarEventRepository = calendarEventRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    @Transactional
    public CalendarEvent createEvent(CalendarEvent event) {
        event.setCreatedAt(LocalDateTime.now());
        event.setActive(true);
        return calendarEventRepository.save(event);
    }

    @Override
    @Transactional
    public CalendarEvent updateEvent(Long id, CalendarEvent event) {
        CalendarEvent existingEvent = getEventById(id);
        
        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setStartDateTime(event.getStartDateTime());
        existingEvent.setEndDateTime(event.getEndDateTime());
        existingEvent.setAllDay(event.isAllDay());
        existingEvent.setUpdatedAt(LocalDateTime.now());

        return calendarEventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        CalendarEvent event = getEventById(id);
        event.setActive(false);
        calendarEventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarEvent getEventById(Long id) {
        return calendarEventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Calendar event not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEvent> getEventsByUser(User user) {
        return calendarEventRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEvent> getEventsByUserAndDateRange(User user, LocalDateTime start, LocalDateTime end) {
        return calendarEventRepository.findByUserAndStartDateTimeBetween(user, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEvent> getEventsByLeaveRequest(Long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));
        return calendarEventRepository.findByLeaveRequest(leaveRequest);
    }

    @Override
    @Transactional
    public CalendarEvent createEventFromLeaveRequest(Long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));

        CalendarEvent event = CalendarEvent.builder()
                .title("Leave Request: " + leaveRequest.getUser().getEmail())
                .description(leaveRequest.getReason())
                .startDateTime(leaveRequest.getStartDate().atStartOfDay())
                .endDateTime(leaveRequest.getEndDate().atStartOfDay())
                .allDay(true)
                .user(leaveRequest.getUser())
                .leaveRequest(leaveRequest)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        return calendarEventRepository.save(event);
    }
} 