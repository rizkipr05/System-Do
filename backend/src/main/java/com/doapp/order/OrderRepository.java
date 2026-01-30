package com.doapp.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<DeliveryOrder, Long> {
  @EntityGraph(attributePaths = {"items", "items.product", "address"})
  List<DeliveryOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

  @EntityGraph(attributePaths = {"items", "items.product", "address"})
  Optional<DeliveryOrder> findByIdAndCustomerId(Long id, Long customerId);

  @EntityGraph(attributePaths = {"items", "items.product", "address", "customer", "driver"})
  List<DeliveryOrder> findAllByOrderByCreatedAtDesc();

  long countByCustomerIdAndStatusIn(Long customerId, Collection<OrderStatus> statuses);
}
