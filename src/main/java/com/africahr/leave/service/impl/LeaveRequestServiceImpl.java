package com.africahr.leave.service.impl;

import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.User;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import com.africahr.leave.model.enums.LeaveType;
import com.africahr.leave.repository.LeaveRequestRepository;
import com.africahr.leave.service.HolidayService;
import com.africahr.leave.service.LeaveRequestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final HolidayService holidayService;

    @Override
    @Transactional
    public LeaveRequest createLeaveRequest(User user, LeaveType leaveType, LocalDate startDate, 
                                         LocalDate endDate, String reason) {
        log.info("Creating leave request for user: {}", user.getEmail());
        
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUser(user);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setReason(reason);
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequest updateLeaveRequest(Long id, User user, LeaveType leaveType, 
                                         LocalDate startDate, LocalDate endDate, String reason) {
        log.info("Updating leave request {} for user: {}", id, user.getEmail());
        
        LeaveRequest leaveRequest = getLeaveRequest(id, user);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setReason(reason);
        
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public void deleteLeaveRequest(Long id, User user) {
        log.info("Deleting leave request {} for user: {}", id, user.getEmail());
        
        LeaveRequest leaveRequest = getLeaveRequest(id, user);
        leaveRequestRepository.delete(leaveRequest);
    }

    @Override
    public LeaveRequest getLeaveRequest(Long id, User user) {
        log.info("Fetching leave request {} for user: {}", id, user.getEmail());
        
        return leaveRequestRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByUser(User user) {
        log.info("Fetching all leave requests for user: {}", user.getEmail());
        return leaveRequestRepository.findByUser(user);
    }

    @Override
    public List<LeaveRequest> getTeamLeaveRequests(User manager) {
        log.info("Fetching team leave requests for manager: {}", manager.getEmail());
        return leaveRequestRepository.findByUserManager(manager);
    }

    @Override
    @Transactional
    public LeaveRequest approveLeaveRequest(Long id, User manager, String comments) {
        log.info("Approving leave request {} by manager: {}", id, manager.getEmail());
        
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));
        
        if (!leaveRequest.getUser().getManager().equals(manager)) {
            throw new RuntimeException("Not authorized to approve this request");
        }
        
        leaveRequest.setStatus(LeaveRequestStatus.APPROVED);
        leaveRequest.setComments(comments);
        
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequest rejectLeaveRequest(Long id, User manager, String comments) {
        log.info("Rejecting leave request {} by manager: {}", id, manager.getEmail());
        
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));
        
        if (!leaveRequest.getUser().getManager().equals(manager)) {
            throw new RuntimeException("Not authorized to reject this request");
        }
        
        leaveRequest.setStatus(LeaveRequestStatus.REJECTED);
        leaveRequest.setComments(comments);
        
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequest cancelLeaveRequest(Long id, User user, String comments) {
        log.info("Canceling leave request {} by user: {}", id, user.getEmail());
        
        LeaveRequest leaveRequest = getLeaveRequest(id, user);
        leaveRequest.setStatus(LeaveRequestStatus.CANCELLED);
        leaveRequest.setComments(comments);
        
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveRequestsByUserAndStatus(User user, LeaveRequestStatus status) {
        log.info("Fetching {} leave requests for user: {}", status, user.getEmail());
        return leaveRequestRepository.findByUserAndStatus(user, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveRequestsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching leave requests between {} and {}", startDate, endDate);
        return leaveRequestRepository.findByStartDateBetween(startDate, endDate);
    }
} 