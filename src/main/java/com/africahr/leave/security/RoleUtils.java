package com.africahr.leave.security;

import com.africahr.leave.model.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleUtils {

    public static Set<UserRole> getRolesFromAuthentication(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
    }

    public static boolean hasRole(Authentication authentication, UserRole role) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.name()));
    }

    public static boolean hasHigherOrEqualRole(Authentication authentication, UserRole requiredRole) {
        Set<UserRole> userRoles = getRolesFromAuthentication(authentication);
        return userRoles.stream()
                .anyMatch(role -> role.hasHigherOrEqualPrivilege(requiredRole));
    }
} 