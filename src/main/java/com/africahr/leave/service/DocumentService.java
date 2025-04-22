package com.africahr.leave.service;

import com.africahr.leave.model.entity.Document;
import com.africahr.leave.model.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    Document uploadDocument(MultipartFile file, String documentType, String description, User user);
    Document getDocumentById(Long id);
    List<Document> getDocumentsByUser(User user);
    void deleteDocument(Long id);
    Resource loadDocumentAsResource(Long id);
    List<String> getDocumentTypes();
} 