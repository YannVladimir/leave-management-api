package com.africahr.leave.controller;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.Document;
import com.africahr.leave.service.DocumentService;
import com.africahr.leave.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam String documentType,
            @RequestParam(required = false) String description) {
        String userEmail = principal.getAttribute("email");
        log.info("Uploading document of type {} for user: {}", documentType, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(documentService.uploadDocument(file, documentType, description, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        log.info("Downloading document id: {} for user: {}", id, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Document document = documentService.getDocumentById(id);
        if (!document.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Resource resource = documentService.loadDocumentAsResource(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/my-documents")
    public ResponseEntity<List<Document>> getMyDocuments(
            @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        log.info("Fetching documents for user: {}", userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(documentService.getDocumentsByUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        log.info("Deleting document id: {} for user: {}", id, userEmail);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Document document = documentService.getDocumentById(id);
        if (!document.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getDocumentTypes() {
        log.info("Fetching available document types");
        return ResponseEntity.ok(documentService.getDocumentTypes());
    }
} 