package com.doapp.qal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpkRepository extends JpaRepository<Spk, Long> {
  Optional<Spk> findBySpkNumber(String spkNumber);
}
