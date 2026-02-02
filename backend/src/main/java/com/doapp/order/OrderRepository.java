package com.doapp.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<DeliveryOrder, Long> {
  @EntityGraph(attributePaths = {"items", "items.product", "address"})
  List<DeliveryOrder> findByOwnerIdOrderByCreatedAtDesc(Long customerId);

  @EntityGraph(attributePaths = {"items", "items.product", "address"})
  Optional<DeliveryOrder> findByIdAndOwnerId(Long id, Long customerId);

  @EntityGraph(attributePaths = {"items", "items.product", "address", "owner", "driver"})
  List<DeliveryOrder> findAllByOrderByCreatedAtDesc();

  long countByOwnerIdAndStatusIn(Long customerId, Collection<OrderStatus> statuses);

  @EntityGraph(attributePaths = {"items", "items.product", "address", "owner"})
  List<DeliveryOrder> findByDriverIdOrderByCreatedAtDesc(Long driverId);

  @EntityGraph(attributePaths = {"items", "items.product", "address", "owner"})
  Optional<DeliveryOrder> findByIdAndDriverId(Long id, Long driverId);
}
