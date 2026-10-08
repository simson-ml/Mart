package com.simson.shopsphere.repository;

import com.simson.shopsphere.entity.Address;
import com.simson.shopsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    Optional<Address> findByIdAndUser(Long id, User user);
    Optional<Address> findByUserAndIsDefaultTrue(User user);
}
