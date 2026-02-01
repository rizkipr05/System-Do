package com.doapp.qal;

import com.doapp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "qc_profiles")
public class QcProfile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "qc_code", nullable = false, unique = true, length = 30)
  private String qcCode;

  @Column(name = "position", nullable = false, length = 100)
  private String position;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getQcCode() { return qcCode; }
  public void setQcCode(String qcCode) { this.qcCode = qcCode; }

  public String getPosition() { return position; }
  public void setPosition(String position) { this.position = position; }
}
