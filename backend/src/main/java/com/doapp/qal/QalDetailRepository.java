package com.doapp.qal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QalDetailRepository extends JpaRepository<QalDetail, Long> {
  List<QalDetail> findByQalQalNumberOrderByIdAsc(String qalNumber);
}
