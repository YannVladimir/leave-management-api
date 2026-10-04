package com.africahr.leave.security;

import com.africahr.leave.model.User;
import com.africahr.leave.model.enums.UserRole;
import com.africahr.leave.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
        log.info("CustomOAuth2UserService initialized with userService: {}", userService);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Loading user from OAuth2 request");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");
        
        log.info("Processing OAuth2 user with email: {}", email);
        
        // Find existing user or create new one
        User user = userService.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Creating new user for email: {}", email);
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProviderId(providerId);
                    newUser.setProvider("microsoft");
                    newUser.setActive(true);
                    newUser.setFullName(name);
                    newUser.setFirstName(name.split(" ")[0]);
                    newUser.setLastName(name.split(" ").length > 1 ? name.split(" ")[1] : "");
                    newUser.setCreatedAt(java.time.LocalDateTime.now());
                    newUser.setLastUpdated(java.time.LocalDateTime.now());
                    
                    // Set default role
                    Set<UserRole> roles = new HashSet<>();
                    roles.add(UserRole.USER);
                    newUser.setRoles(roles);
                    
                    // Save the user first
                    User savedUser = userService.save(newUser);
                    log.info("Created new user with ID: {}", savedUser.getId());
                    return savedUser;
                });
        
        log.info("Successfully processed OAuth2 user: {}", user.getEmail());
        return oAuth2User;
    }
} 