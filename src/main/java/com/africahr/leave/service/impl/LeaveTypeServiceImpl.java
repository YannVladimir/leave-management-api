package com.africahr.leave.service.impl;

import com.africahr.leave.model.entity.LeaveType;
import com.africahr.leave.repository.LeaveTypeRepository;
import com.africahr.leave.service.LeaveTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeServiceImpl(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Override
    @Transactional
    public LeaveType createLeaveType(LeaveType leaveType) {
        if (leaveTypeRepository.existsByName(leaveType.getName())) {
            throw new IllegalArgumentException("Leave type with name " + leaveType.getName() + " already exists");
        }
        return leaveTypeRepository.save(leaveType);
    }

    @Override
    @Transactional
    public LeaveType updateLeaveType(Long id, LeaveType leaveType) {
        LeaveType existingLeaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found with id: " + id));

        if (!existingLeaveType.getName().equals(leaveType.getName()) && 
            leaveTypeRepository.existsByName(leaveType.getName())) {
            throw new IllegalArgumentException("Leave type with name " + leaveType.getName() + " already exists");
        }

        existingLeaveType.setName(leaveType.getName());
        existingLeaveType.setDescription(leaveType.getDescription());
        existingLeaveType.setMaxDaysPerYear(leaveType.getMaxDaysPerYear());
        existingLeaveType.setAutoAccrual(leaveType.isAutoAccrual());
        existingLeaveType.setAccrualRate(leaveType.getAccrualRate());
        existingLeaveType.setMinServiceMonths(leaveType.getMinServiceMonths());
        existingLeaveType.setRequiresApproval(leaveType.isRequiresApproval());
        existingLeaveType.setActive(leaveType.isActive());

        return leaveTypeRepository.save(existingLeaveType);
    }

    @Override
    @Transactional
    public void deleteLeaveType(Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("Leave type not found with id: " + id);
        }
        leaveTypeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveType getLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveType getLeaveTypeByName(String name) {
        return leaveTypeRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveType> getActiveLeaveTypes() {
        return leaveTypeRepository.findAll().stream()
                .filter(LeaveType::isActive)
                .toList();
    }
} 