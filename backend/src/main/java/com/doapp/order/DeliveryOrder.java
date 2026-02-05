package com.doapp.order;

import com.doapp.address.Address;
import com.doapp.customer.Customer;
import com.doapp.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_orders")
public class DeliveryOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_address_id", nullable = false)
  private Address address;

  @Column(name = "ship_to_name")
  private String shipToName;

  @Column(name = "ship_to_phone")
  private String shipToPhone;

  @Column(name = "ship_to_address", columnDefinition = "TEXT")
  private String shipToAddress;

  @Column(name = "ship_to_city")
  private String shipToCity;

  @Column(name = "ship_to_province")
  private String shipToProvince;

  @Column(name = "ship_to_postal_code")
  private String shipToPostalCode;

  @Column(name = "do_number", unique = true)
  private String doNumber;

  @Column(name = "order_number")
  private String orderNumber;

  @Enumerated(EnumType.STRING)
  private OrderStatus status = OrderStatus.DRAFT;

  @Column(name = "order_note", length = 500)
  private String note;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  @Column(name = "receiver_name")
  private String receiverName;

  @Column(name = "receiver_note", length = 500)
  private String receiverNote;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_id")
  private User driver;

  @Lob
  @Column(name = "signature_data", columnDefinition = "LONGTEXT")
  private String signatureData;

  @Lob
  @Column(name = "proof_image_data", columnDefinition = "LONGTEXT")
  private String proofImageData;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
    if (this.orderNumber == null && this.doNumber != null) {
      this.orderNumber = this.doNumber;
    }
    if (this.address != null) {
      if (this.shipToName == null) this.shipToName = this.address.getRecipientName();
      if (this.shipToPhone == null) this.shipToPhone = this.address.getPhone();
      if (this.shipToAddress == null) this.shipToAddress = this.address.getAddressLine();
      if (this.shipToCity == null) this.shipToCity = this.address.getCity();
      if (this.shipToProvince == null) this.shipToProvince = this.address.getProvince();
      if (this.shipToPostalCode == null) this.shipToPostalCode = this.address.getPostalCode();
    }
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Customer getCustomer() { return customer; }
  public void setCustomer(Customer customer) { this.customer = customer; }

  public Address getAddress() { return address; }
  public void setAddress(Address address) {
    this.address = address;
    if (address != null) {
      if (this.shipToName == null) this.shipToName = address.getRecipientName();
      if (this.shipToPhone == null) this.shipToPhone = address.getPhone();
      if (this.shipToAddress == null) this.shipToAddress = address.getAddressLine();
      if (this.shipToCity == null) this.shipToCity = address.getCity();
      if (this.shipToProvince == null) this.shipToProvince = address.getProvince();
      if (this.shipToPostalCode == null) this.shipToPostalCode = address.getPostalCode();
    }
  }

  public String getDoNumber() { return doNumber; }
  public void setDoNumber(String doNumber) {
    this.doNumber = doNumber;
    if (this.orderNumber == null) {
      this.orderNumber = doNumber;
    }
  }

  public String getOrderNumber() { return orderNumber; }
  public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

  public String getShipToName() { return shipToName; }
  public void setShipToName(String shipToName) { this.shipToName = shipToName; }

  public String getShipToPhone() { return shipToPhone; }
  public void setShipToPhone(String shipToPhone) { this.shipToPhone = shipToPhone; }

  public String getShipToAddress() { return shipToAddress; }
  public void setShipToAddress(String shipToAddress) { this.shipToAddress = shipToAddress; }

  public String getShipToCity() { return shipToCity; }
  public void setShipToCity(String shipToCity) { this.shipToCity = shipToCity; }

  public String getShipToProvince() { return shipToProvince; }
  public void setShipToProvince(String shipToProvince) { this.shipToProvince = shipToProvince; }

  public String getShipToPostalCode() { return shipToPostalCode; }
  public void setShipToPostalCode(String shipToPostalCode) { this.shipToPostalCode = shipToPostalCode; }

  public OrderStatus getStatus() { return status; }
  public void setStatus(OrderStatus status) { this.status = status; }

  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  public LocalDateTime getConfirmedAt() { return confirmedAt; }
  public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

  public String getReceiverName() { return receiverName; }
  public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

  public String getReceiverNote() { return receiverNote; }
  public void setReceiverNote(String receiverNote) { this.receiverNote = receiverNote; }

  public User getDriver() { return driver; }
  public void setDriver(User driver) { this.driver = driver; }

  public String getSignatureData() { return signatureData; }
  public void setSignatureData(String signatureData) { this.signatureData = signatureData; }

  public String getProofImageData() { return proofImageData; }
  public void setProofImageData(String proofImageData) { this.proofImageData = proofImageData; }

  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }
}
