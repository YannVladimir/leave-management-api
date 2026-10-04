package com.africahr.leave.service.impl;

import com.africahr.leave.model.entity.Document;
import com.africahr.leave.model.User;
import com.africahr.leave.repository.DocumentRepository;
import com.africahr.leave.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private Path fileStorageLocation;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    @Transactional
    public Document uploadDocument(MultipartFile file, String documentType, String description, User user) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            if (file.isEmpty()) {
                throw new IllegalArgumentException("Cannot upload empty file");
            }

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Document document = new Document();
            document.setUser(user);
            document.setFileName(fileName);
            document.setFilePath(targetLocation.toString());
            document.setContentType(file.getContentType());
            document.setDocumentType(documentType);
            document.setDescription(description);

            return documentRepository.save(document);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));
    }

    @Override
    public List<Document> getDocumentsByUser(User user) {
        return documentRepository.findByUser(user);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = getDocumentById(id);
        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
            documentRepository.delete(document);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + document.getFileName(), ex);
        }
    }

    @Override
    public Resource loadDocumentAsResource(Long id) {
        Document document = getDocumentById(id);
        try {
            Path filePath = Paths.get(document.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + document.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + document.getFileName(), ex);
        }
    }

    @Override
    public List<String> getDocumentTypes() {
        return Arrays.asList("LEAVE_REQUEST", "MEDICAL_CERTIFICATE", "OTHER");
    }
} 