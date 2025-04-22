package com.africahr.leave.security;

import com.africahr.leave.model.entity.User;
import com.africahr.leave.model.enums.UserRole;
import com.africahr.leave.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final UserService userService;

    public OAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
        setDefaultTargetUrl("http://localhost:3000/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2/OIDC authentication success. Processing user details...");
        
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            try {
                Map<String, Object> attributes = oidcUser.getAttributes();
                String microsoftId = attributes.get("sub").toString();
                String email = attributes.get("email").toString();
                String firstName = attributes.get("given_name").toString();
                String lastName = attributes.get("family_name").toString();

                log.info("Processing user - Email: {}, MicrosoftId: {}", email, microsoftId);

                User user = userService.findByMicrosoftId(microsoftId)
                        .orElseGet(() -> userService.findByEmail(email)
                                .orElseGet(() -> {
                                    log.info("Creating new user with email: {}", email);
                                    User newUser = new User();
                                    newUser.setEmail(email);
                                    newUser.setFirstName(firstName);
                                    newUser.setLastName(lastName);
                                    newUser.setMicrosoftId(microsoftId);
                                    newUser.setActive(true);

                                    Set<UserRole> roles = new HashSet<>();
                                    roles.add(UserRole.USER);
                                    newUser.setRoles(roles);

                                    log.info("Saving new user to database");
                                    return userService.save(newUser);
                                }));

                log.info("User processed successfully. ID: {}", user.getId());
            } catch (Exception e) {
                log.error("Error processing user after authentication: {}", e.getMessage(), e);
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
} 