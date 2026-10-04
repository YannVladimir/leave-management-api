package com.africahr.leave.service.impl;

import com.africahr.leave.event.UserCreatedEvent;
import com.africahr.leave.model.User;
import com.africahr.leave.model.enums.UserRole;
import com.africahr.leave.repository.UserRepository;
import com.africahr.leave.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Optional<User> findByMicrosoftId(String microsoftId) {
        return userRepository.findByMicrosoftId(microsoftId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        log.info("Attempting to save user to database - Email: {}, Provider ID: {}", user.getEmail(), user.getProviderId());
        try {
            User savedUser = userRepository.save(user);
            log.info("Successfully saved user to database - ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
            
            // Publish event for new user creation
            eventPublisher.publishEvent(new UserCreatedEvent(this, savedUser));
            
            return savedUser;
        } catch (Exception e) {
            log.error("Failed to save user to database - Email: {}, Error: {}", user.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<User> findAllActiveUsers() {
        log.info("Finding all active users");
        return userRepository.findAllActiveUsers();
    }

    @Override
    public Optional<User> findById(Long id) {
        log.info("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public User getOrCreateUserFromOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");
        
        log.info("Processing OAuth2 user - Email: {}, ProviderId: {}", email, providerId);

        User user = userRepository.findByMicrosoftId(providerId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            log.info("Creating new user with email: {}", email);
                            Set<UserRole> roles = new HashSet<>();
                            roles.add(UserRole.USER);
                            
                            // Split name into first and last name
                            String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"", ""};
                            String firstName = nameParts[0];
                            String lastName = nameParts.length > 1 ? nameParts[1] : "";
                            
                            return userRepository.save(User.builder()
                                    .email(email)
                                    .name(name)
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .fullName(name)
                                    .microsoftId(providerId)
                                    .active(true)
                                    .roles(roles)
                                    .createdAt(LocalDateTime.now())
                                    .lastUpdated(LocalDateTime.now())
                                    .provider("microsoft")
                                    .providerId(providerId)
                                    .build());
                        }));

        log.info("User processed successfully. ID: {}", user.getId());
        return user;
    }
} 