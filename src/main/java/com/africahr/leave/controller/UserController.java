package com.africahr.leave.controller;

import com.africahr.leave.model.User;
import com.africahr.leave.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.ok(Map.of(
                "authenticated", false
            ));
        }

        User user = userService.getOrCreateUserFromOAuth2User(principal);
        
        return ResponseEntity.ok(Map.of(
            "authenticated", true,
            "name", user.getName(),
            "email", user.getEmail(),
            "picture", user.getProfilePicture()
        ));
    }
} 