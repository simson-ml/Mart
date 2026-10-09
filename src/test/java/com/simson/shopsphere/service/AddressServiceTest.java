package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.AddressDto;
import com.simson.shopsphere.entity.Address;
import com.simson.shopsphere.entity.AddressType;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.AddressRepository;
import com.simson.shopsphere.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User user1;
    private User user2;
    private Address address1;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).email("user1@example.com").name("User 1").role(Role.USER).build();
        user2 = User.builder().id(2L).email("user2@example.com").name("User 2").role(Role.USER).build();

        address1 = Address.builder()
                .id(10L)
                .user(user1)
                .fullName("User 1 Name")
                .phone("9876543210")
                .addressLine1("123 Main St")
                .city("Bengaluru")
                .state("Karnataka")
                .pincode("560001")
                .addressType(AddressType.HOME)
                .isDefault(true)
                .build();
    }

    @Test
    @DisplayName("createAddress should associate address with authenticated user")
    void testCreateAddressAssociatesWithUser() {
        AddressDto dto = AddressDto.builder()
                .fullName("New Address Name")
                .phone("9876543210")
                .addressLine1("456 Park Ave")
                .city("Bengaluru")
                .state("Karnataka")
                .pincode("560001")
                .addressType(AddressType.WORK)
                .defaultAddress(false)
                .build();

        when(addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user1)).thenReturn(List.of(address1));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Address created = addressService.createAddress(user1, dto);

        assertNotNull(created);
        assertEquals(user1, created.getUser());
        assertEquals("New Address Name", created.getFullName());
        assertEquals(AddressType.WORK, created.getAddressType());
    }

    @Test
    @DisplayName("getAddressById should return address if it belongs to user")
    void testGetAddressByIdSuccess() {
        when(addressRepository.findByIdAndUser(10L, user1)).thenReturn(Optional.of(address1));

        Address result = addressService.getAddressById(10L, user1);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(user1, result.getUser());
    }

    @Test
    @DisplayName("getAddressById should throw ResourceNotFoundException if address belongs to another user")
    void testGetAddressByIdUnauthorized() {
        when(addressRepository.findByIdAndUser(10L, user2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getAddressById(10L, user2));
    }

    @Test
    @DisplayName("deleteAddress should delete address if owned by user")
    void testDeleteAddressSuccess() {
        when(addressRepository.findByIdAndUser(10L, user1)).thenReturn(Optional.of(address1));
        when(addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user1)).thenReturn(List.of());

        addressService.deleteAddress(10L, user1);

        verify(addressRepository, times(1)).delete(address1);
    }
}
