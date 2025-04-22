package com.africahr.leave.config;

import com.africahr.leave.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class TestConfig {

    @Bean
    public JwtService jwtService() {
        return new JwtService() {
            @Override
            public String extractUsername(String token) {
                return null;
            }

            @Override
            public List<String> extractRoles(String token) {
                return List.of();
            }

            @Override
            public String generateToken(UserDetails userDetails) {
                return "mock-token";
            }

            @Override
            public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
                return "mock-token";
            }

            @Override
            public boolean isTokenValid(String token, UserDetails userDetails) {
                return true;
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().permitAll()
            )
            .httpBasic();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
            User.withUsername("admin")
                .password("{noop}password")
                .roles("ADMIN")
                .build(),
            User.withUsername("manager")
                .password("{noop}password")
                .roles("MANAGER")
                .build(),
            User.withUsername("user")
                .password("{noop}password")
                .roles("USER")
                .build()
        );
    }
} 