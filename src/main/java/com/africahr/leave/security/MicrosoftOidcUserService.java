package com.africahr.leave.security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.HashSet;

public class MicrosoftOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);
        
        // Get the claims from the id_token
        Map<String, Object> claims = user.getClaims();
        
        // Extract user information from claims
        String email = (String) claims.get("email");
        String name = (String) claims.get("name");
        String preferredUsername = (String) claims.get("preferred_username");
        
        // Use email as the name attribute if name is not available
        String nameAttribute = StringUtils.hasText(name) ? name : 
                             (StringUtils.hasText(preferredUsername) ? preferredUsername : email);
        
        // Create a new DefaultOidcUser with the extracted information
        return new DefaultOidcUser(
            user.getAuthorities(),
            user.getIdToken(),
            user.getUserInfo(),
            "email" // Use email as the name attribute key
        );
    }
} 