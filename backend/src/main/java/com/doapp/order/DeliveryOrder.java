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
  @JoinColumn(name = "address_id", nullable = false)
  private Address address;

  @Column(name = "do_number", unique = true)
  private String doNumber;

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
  public void setAddress(Address address) { this.address = address; }

  public String getDoNumber() { return doNumber; }
  public void setDoNumber(String doNumber) { this.doNumber = doNumber; }

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
