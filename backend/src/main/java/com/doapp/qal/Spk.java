package com.doapp.qal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "spk")
public class Spk {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "spk_number", unique = true, nullable = false, length = 60)
  private String spkNumber;

  @Column(name = "job_name", nullable = false, length = 200)
  private String jobName;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getSpkNumber() { return spkNumber; }
  public void setSpkNumber(String spkNumber) { this.spkNumber = spkNumber; }

  public String getJobName() { return jobName; }
  public void setJobName(String jobName) { this.jobName = jobName; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
