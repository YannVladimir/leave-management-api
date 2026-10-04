package com.africahr.leave.controller;

import com.africahr.leave.model.User;
import com.africahr.leave.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.findByMicrosoftId(oidcUser.getSubject())
                .orElseGet(() -> userService.findByEmail(oidcUser.getEmail())
                        .orElse(null));

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "firstName", user.getFirstName(),
            "lastName", user.getLastName(),
            "fullName", user.getFullName(),
            "roles", user.getRoles(),
            "picture", oidcUser.getPicture()
        ));
    }
} 