package com.africahr.leave.model.enums;

public enum LeaveType {
    ANNUAL("Annual Leave", true, 2.5, 20),
    SICK("Sick Leave", true, 1.25, 10),
    MATERNITY("Maternity Leave", false, 0, 90),
    PATERNITY("Paternity Leave", false, 0, 5),
    BEREAVEMENT("Bereavement Leave", false, 0, 3),
    UNPAID("Unpaid Leave", false, 0, 0);

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

    public String getName() {
        return name;
    }

    public boolean isAutoAccrual() {
        return autoAccrual;
    }

    public double getAccrualRate() {
        return accrualRate;
    }

    public int getMaxDaysPerYear() {
        return maxDaysPerYear;
    }

    public static LeaveType fromName(String name) {
        for (LeaveType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant " + LeaveType.class.getName() + "." + name);
    }
} 