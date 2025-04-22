package com.africahr.leave.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "leave_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "max_days_per_year", nullable = false)
    private Integer maxDaysPerYear;

    @Column(name = "auto_accrual", nullable = false)
    private boolean autoAccrual;

    @Column(name = "accrual_rate")
    private Double accrualRate; // Days per month

    @Column(name = "min_service_months")
    private Integer minServiceMonths;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "leaveType", cascade = CascadeType.ALL)
    private Set<LeaveRequest> leaveRequests;

    @OneToMany(mappedBy = "leaveType", cascade = CascadeType.ALL)
    private Set<LeaveBalance> leaveBalances;

    // Custom getters and setters for Lombok compatibility
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getMaxDaysPerYear() { return maxDaysPerYear; }
    public void setMaxDaysPerYear(Integer maxDaysPerYear) { this.maxDaysPerYear = maxDaysPerYear; }
    public boolean isAutoAccrual() { return autoAccrual; }
    public void setAutoAccrual(boolean autoAccrual) { this.autoAccrual = autoAccrual; }
    public Double getAccrualRate() { return accrualRate; }
    public void setAccrualRate(Double accrualRate) { this.accrualRate = accrualRate; }
    public Integer getMinServiceMonths() { return minServiceMonths; }
    public void setMinServiceMonths(Integer minServiceMonths) { this.minServiceMonths = minServiceMonths; }
    public boolean isRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(boolean requiresApproval) { this.requiresApproval = requiresApproval; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
} 