package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.AddressDto;
import com.simson.shopsphere.entity.Address;
import com.simson.shopsphere.entity.User;

import java.util.List;

public interface AddressService {
    List<Address> getUserAddresses(User user);
    Address getAddressById(Long id, User user);
    Address createAddress(User user, AddressDto dto);
    Address updateAddress(Long id, User user, AddressDto dto);
    void deleteAddress(Long id, User user);
    void setDefaultAddress(Long id, User user);
    Address getDefaultAddress(User user);
}
