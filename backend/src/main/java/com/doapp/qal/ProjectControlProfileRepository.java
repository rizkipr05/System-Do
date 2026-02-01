package com.doapp.qal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectControlProfileRepository extends JpaRepository<ProjectControlProfile, Long> {
  Optional<ProjectControlProfile> findByUserId(Long userId);
}
