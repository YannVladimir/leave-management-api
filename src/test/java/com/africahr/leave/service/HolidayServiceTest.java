package com.africahr.leave.service;

import com.africahr.leave.model.dto.HolidayDTO;
import com.africahr.leave.model.entity.Holiday;
import com.africahr.leave.repository.HolidayRepository;
import com.africahr.leave.service.impl.HolidayServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayServiceImpl holidayService;

    private Holiday holiday;
    private HolidayDTO holidayDTO;

    @BeforeEach
    void setUp() {
        holiday = Holiday.builder()
                .id(1L)
                .name("New Year")
                .date(LocalDate.of(2024, 1, 1))
                .recurring(true)
                .build();

        holidayDTO = HolidayDTO.builder()
                .name("New Year")
                .date(LocalDate.of(2024, 1, 1))
                .recurring(true)
                .build();
    }

    @Test
    void createHoliday_Success() {
        when(holidayRepository.save(any(Holiday.class))).thenReturn(holiday);

        HolidayDTO result = holidayService.createHoliday(holidayDTO);

        assertNotNull(result);
        assertEquals(holidayDTO.getName(), result.getName());
        assertEquals(holidayDTO.getDate(), result.getDate());
        assertEquals(holidayDTO.isRecurring(), result.isRecurring());
        verify(holidayRepository).save(any(Holiday.class));
    }

    @Test
    void updateHoliday_Success() {
        when(holidayRepository.findById(1L)).thenReturn(Optional.of(holiday));
        when(holidayRepository.save(any(Holiday.class))).thenReturn(holiday);

        HolidayDTO result = holidayService.updateHoliday(1L, holidayDTO);

        assertNotNull(result);
        assertEquals(holidayDTO.getName(), result.getName());
        verify(holidayRepository).findById(1L);
        verify(holidayRepository).save(any(Holiday.class));
    }

    @Test
    void updateHoliday_NotFound() {
        when(holidayRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> holidayService.updateHoliday(1L, holidayDTO));
        verify(holidayRepository).findById(1L);
        verify(holidayRepository, never()).save(any(Holiday.class));
    }

    @Test
    void deleteHoliday_Success() {
        doNothing().when(holidayRepository).deleteById(1L);

        holidayService.deleteHoliday(1L);

        verify(holidayRepository).deleteById(1L);
    }

    @Test
    void getAllHolidays_Success() {
        List<Holiday> holidays = Arrays.asList(holiday);
        when(holidayRepository.findAll()).thenReturn(holidays);

        List<HolidayDTO> result = holidayService.getAllHolidays();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(holidayRepository).findAll();
    }

    @Test
    void getHolidaysBetweenDates_Success() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<Holiday> holidays = Arrays.asList(holiday);
        when(holidayRepository.findByDateBetween(startDate, endDate)).thenReturn(holidays);

        List<HolidayDTO> result = holidayService.getHolidaysBetweenDates(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(holidayRepository).findByDateBetween(startDate, endDate);
    }

    @Test
    void isHoliday_WhenDateIsHoliday_ReturnsTrue() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(holidayRepository.findByDate(date)).thenReturn(Arrays.asList(holiday));

        boolean result = holidayService.isHoliday(date);

        assertTrue(result);
        verify(holidayRepository).findByDate(date);
    }

    @Test
    void isHoliday_WhenDateIsNotHoliday_ReturnsFalse() {
        LocalDate date = LocalDate.of(2024, 1, 2);
        when(holidayRepository.findByDate(date)).thenReturn(Arrays.asList());
        when(holidayRepository.findByRecurringTrue()).thenReturn(Arrays.asList());

        boolean result = holidayService.isHoliday(date);

        assertFalse(result);
        verify(holidayRepository).findByDate(date);
        verify(holidayRepository).findByRecurringTrue();
    }
} 