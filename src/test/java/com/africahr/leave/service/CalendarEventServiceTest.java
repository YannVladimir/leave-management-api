package com.africahr.leave.service;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.CalendarEvent;
import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.repository.CalendarEventRepository;
import com.africahr.leave.repository.LeaveRequestRepository;
import com.africahr.leave.service.impl.CalendarEventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private CalendarEventServiceImpl calendarEventService;

    private User user;
    private CalendarEvent event;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setActive(true);

        event = new CalendarEvent();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setStartDateTime(LocalDateTime.now());
        event.setEndDateTime(LocalDateTime.now().plusHours(2));
        event.setUser(user);
        event.setActive(true);

        leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setUser(user);
        leaveRequest.setStartDate(LocalDateTime.now().toLocalDate());
        leaveRequest.setEndDate(LocalDateTime.now().plusDays(5).toLocalDate());
    }

    @Test
    void createEvent_Success() {
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(event);

        CalendarEvent createdEvent = calendarEventService.createEvent(event);

        assertNotNull(createdEvent);
        assertTrue(createdEvent.isActive());
        verify(calendarEventRepository).save(event);
    }

    @Test
    void updateEvent_Success() {
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(event);

        CalendarEvent updatedEvent = calendarEventService.updateEvent(1L, event);

        assertNotNull(updatedEvent);
        verify(calendarEventRepository).save(event);
    }

    @Test
    void updateEvent_NotFound() {
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> calendarEventService.updateEvent(1L, event));
    }

    @Test
    void deleteEvent_Success() {
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(event);

        calendarEventService.deleteEvent(1L);

        assertFalse(event.isActive());
        verify(calendarEventRepository).save(event);
    }

    @Test
    void getEventById_Success() {
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));

        CalendarEvent foundEvent = calendarEventService.getEventById(1L);

        assertNotNull(foundEvent);
        assertEquals(event.getTitle(), foundEvent.getTitle());
    }

    @Test
    void getEventById_NotFound() {
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> calendarEventService.getEventById(1L));
    }

    @Test
    void getEventsByUser_Success() {
        List<CalendarEvent> events = Arrays.asList(event);
        when(calendarEventRepository.findByUser(user)).thenReturn(events);

        List<CalendarEvent> foundEvents = calendarEventService.getEventsByUser(user);

        assertNotNull(foundEvents);
        assertEquals(1, foundEvents.size());
        verify(calendarEventRepository).findByUser(user);
    }

    @Test
    void getEventsByUserAndDateRange_Success() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        List<CalendarEvent> events = Arrays.asList(event);
        
        when(calendarEventRepository.findByUserAndStartDateTimeBetween(user, start, end))
            .thenReturn(events);

        List<CalendarEvent> foundEvents = calendarEventService.getEventsByUserAndDateRange(user, start, end);

        assertNotNull(foundEvents);
        assertEquals(1, foundEvents.size());
        verify(calendarEventRepository).findByUserAndStartDateTimeBetween(user, start, end);
    }

    @Test
    void getEventsByLeaveRequest_Success() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        List<CalendarEvent> events = Arrays.asList(event);
        when(calendarEventRepository.findByLeaveRequest(leaveRequest)).thenReturn(events);

        List<CalendarEvent> foundEvents = calendarEventService.getEventsByLeaveRequest(1L);

        assertNotNull(foundEvents);
        assertEquals(1, foundEvents.size());
        verify(calendarEventRepository).findByLeaveRequest(leaveRequest);
    }

    @Test
    void createEventFromLeaveRequest_Success() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(event);

        CalendarEvent createdEvent = calendarEventService.createEventFromLeaveRequest(1L);

        assertNotNull(createdEvent);
        assertTrue(createdEvent.isActive());
        verify(calendarEventRepository).save(any(CalendarEvent.class));
    }

    @Test
    void createEventFromLeaveRequest_NotFound() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> calendarEventService.createEventFromLeaveRequest(1L));
    }
} 