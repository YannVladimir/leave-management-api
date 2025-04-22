package com.africahr.leave.service;

import com.africahr.leave.model.dto.HolidayDTO;
import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    HolidayDTO createHoliday(HolidayDTO holidayDTO);
    HolidayDTO updateHoliday(Long id, HolidayDTO holidayDTO);
    void deleteHoliday(Long id);
    List<HolidayDTO> getAllHolidays();
    List<HolidayDTO> getHolidaysBetweenDates(LocalDate startDate, LocalDate endDate);
    boolean isHoliday(LocalDate date);
} 