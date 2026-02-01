package com.doapp.qal;

import com.doapp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "qal_records")
public class QalRecord {
  @Id
  @Column(name = "qal_number", nullable = false, length = 60)
  private String qalNumber;

  @Column(name = "qal_date", nullable = false)
  private LocalDate qalDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "spk_id", nullable = false)
  private Spk spk;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "qc_user_id", nullable = false)
  private User qcUser;

  @Column(name = "qc_code", length = 30)
  private String qcCode;

  @Column(name = "qc_position", length = 100)
  private String qcPosition;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_control_user_id")
  private User projectControlUser;

  @Column(name = "project_control_code", length = 30)
  private String projectControlCode;

  @Column(name = "project_control_name", length = 120)
  private String projectControlName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id")
  private User ownerUser;

  @Column(name = "owner_code", length = 30)
  private String ownerCode;

  @Column(name = "owner_name", length = 120)
  private String ownerName;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private QalStatus status = QalStatus.DRAFT;

  @Column(name = "signed_at")
  private Instant signedAt;

  @Column(name = "approved_at")
  private Instant approvedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public String getQalNumber() { return qalNumber; }
  public void setQalNumber(String qalNumber) { this.qalNumber = qalNumber; }

  public LocalDate getQalDate() { return qalDate; }
  public void setQalDate(LocalDate qalDate) { this.qalDate = qalDate; }

  public Spk getSpk() { return spk; }
  public void setSpk(Spk spk) { this.spk = spk; }

  public User getQcUser() { return qcUser; }
  public void setQcUser(User qcUser) { this.qcUser = qcUser; }

  public String getQcCode() { return qcCode; }
  public void setQcCode(String qcCode) { this.qcCode = qcCode; }

  public String getQcPosition() { return qcPosition; }
  public void setQcPosition(String qcPosition) { this.qcPosition = qcPosition; }

  public User getProjectControlUser() { return projectControlUser; }
  public void setProjectControlUser(User projectControlUser) { this.projectControlUser = projectControlUser; }

  public String getProjectControlCode() { return projectControlCode; }
  public void setProjectControlCode(String projectControlCode) { this.projectControlCode = projectControlCode; }

  public String getProjectControlName() { return projectControlName; }
  public void setProjectControlName(String projectControlName) { this.projectControlName = projectControlName; }

  public User getOwnerUser() { return ownerUser; }
  public void setOwnerUser(User ownerUser) { this.ownerUser = ownerUser; }

  public String getOwnerCode() { return ownerCode; }
  public void setOwnerCode(String ownerCode) { this.ownerCode = ownerCode; }

  public String getOwnerName() { return ownerName; }
  public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

  public QalStatus getStatus() { return status; }
  public void setStatus(QalStatus status) { this.status = status; }

  public Instant getSignedAt() { return signedAt; }
  public void setSignedAt(Instant signedAt) { this.signedAt = signedAt; }

  public Instant getApprovedAt() { return approvedAt; }
  public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
