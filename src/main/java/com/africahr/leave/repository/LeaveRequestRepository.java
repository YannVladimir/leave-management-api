package com.africahr.leave.repository;

import com.africahr.leave.model.entity.LeaveRequest;
import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.Department;
import com.africahr.leave.model.enums.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUser(User user);
    List<LeaveRequest> findByUserAndStatus(User user, LeaveRequestStatus status);
    List<LeaveRequest> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<LeaveRequest> findByUserAndStartDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    Optional<LeaveRequest> findByIdAndUser(Long id, User user);
    
    List<LeaveRequest> findByUserManager(User manager);

    List<LeaveRequest> findByUserAndStartDateBetweenAndStatus(User user, LocalDate startDate, LocalDate endDate, LeaveRequestStatus status);
    
    List<LeaveRequest> findByUserDepartmentAndStartDateBetweenAndStatus(Department department, LocalDate startDate, LocalDate endDate, LeaveRequestStatus status);
    
    List<LeaveRequest> findByStartDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, LeaveRequestStatus status);
} 