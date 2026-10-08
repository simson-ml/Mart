package com.simson.shopsphere.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDirectory);
    boolean deleteFile(String filePath);
    String getDefaultProductImage();
    String getDefaultCategoryImage();
}
