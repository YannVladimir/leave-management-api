package com.africahr.leave.service;

import com.africahr.leave.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByMicrosoftId(String microsoftId);
    Optional<User> findByEmail(String email);
    User save(User user);
    List<User> findAllActiveUsers();
    Optional<User> findById(Long id);
    User getOrCreateUserFromOAuth2User(OAuth2User oauth2User);
} 