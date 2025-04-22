package com.africahr.leave.service;

import com.africahr.leave.model.entity.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    List<Department> findAll();
    
    Optional<Department> findById(Long id);
    
    Department save(Department department);
    
    void delete(Long id);
    
    List<Department> findActiveDepartments();
} 