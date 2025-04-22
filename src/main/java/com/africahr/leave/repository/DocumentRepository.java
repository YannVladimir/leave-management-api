package com.africahr.leave.repository;

import com.africahr.leave.model.entity.Document;
import com.africahr.leave.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUser(User user);
} 