package com.africahr.leave.dto;

import com.africahr.leave.model.entity.LeaveBalance;
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
    private String userFullName;
    private LeaveType leaveType;
    private Double balance;
    private Double totalAccrued;
    private Double usedDays;
    private Double carriedForward;
    private Integer year;
    private LocalDate lastUpdated;

    public static LeaveBalanceDTO fromEntity(LeaveBalance leaveBalance) {
        LeaveBalanceDTO dto = new LeaveBalanceDTO();
        dto.setId(leaveBalance.getId());
        dto.setUserId(leaveBalance.getUser().getId());
        dto.setUserFullName(leaveBalance.getUser().getFullName());
        dto.setLeaveType(leaveBalance.getLeaveType());
        dto.setBalance(leaveBalance.getBalance());
        dto.setTotalAccrued(leaveBalance.getTotalAccrued());
        dto.setUsedDays(leaveBalance.getUsedDays());
        dto.setCarriedForward(leaveBalance.getCarriedForward());
        dto.setYear(leaveBalance.getYear());
        dto.setLastUpdated(leaveBalance.getLastUpdated());
        return dto;
    }
} 