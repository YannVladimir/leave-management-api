package com.africahr.leave.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.africahr.leave.model.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;
} 