package com.africahr.leave.model.enums;

import lombok.Getter;

@Getter
public enum LeaveType {
    ANNUAL("Annual Leave", true, 2.5, 30),
    SICK("Sick Leave", true, 1.25, 15),
    MATERNITY("Maternity Leave", false, 0, 90),
    PATERNITY("Paternity Leave", false, 0, 14),
    COMPASSIONATE("Compassionate Leave", false, 0, 0),
    UNPAID("Unpaid Leave", false, 0, 0),
    STUDY("Study Leave", false, 0, 0);

    private final String name;
    private final boolean autoAccrual;
    private final double accrualRate;
    private final int maxDaysPerYear;

    LeaveType(String name, boolean autoAccrual, double accrualRate, int maxDaysPerYear) {
        this.name = name;
        this.autoAccrual = autoAccrual;
        this.accrualRate = accrualRate;
        this.maxDaysPerYear = maxDaysPerYear;
    }
} 