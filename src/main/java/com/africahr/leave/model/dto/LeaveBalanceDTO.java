package com.africahr.leave.model.dto;

import com.africahr.leave.model.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {
    private Long id;
    private Long userId;
    private LeaveType leaveType;
    private Double balance;
    private Double totalAccrued;
    private Double usedDays;
    private Double carriedForward;
    private Integer year;
    private LocalDate createdAt;
    private LocalDate lastUpdated;
} 