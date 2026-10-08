package com.simson.shopsphere.dto;

import com.simson.shopsphere.entity.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddressDto {
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a valid 10-digit number")
    private String phone;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;
    private String addressLine;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @NotNull(message = "Address type is required")
    private AddressType addressType = AddressType.HOME;

    private boolean defaultAddress;

    public AddressDto() {}

    public AddressDto(Long id, String fullName, String phone, String addressLine1, String addressLine2, String addressLine, String city, String state, String pincode, AddressType addressType, boolean defaultAddress) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.addressType = addressType;
        this.defaultAddress = defaultAddress;
    }

    public static AddressDtoBuilder builder() {
        return new AddressDtoBuilder();
    }

    public static class AddressDtoBuilder {
        private Long id;
        private String fullName;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String addressLine;
        private String city;
        private String state;
        private String pincode;
        private AddressType addressType = AddressType.HOME;
        private boolean defaultAddress;

        public AddressDtoBuilder id(Long id) { this.id = id; return this; }
        public AddressDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AddressDtoBuilder phone(String phone) { this.phone = phone; return this; }
        public AddressDtoBuilder addressLine1(String addressLine1) { this.addressLine1 = addressLine1; return this; }
        public AddressDtoBuilder addressLine2(String addressLine2) { this.addressLine2 = addressLine2; return this; }
        public AddressDtoBuilder addressLine(String addressLine) { this.addressLine = addressLine; return this; }
        public AddressDtoBuilder city(String city) { this.city = city; return this; }
        public AddressDtoBuilder state(String state) { this.state = state; return this; }
        public AddressDtoBuilder pincode(String pincode) { this.pincode = pincode; return this; }
        public AddressDtoBuilder addressType(AddressType addressType) { this.addressType = addressType; return this; }
        public AddressDtoBuilder defaultAddress(boolean defaultAddress) { this.defaultAddress = defaultAddress; return this; }

        public AddressDto build() {
            return new AddressDto(id, fullName, phone, addressLine1, addressLine2, addressLine, city, state, pincode, addressType, defaultAddress);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getAddressLine() {
        if (addressLine != null && !addressLine.isBlank()) return addressLine;
        return (addressLine1 != null ? addressLine1 : "") + (addressLine2 != null && !addressLine2.isBlank() ? ", " + addressLine2 : "");
    }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public AddressType getAddressType() { return addressType; }
    public void setAddressType(AddressType addressType) { this.addressType = addressType; }

    public boolean isDefaultAddress() { return defaultAddress; }
    public void setDefaultAddress(boolean defaultAddress) { this.defaultAddress = defaultAddress; }

    public boolean isDefault() { return defaultAddress; }
    public void setDefault(boolean defaultAddress) { this.defaultAddress = defaultAddress; }
}
