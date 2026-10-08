package com.simson.shopsphere.service;

import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("File Storage Security & Traversal Tests")
class FileStorageSecurityTest {

    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempUploadDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl();
        ReflectionTestUtils.setField(fileStorageService, "uploadBaseDir", tempUploadDir.toString());
        fileStorageService.init();
    }

    @Test
    void shouldStoreValidImageSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "camera.png", "image/png", "fake-png-content".getBytes()
        );

        String storedUrl = fileStorageService.storeFile(file, "products");

        assertThat(storedUrl).isNotNull();
        assertThat(storedUrl).startsWith("/uploads/products/");
        assertThat(storedUrl).endsWith(".png");
    }

    @Test
    void shouldRejectDirectoryTraversalAttempt() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "../../etc/passwd.png", "image/png", "malicious-content".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "products"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("illegal path characters");
    }

    @Test
    void shouldRejectExecutableFileExtensions() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "script.sh", "application/x-sh", "echo evil".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "products"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid image format");
    }

    @Test
    void shouldRejectMimeTypeMismatch() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.jpg", "application/javascript", "alert(1)".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "products"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("MIME type is invalid");
    }

    @Test
    void shouldRejectOversizedFiles() {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB > 5MB limit
        MockMultipartFile file = new MockMultipartFile(
                "image", "huge.png", "image/png", largeContent
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(file, "products"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("exceeds maximum limit");
    }
}
