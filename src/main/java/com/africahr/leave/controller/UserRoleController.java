package com.africahr.leave.controller;

import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.UserRole;
import com.africahr.leave.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserRoleController {

    private final UserService userService;

    public UserRoleController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody Set<UserRole> newRoles,
            Authentication authentication
    ) {
        // Get the current user
        String currentUserEmail = authentication.getName();
        User currentUser = userService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Prevent self-role modification
        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.badRequest().body("You cannot modify your own roles");
        }

        // Update user roles
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRoles(newRoles);
        userService.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<UserRole>> getAllRoles() {
        return ResponseEntity.ok(Set.of(UserRole.values()));
    }
} 