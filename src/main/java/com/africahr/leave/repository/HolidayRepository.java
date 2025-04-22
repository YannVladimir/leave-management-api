package com.africahr.leave.repository;

import com.africahr.leave.model.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Holiday> findByDate(LocalDate date);
    List<Holiday> findByRecurringTrue();
} 