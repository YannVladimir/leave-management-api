package com.africahr.leave.security;

import com.africahr.leave.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OidcUser {
    private final User user;
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return OidcUserInfo.builder()
                .subject(user.getMicrosoftId())
                .email(user.getEmail())
                .givenName(user.getFirstName())
                .familyName(user.getLastName())
                .name(user.getFullName())
                .build();
    }

    @Override
    public OidcIdToken getIdToken() {
        return null; // We don't need to implement this for our use case
    }

    public User getUser() {
        return user;
    }
} 