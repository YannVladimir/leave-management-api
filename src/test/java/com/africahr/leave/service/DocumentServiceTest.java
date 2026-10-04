package com.africahr.leave.service;

import com.africahr.leave.model.User;
import com.africahr.leave.model.entity.Document;
import com.africahr.leave.repository.DocumentRepository;
import com.africahr.leave.service.impl.DocumentServiceImpl;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private User user;
    private Document document;
    private MultipartFile file;

    @BeforeEach
    void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setActive(true);

        document = Document.builder()
            .id(1L)
            .fileName("test.pdf")
            .filePath("/uploads/test.pdf")
            .contentType("application/pdf")
            .documentType("LEAVE_REQUEST")
            .description("Test Document")
            .user(user)
            .build();

        file = new MockMultipartFile(
            "test.pdf",
            "test.pdf",
            "application/pdf",
            "Test content".getBytes()
        );

        // Initialize file storage location
        Path tempDir = Files.createTempDirectory("test-uploads");
        documentService = new DocumentServiceImpl(documentRepository);
        Field field = DocumentServiceImpl.class.getDeclaredField("fileStorageLocation");
        field.setAccessible(true);
        field.set(documentService, tempDir);
    }

    @AfterEach
    void tearDown() throws IOException, IllegalAccessException {
        // Clean up test files
        Path tempDir = ((Path) FieldUtils.readField(documentService, "fileStorageLocation", true));
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore cleanup errors
                    }
                });
        }
    }

    @Test
    void uploadDocument_Success() throws IOException {
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document uploadedDocument = documentService.uploadDocument(
            file, "LEAVE_REQUEST", "Test Document", user
        );

        assertNotNull(uploadedDocument);
        assertEquals(document.getFileName(), uploadedDocument.getFileName());
        assertEquals(document.getDocumentType(), uploadedDocument.getDocumentType());
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void uploadDocument_InvalidFile() {
        MultipartFile invalidFile = new MockMultipartFile(
            "test.txt",
            "test.txt",
            "text/plain",
            new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> 
            documentService.uploadDocument(invalidFile, "LEAVE_REQUEST", "Test Document", user)
        );
    }

    @Test
    void getDocumentById_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        Document foundDocument = documentService.getDocumentById(1L);

        assertNotNull(foundDocument);
        assertEquals(document.getId(), foundDocument.getId());
        assertEquals(document.getFileName(), foundDocument.getFileName());
        verify(documentRepository).findById(1L);
    }

    @Test
    void getDocumentById_NotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            documentService.getDocumentById(1L)
        );
    }

    @Test
    void getDocumentsByUser_Success() {
        List<Document> documents = Arrays.asList(document);
        when(documentRepository.findByUser(user)).thenReturn(documents);

        List<Document> foundDocuments = documentService.getDocumentsByUser(user);

        assertNotNull(foundDocuments);
        assertEquals(1, foundDocuments.size());
        assertEquals(document.getId(), foundDocuments.get(0).getId());
        verify(documentRepository).findByUser(user);
    }

    @Test
    void deleteDocument_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        doNothing().when(documentRepository).delete(document);

        documentService.deleteDocument(1L);

        verify(documentRepository).findById(1L);
        verify(documentRepository).delete(document);
    }

    @Test
    void deleteDocument_NotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            documentService.deleteDocument(1L)
        );
    }

    @Test
    void loadDocumentAsResource_Success() throws IOException {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        
        Path filePath = Paths.get(document.getFilePath());
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, "Test content".getBytes());

        Resource resource = documentService.loadDocumentAsResource(1L);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(document.getFileName(), resource.getFilename());
    }

    @Test
    void loadDocumentAsResource_NotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            documentService.loadDocumentAsResource(1L)
        );
    }

    @Test
    void getDocumentTypes_Success() {
        List<String> documentTypes = documentService.getDocumentTypes();

        assertNotNull(documentTypes);
        assertTrue(documentTypes.contains("LEAVE_REQUEST"));
        assertTrue(documentTypes.contains("MEDICAL_CERTIFICATE"));
        assertTrue(documentTypes.contains("OTHER"));
    }
} 