package com.africahr.leave.model.entity;

import com.africahr.leave.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"roles", "manager", "teamMembers", "department"})
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "microsoft_id", unique = true)
    private String microsoftId;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;
    
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<User> teamMembers;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        if (this.fullName == null) {
            this.fullName = this.firstName + " " + this.lastName;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
        if (this.fullName == null) {
            this.fullName = this.firstName + " " + this.lastName;
        }
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getMicrosoftId() { return microsoftId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }
    public boolean isActive() { return active; }
    public Set<UserRole> getRoles() { return roles; }
    public User getManager() { return manager; }
    public List<User> getTeamMembers() { return teamMembers; }
    public Department getDepartment() { return department; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setMicrosoftId(String microsoftId) { this.microsoftId = microsoftId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setActive(boolean active) { this.active = active; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }
    public void setManager(User manager) { this.manager = manager; }
    public void setTeamMembers(List<User> teamMembers) { this.teamMembers = teamMembers; }
    public void setDepartment(Department department) { this.department = department; }
} 