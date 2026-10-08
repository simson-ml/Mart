package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.CategoryDto;
import com.simson.shopsphere.entity.Category;
import com.simson.shopsphere.exception.DuplicateResourceException;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.CategoryRepository;
import com.simson.shopsphere.service.AuditLogService;
import com.simson.shopsphere.service.CategoryService;
import com.simson.shopsphere.service.FileStorageService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CategoryServiceImpl.class);

    public CategoryServiceImpl(CategoryRepository categoryRepository, FileStorageService fileStorageService, AuditLogService auditLogService) {
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogService = auditLogService;
    }


    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategoriesAdmin() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    @Override
    @Transactional
    public Category createCategory(CategoryDto dto, String adminEmail) {
        String trimmedName = dto.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("A category with name '" + trimmedName + "' already exists.");
        }

        String slug = generateSlug(trimmedName);
        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        String imageUrl = dto.getImageUrl();
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            imageUrl = fileStorageService.storeFile(dto.getImageFile(), "categories");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            imageUrl = fileStorageService.getDefaultCategoryImage();
        }

        Category category = Category.builder()
                .name(trimmedName)
                .slug(slug)
                .description(dto.getDescription())
                .imageUrl(imageUrl)
                .active(dto.isActive())
                .build();

        Category saved = categoryRepository.save(category);
        auditLogService.log(adminEmail, "CREATE_CATEGORY", "Category", String.valueOf(saved.getId()),
                "Created category: " + saved.getName(), "127.0.0.1");

        return saved;
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryDto dto, String adminEmail) {
        Category category = getCategoryById(id);
        String trimmedName = dto.getName().trim();

        if (!category.getName().equalsIgnoreCase(trimmedName) && categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("A category with name '" + trimmedName + "' already exists.");
        }

        category.setName(trimmedName);
        category.setDescription(dto.getDescription());
        category.setActive(dto.isActive());

        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            String newImage = fileStorageService.storeFile(dto.getImageFile(), "categories");
            if (newImage != null) {
                category.setImageUrl(newImage);
            }
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            category.setImageUrl(dto.getImageUrl().trim());
        }

        Category updated = categoryRepository.save(category);
        auditLogService.log(adminEmail, "UPDATE_CATEGORY", "Category", String.valueOf(updated.getId()),
                "Updated category: " + updated.getName(), "127.0.0.1");

        return updated;
    }

    @Override
    @Transactional
    public void toggleCategoryStatus(Long id, String adminEmail) {
        Category category = getCategoryById(id);
        category.setActive(!category.isActive());
        categoryRepository.save(category);
        auditLogService.log(adminEmail, "TOGGLE_CATEGORY_STATUS", "Category", String.valueOf(category.getId()),
                "Category " + category.getName() + " active=" + category.isActive(), "127.0.0.1");
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCategoryCount() {
        return categoryRepository.count();
    }
}
