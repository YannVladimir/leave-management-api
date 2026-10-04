package com.africahr.leave.security;

import com.africahr.leave.model.User;
import com.africahr.leave.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final UserService userService;

    public OAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
        setDefaultTargetUrl("http://localhost:3001/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            logger.info("OAuth2 authentication successful");
            
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            logger.debug("OAuth2 user attributes: {}", attributes);
            
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String providerId = (String) attributes.get("sub");
            
            if (email == null || name == null || providerId == null) {
                logger.error("Missing required user attributes: email={}, name={}, providerId={}", 
                    email, name, providerId);
                throw new ServletException("Missing required user attributes");
            }
            
            User user = userService.getOrCreateUserFromOAuth2User(oauth2User);
            logger.info("User processed successfully: {}", user.getEmail());
            
            super.onAuthenticationSuccess(request, response, authentication);
        } catch (Exception e) {
            logger.error("Error during OAuth2 authentication success handling", e);
            response.sendRedirect("/error-500?error=" + e.getMessage());
        }
    }
} 