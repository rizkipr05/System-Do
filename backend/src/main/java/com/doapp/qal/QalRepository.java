package com.doapp.qal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QalRepository extends JpaRepository<QalRecord, String> {
  List<QalRecord> findByDriverUserIdOrderByCreatedAtDesc(Long userId);
  List<QalRecord> findByCustomerUserIdOrderByCreatedAtDesc(Long userId);
  List<QalRecord> findAllByOrderByCreatedAtDesc();
  Optional<QalRecord> findByQalNumber(String qalNumber);
}
