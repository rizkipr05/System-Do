package com.doapp.customer;

import com.doapp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="customers")
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name="user_id", nullable = false, unique = true)
  private User user;

  @Column(name="customer_code")
  private String customerCode;

  @Column(name="company_name")
  private String companyName;

  // Legacy columns kept for compatibility with existing schema/data dumps.
  @Column(name="name")
  private String name;

  @Column(name="email")
  private String email;

  @Column(name="phone")
  private String phone;

  @Column(name="active")
  private Boolean active;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getCustomerCode() { return customerCode; }
  public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

  public String getCompanyName() { return companyName; }
  public void setCompanyName(String companyName) { this.companyName = companyName; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public Boolean getActive() { return active; }
  public void setActive(Boolean active) { this.active = active; }
}
