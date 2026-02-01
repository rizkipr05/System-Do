package com.doapp.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
  List<Address> findByOwnerIdOrderByIsDefaultDescIdDesc(Long customerId);
  Optional<Address> findByIdAndOwnerId(Long id, Long customerId);
}
