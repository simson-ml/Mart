package com.example.ecommerce.address;

import org.springframework.data.jpa.repository.*;
import com.example.ecommerce.user.*;
import java.util.*;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User u);
}
