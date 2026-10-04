package com.africahr.leave.repository;

import com.africahr.leave.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.microsoftId = :microsoftId")
    Optional<User> findByMicrosoftId(@Param("microsoftId") String microsoftId);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findAllActiveUsers();

    Optional<User> findByProviderId(String providerId);
} 