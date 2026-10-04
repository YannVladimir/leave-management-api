package com.africahr.leave.dto;

import com.africahr.leave.model.entity.LeaveBalance;
import com.africahr.leave.model.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeaveBalanceDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private LeaveType leaveType;
    private double balance;
    private double totalAccrued;
    private double usedDays;
    private double carriedForward;
    private int year;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

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
        dto.setCreatedAt(leaveBalance.getCreatedAt());
        dto.setLastUpdated(leaveBalance.getLastUpdated());
        return dto;
    }
} 