package com.simson.shopsphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileUpdateRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a valid 10-digit number")
    private String phone;

    public ProfileUpdateRequest() {}

    public ProfileUpdateRequest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public static ProfileUpdateRequestBuilder builder() {
        return new ProfileUpdateRequestBuilder();
    }

    public static class ProfileUpdateRequestBuilder {
        private String name;
        private String phone;

        public ProfileUpdateRequestBuilder name(String name) { this.name = name; return this; }
        public ProfileUpdateRequestBuilder phone(String phone) { this.phone = phone; return this; }

        public ProfileUpdateRequest build() {
            return new ProfileUpdateRequest(name, phone);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
