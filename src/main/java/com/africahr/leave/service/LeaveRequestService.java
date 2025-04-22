package com.africahr.leave.service;

import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestService {
    LeaveRequest createLeaveRequest(User user, LeaveType leaveType, LocalDate startDate, 
                                  LocalDate endDate, String reason);
    
    LeaveRequest updateLeaveRequest(Long id, User user, LeaveType leaveType, 
                                  LocalDate startDate, LocalDate endDate, String reason);
    
    void deleteLeaveRequest(Long id, User user);
    
    LeaveRequest getLeaveRequest(Long id, User user);
    
    List<LeaveRequest> getLeaveRequestsByUser(User user);
    
    List<LeaveRequest> getTeamLeaveRequests(User manager);
    
    LeaveRequest approveLeaveRequest(Long id, User manager, String comments);
    
    LeaveRequest rejectLeaveRequest(Long id, User manager, String comments);
    
    LeaveRequest cancelLeaveRequest(Long id, User user, String comments);
    
    List<LeaveRequest> getLeaveRequestsByUserAndStatus(User user, LeaveRequestStatus status);
    
    List<LeaveRequest> getLeaveRequestsByDateRange(LocalDate startDate, LocalDate endDate);
} 