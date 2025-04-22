package com.africahr.leave.service;

import com.africahr.leave.model.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByMicrosoftId(String microsoftId);
    Optional<User> findByEmail(String email);
    User save(User user);
    List<User> findAllActiveUsers();
    Optional<User> findById(Long id);
} 