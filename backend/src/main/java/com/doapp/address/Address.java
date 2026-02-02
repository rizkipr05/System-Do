package com.doapp.address;

import com.doapp.owner.Owner;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "owner_addresses")
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private Owner owner;

  private String label;

  @Column(name = "receiver_name")
  private String recipientName;

  @Column(name = "receiver_phone")
  private String phone;

  @Column(name = "address", columnDefinition = "TEXT")
  private String address;

  @Column(name = "address_line")
  private String addressLine;

  private String city;

  private String province;

  @Column(name = "postal_code")
  private String postalCode;

  @Column(length = 500)
  private String notes;

  @Column(name = "is_default")
  private boolean isDefault;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Owner getOwner() { return owner; }
  public void setOwner(Owner owner) { this.owner = owner; }

  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }

  public String getRecipientName() { return recipientName; }
  public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public String getAddressLine() { return address != null ? address : addressLine; }
  public void setAddressLine(String addressLine) {
    this.addressLine = addressLine;
    this.address = addressLine;
  }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }

  public String getProvince() { return province; }
  public void setProvince(String province) { this.province = province; }

  public String getPostalCode() { return postalCode; }
  public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }

  public boolean isDefault() { return isDefault; }
  public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
