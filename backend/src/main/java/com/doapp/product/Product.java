package com.doapp.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true)
  private String sku;

  private String unit;

  private BigDecimal price;

  private int stock;

  @Column(name = "is_active")
  private boolean active = true;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }

  public String getUnit() { return unit; }
  public void setUnit(String unit) { this.unit = unit; }

  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }

  public int getStock() { return stock; }
  public void setStock(int stock) { this.stock = stock; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
}
