package com.africahr.leave.service.impl;

import com.africahr.leave.model.dto.HolidayDTO;
import com.africahr.leave.model.entity.Holiday;
import com.africahr.leave.repository.HolidayRepository;
import com.africahr.leave.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {
    private final HolidayRepository holidayRepository;

    @Override
    @Transactional
    public HolidayDTO createHoliday(HolidayDTO holidayDTO) {
        Holiday holiday = Holiday.builder()
                .name(holidayDTO.getName())
                .date(holidayDTO.getDate())
                .recurring(holidayDTO.isRecurring())
                .build();
        
        Holiday savedHoliday = holidayRepository.save(holiday);
        return mapToDTO(savedHoliday);
    }

    @Override
    @Transactional
    public HolidayDTO updateHoliday(Long id, HolidayDTO holidayDTO) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found with id: " + id));
        
        holiday.setName(holidayDTO.getName());
        holiday.setDate(holidayDTO.getDate());
        holiday.setRecurring(holidayDTO.isRecurring());
        
        Holiday updatedHoliday = holidayRepository.save(holiday);
        return mapToDTO(updatedHoliday);
    }

    @Override
    @Transactional
    public void deleteHoliday(Long id) {
        holidayRepository.deleteById(id);
    }

    @Override
    public List<HolidayDTO> getAllHolidays() {
        return holidayRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayDTO> getHolidaysBetweenDates(LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        // Check for specific date
        if (!holidayRepository.findByDate(date).isEmpty()) {
            return true;
        }
        
        // Check for recurring holidays
        return holidayRepository.findByRecurringTrue().stream()
                .anyMatch(holiday -> {
                    LocalDate holidayDate = holiday.getDate();
                    return holidayDate.getMonth() == date.getMonth() && 
                           holidayDate.getDayOfMonth() == date.getDayOfMonth();
                });
    }

    private HolidayDTO mapToDTO(Holiday holiday) {
        return HolidayDTO.builder()
                .id(holiday.getId())
                .name(holiday.getName())
                .date(holiday.getDate())
                .recurring(holiday.isRecurring())
                .build();
    }
} 