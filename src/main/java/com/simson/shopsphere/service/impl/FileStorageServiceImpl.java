package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Value("${app.upload.dir:./uploads}")
    private String uploadBaseDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp"
    );

    @PostConstruct
    public void init() {
        try {
            Path root = Paths.get(uploadBaseDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            Files.createDirectories(root.resolve("products"));
            Files.createDirectories(root.resolve("categories"));
            log.info("File upload directories initialized securely at: {}", root.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not initialize upload storage folder", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Size check (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("Image file size exceeds maximum limit of 5MB.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");

        // Path traversal check
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BadRequestException("Invalid filename with illegal path characters: " + originalFilename);
        }

        // Extension & MIME check
        String extension = "";
        int extIndex = originalFilename.lastIndexOf('.');
        if (extIndex > 0) {
            extension = originalFilename.substring(extIndex + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Invalid image format. Supported formats: JPG, JPEG, PNG, WEBP.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Uploaded file MIME type is invalid: " + contentType);
        }

        try {
            String newFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "." + extension;
            Path targetDir = Paths.get(uploadBaseDir).resolve(subDirectory);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path targetLocation = targetDir.resolve(newFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Securely stored uploaded file: {} as {}", originalFilename, newFilename);
            return "/uploads/" + subDirectory + "/" + newFilename;
        } catch (IOException e) {
            log.error("Failed to store file: " + originalFilename, e);
            throw new BadRequestException("Failed to store image file: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return false;
        }
        try {
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = Paths.get(uploadBaseDir).resolve(relativePath);
            boolean deleted = Files.deleteIfExists(filePath);
            log.info("Deleted file: {} (success: {})", filePath, deleted);
            return deleted;
        } catch (IOException e) {
            log.warn("Could not delete file: {}", fileUrl);
            return false;
        }
    }

    @Override
    public String getDefaultProductImage() {
        return "/images/products/default.svg";
    }

    @Override
    public String getDefaultCategoryImage() {
        return "/images/categories/default.svg";
    }
}
