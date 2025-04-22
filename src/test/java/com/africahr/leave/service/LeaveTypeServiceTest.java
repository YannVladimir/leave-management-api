package com.africahr.leave.service;

import com.africahr.leave.model.entity.LeaveType;
import com.africahr.leave.repository.LeaveTypeRepository;
import com.africahr.leave.service.impl.LeaveTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LeaveTypeServiceTest {

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @InjectMocks
    private LeaveTypeServiceImpl leaveTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLeaveType() {
        // Given
        LeaveType leaveType = new LeaveType();
        leaveType.setName("Annual Leave");
        leaveType.setMaxDaysPerYear(21);
        leaveType.setActive(true);

        when(leaveTypeRepository.save(any(LeaveType.class))).thenReturn(leaveType);

        // When
        LeaveType result = leaveTypeService.createLeaveType(leaveType);

        // Then
        assertNotNull(result);
        assertEquals("Annual Leave", result.getName());
        assertEquals(21, result.getMaxDaysPerYear());
        assertTrue(result.isActive());
        verify(leaveTypeRepository, times(1)).save(any(LeaveType.class));
    }

    @Test
    void testGetAllLeaveTypes() {
        // Given
        LeaveType leaveType1 = new LeaveType();
        leaveType1.setName("Annual Leave");
        LeaveType leaveType2 = new LeaveType();
        leaveType2.setName("Sick Leave");

        when(leaveTypeRepository.findAll()).thenReturn(Arrays.asList(leaveType1, leaveType2));

        // When
        List<LeaveType> result = leaveTypeService.getAllLeaveTypes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(leaveTypeRepository, times(1)).findAll();
    }

    @Test
    void testGetLeaveTypeById() {
        // Given
        Long id = 1L;
        LeaveType leaveType = new LeaveType();
        leaveType.setId(id);
        leaveType.setName("Annual Leave");

        when(leaveTypeRepository.findById(id)).thenReturn(Optional.of(leaveType));

        // When
        LeaveType result = leaveTypeService.getLeaveTypeById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Annual Leave", result.getName());
        verify(leaveTypeRepository, times(1)).findById(id);
    }
} 