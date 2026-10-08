package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.AddressDto;
import com.simson.shopsphere.entity.Address;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.AddressRepository;
import com.simson.shopsphere.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AddressServiceImpl.class);

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Address getAddressById(Long id, User user) {
        return addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or unauthorized"));
    }

    @Override
    @Transactional
    public Address createAddress(User user, AddressDto dto) {
        List<Address> existing = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        boolean isFirst = existing.isEmpty();
        boolean setAsDefault = isFirst || dto.isDefault();

        if (setAsDefault) {
            existing.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existing);
        }

        Address address = Address.builder()
                .user(user)
                .fullName(dto.getFullName().trim())
                .phone(dto.getPhone().trim())
                .addressLine1(dto.getAddressLine1().trim())
                .addressLine2(dto.getAddressLine2() != null ? dto.getAddressLine2().trim() : null)
                .city(dto.getCity().trim())
                .state(dto.getState().trim())
                .pincode(dto.getPincode().trim())
                .addressType(dto.getAddressType())
                .isDefault(setAsDefault)
                .build();

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long id, User user, AddressDto dto) {
        Address address = getAddressById(id, user);

        if (dto.isDefault() && !address.isDefault()) {
            List<Address> existing = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
            existing.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existing);
            address.setDefault(true);
        }

        address.setFullName(dto.getFullName().trim());
        address.setPhone(dto.getPhone().trim());
        address.setAddressLine1(dto.getAddressLine1().trim());
        address.setAddressLine2(dto.getAddressLine2() != null ? dto.getAddressLine2().trim() : null);
        address.setCity(dto.getCity().trim());
        address.setState(dto.getState().trim());
        address.setPincode(dto.getPincode().trim());
        address.setAddressType(dto.getAddressType());

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id, User user) {
        Address address = getAddressById(id, user);
        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
            if (!remaining.isEmpty()) {
                Address newDefault = remaining.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id, User user) {
        List<Address> addresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        for (Address a : addresses) {
            a.setDefault(a.getId().equals(id));
        }
        addressRepository.saveAll(addresses);
    }

    @Override
    @Transactional(readOnly = true)
    public Address getDefaultAddress(User user) {
        return addressRepository.findByUserAndIsDefaultTrue(user)
                .orElseGet(() -> {
                    List<Address> all = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
                    return all.isEmpty() ? null : all.get(0);
                });
    }
}
