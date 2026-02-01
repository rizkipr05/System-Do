package com.doapp.qal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerProfileRepository extends JpaRepository<OwnerProfile, Long> {
  Optional<OwnerProfile> findByUserId(Long userId);
}
