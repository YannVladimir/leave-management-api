package com.africahr.leave.service.impl;

import com.africahr.leave.event.UserCreatedEvent;
import com.africahr.leave.model.entity.User;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public User save(User user) {
        log.info("Attempting to save user to database - Email: {}, Microsoft ID: {}", user.getEmail(), user.getMicrosoftId());
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
    @Transactional(readOnly = true)
    public Optional<User> findByMicrosoftId(String microsoftId) {
        log.info("Searching for user by Microsoft ID: {}", microsoftId);
        Optional<User> user = userRepository.findByMicrosoftId(microsoftId);
        if (user.isEmpty()) {
            log.info("No user found with Microsoft ID: {}", microsoftId);
        } else {
            log.info("Found user by Microsoft ID - User ID: {}, Email: {}", 
                    user.get().getId(), user.get().getEmail());
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.info("Searching for user by Email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.info("No user found with Email: {}", email);
        } else {
            log.info("Found user by Email - User ID: {}, Email: {}", 
                    user.get().getId(), user.get().getEmail());
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllActiveUsers() {
        log.info("Finding all active users");
        return userRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        log.info("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }
} 