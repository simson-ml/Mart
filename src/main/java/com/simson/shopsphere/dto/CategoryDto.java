package com.simson.shopsphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String imageUrl;
    private String iconName;
    private boolean active = true;
    private int displayOrder;
    private long productCount;
    private MultipartFile imageFile;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String description, String imageUrl, String iconName, boolean active, int displayOrder, long productCount, MultipartFile imageFile) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.iconName = iconName;
        this.active = active;
        this.displayOrder = displayOrder;
        this.productCount = productCount;
        this.imageFile = imageFile;
    }

    public static CategoryDtoBuilder builder() {
        return new CategoryDtoBuilder();
    }

    public static class CategoryDtoBuilder {
        private Long id;
        private String name;
        private String description;
        private String imageUrl;
        private String iconName;
        private boolean active = true;
        private int displayOrder;
        private long productCount;
        private MultipartFile imageFile;

        public CategoryDtoBuilder id(Long id) { this.id = id; return this; }
        public CategoryDtoBuilder name(String name) { this.name = name; return this; }
        public CategoryDtoBuilder description(String description) { this.description = description; return this; }
        public CategoryDtoBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public CategoryDtoBuilder iconName(String iconName) { this.iconName = iconName; return this; }
        public CategoryDtoBuilder active(boolean active) { this.active = active; return this; }
        public CategoryDtoBuilder displayOrder(int displayOrder) { this.displayOrder = displayOrder; return this; }
        public CategoryDtoBuilder productCount(long productCount) { this.productCount = productCount; return this; }
        public CategoryDtoBuilder imageFile(MultipartFile imageFile) { this.imageFile = imageFile; return this; }

        public CategoryDto build() {
            return new CategoryDto(id, name, description, imageUrl, iconName, active, displayOrder, productCount, imageFile);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public long getProductCount() { return productCount; }
    public void setProductCount(long productCount) { this.productCount = productCount; }

    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }
}
