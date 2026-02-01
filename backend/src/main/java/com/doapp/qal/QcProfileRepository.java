package com.doapp.qal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QcProfileRepository extends JpaRepository<QcProfile, Long> {
  Optional<QcProfile> findByUserId(Long userId);
}
