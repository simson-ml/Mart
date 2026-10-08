package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.CategoryDto;
import com.simson.shopsphere.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllActiveCategories();
    List<Category> getAllCategoriesAdmin();
    Category getCategoryById(Long id);
    Category getCategoryBySlug(String slug);
    Category createCategory(CategoryDto dto, String adminEmail);
    Category updateCategory(Long id, CategoryDto dto, String adminEmail);
    void toggleCategoryStatus(Long id, String adminEmail);
    long getTotalCategoryCount();
}
