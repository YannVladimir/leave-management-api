package com.africahr.leave.security;

import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.UserRole;
import com.africahr.leave.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
        log.info("CustomOAuth2UserService initialized with userService: {}", userService);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Starting loadUser process for OAuth2UserRequest: {}", userRequest);
        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            log.info("Successfully loaded OAuth2User from parent service: {}", oauth2User);

            Map<String, Object> attributes = oauth2User.getAttributes();
            log.info("OAuth2User attributes: {}", attributes);

            String microsoftId = attributes.get("sub").toString();
            String email = attributes.get("email").toString();
            String firstName = attributes.get("given_name").toString();
            String lastName = attributes.get("family_name").toString();
            String fullName = firstName + " " + lastName;

            log.info("Extracted user details - microsoftId: {}, email: {}, firstName: {}, lastName: {}", 
                    microsoftId, email, firstName, lastName);

            User user = userService.findByMicrosoftId(microsoftId)
                    .orElseGet(() -> userService.findByEmail(email)
                            .orElseGet(() -> {
                                log.info("No existing user found, creating new user with email: {}", email);
                                User newUser = new User();
                                newUser.setEmail(email);
                                newUser.setFirstName(firstName);
                                newUser.setLastName(lastName);
                                newUser.setFullName(fullName);
                                newUser.setMicrosoftId(microsoftId);
                                newUser.setActive(true);
                                
                                // Set default role as USER
                                Set<UserRole> roles = new HashSet<>();
                                roles.add(UserRole.USER);
                                newUser.setRoles(roles);
                                
                                log.info("Attempting to save new user: {}", newUser);
                                try {
                                    User savedUser = userService.save(newUser);
                                    log.info("Successfully saved new user with ID: {}", savedUser.getId());
                                    return savedUser;
                                } catch (Exception e) {
                                    log.error("Failed to save new user: {}", e.getMessage(), e);
                                    throw new OAuth2AuthenticationException(new OAuth2Error("user_creation_failed", 
                                            "Failed to create user: " + e.getMessage(), null), e);
                                }
                            }));

            log.info("Successfully processed OAuth2User for email: {}", email);
            return new CustomOAuth2User(user, attributes);
        } catch (Exception e) {
            log.error("Error processing OAuth2User: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException(new OAuth2Error("oauth2_processing_error", 
                    "Error processing OAuth2User: " + e.getMessage(), null), e);
        }
    }
} 