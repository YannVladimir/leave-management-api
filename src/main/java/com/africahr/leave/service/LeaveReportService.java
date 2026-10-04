package com.africahr.leave.service;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.Department;
import com.africahr.leave.model.enums.LeaveType;

import java.time.LocalDate;
import java.util.Map;

public interface LeaveReportService {
    /**
     * Counts the number of leave days taken by a user in a given period
     * @param user The user to analyze
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return Map of leave types to number of days taken
     */
    Map<LeaveType, Double> getUserLeaveDays(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Generates department-level leave summaries
     * @param department The department to analyze
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return Map of leave types to total days taken by all department members
     */
    Map<LeaveType, Double> getDepartmentLeaveSummary(Department department, LocalDate startDate, LocalDate endDate);

    /**
     * Gets the most common leave types in a given period
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return Map of leave types to number of occurrences
     */
    Map<LeaveType, Long> getMostCommonLeaveTypes(LocalDate startDate, LocalDate endDate);

    /**
     * Gets the busiest leave months in a given period
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return Map of month numbers to number of leave days taken
     */
    Map<Integer, Double> getBusiestLeaveMonths(LocalDate startDate, LocalDate endDate);
} 