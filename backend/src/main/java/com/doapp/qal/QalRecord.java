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
  @JoinColumn(name = "admin_user_id", nullable = false)
  private User adminUser;

  @Column(name = "admin_code", length = 30)
  private String adminCode;

  @Column(name = "admin_position", length = 100)
  private String adminPosition;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_user_id")
  private User driverUser;

  @Column(name = "driver_code", length = 30)
  private String driverCode;

  @Column(name = "driver_name", length = 120)
  private String driverName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_user_id")
  private User customerUser;

  @Column(name = "customer_code", length = 30)
  private String customerCode;

  @Column(name = "customer_name", length = 120)
  private String customerName;

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

  public User getAdminUser() { return adminUser; }
  public void setAdminUser(User adminUser) { this.adminUser = adminUser; }

  public String getAdminCode() { return adminCode; }
  public void setAdminCode(String adminCode) { this.adminCode = adminCode; }

  public String getAdminPosition() { return adminPosition; }
  public void setAdminPosition(String adminPosition) { this.adminPosition = adminPosition; }

  public User getDriverUser() { return driverUser; }
  public void setDriverUser(User driverUser) { this.driverUser = driverUser; }

  public String getDriverCode() { return driverCode; }
  public void setDriverCode(String driverCode) { this.driverCode = driverCode; }

  public String getDriverName() { return driverName; }
  public void setDriverName(String driverName) { this.driverName = driverName; }

  public User getCustomerUser() { return customerUser; }
  public void setCustomerUser(User customerUser) { this.customerUser = customerUser; }

  public String getCustomerCode() { return customerCode; }
  public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }

  public QalStatus getStatus() { return status; }
  public void setStatus(QalStatus status) { this.status = status; }

  public Instant getSignedAt() { return signedAt; }
  public void setSignedAt(Instant signedAt) { this.signedAt = signedAt; }

  public Instant getApprovedAt() { return approvedAt; }
  public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
