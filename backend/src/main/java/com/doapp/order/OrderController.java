package com.doapp.order;

import com.doapp.address.Address;
import com.doapp.address.AddressRepository;
import com.doapp.address.dto.AddressDto;
import com.doapp.auth.AuthHelper;
import com.doapp.customer.Customer;
import com.doapp.order.dto.ConfirmRequest;
import com.doapp.order.dto.CreateOrderRequest;
import com.doapp.order.dto.OrderDto;
import com.doapp.order.dto.OrderItemDto;
import com.doapp.order.dto.OrderSummaryDto;
import com.doapp.product.Product;
import com.doapp.product.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderRepository orderRepo;
  private final ProductRepository productRepo;
  private final AddressRepository addressRepo;
  private final AuthHelper authHelper;

  public OrderController(OrderRepository orderRepo,
                         ProductRepository productRepo,
                         AddressRepository addressRepo,
                         AuthHelper authHelper) {
    this.orderRepo = orderRepo;
    this.productRepo = productRepo;
    this.addressRepo = addressRepo;
    this.authHelper = authHelper;
  }

  @GetMapping("/summary")
  public OrderSummaryDto summary(@RequestHeader("Authorization") String authHeader) {
    Customer c = authHelper.requireCustomer(authHeader);
    long active = orderRepo.countByCustomerIdAndStatusIn(c.getId(), List.of(
        OrderStatus.DRAFT, OrderStatus.APPROVED, OrderStatus.PACKING,
        OrderStatus.READY_TO_SHIP, OrderStatus.IN_TRANSIT
    ));
    long completed = orderRepo.countByCustomerIdAndStatusIn(c.getId(), List.of(
        OrderStatus.DELIVERED, OrderStatus.CONFIRMED
    ));
    long inTransit = orderRepo.countByCustomerIdAndStatusIn(c.getId(), List.of(OrderStatus.IN_TRANSIT));
    return new OrderSummaryDto(active, completed, inTransit);
  }

  @GetMapping
  public List<OrderDto> list(@RequestHeader("Authorization") String authHeader) {
    Customer c = authHelper.requireCustomer(authHeader);
    return orderRepo.findByCustomerIdOrderByCreatedAtDesc(c.getId())
        .stream()
        .map(OrderController::toDto)
        .toList();
  }

  @GetMapping("/{id}")
  public OrderDto detail(@RequestHeader("Authorization") String authHeader,
                         @PathVariable Long id) {
    Customer c = authHelper.requireCustomer(authHeader);
    DeliveryOrder o = orderRepo.findByIdAndCustomerId(id, c.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tidak ditemukan"));
    return toDto(o);
  }

  @PostMapping
  public OrderDto create(@RequestHeader("Authorization") String authHeader,
                         @RequestBody CreateOrderRequest req) {
    Customer c = authHelper.requireCustomer(authHeader);

    if (req == null || req.items() == null || req.items().isEmpty())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item order wajib diisi");

    if (req.addressId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alamat wajib dipilih");

    Address address = addressRepo.findByIdAndCustomerId(req.addressId(), c.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alamat tidak valid"));

    DeliveryOrder o = new DeliveryOrder();
    o.setCustomer(c);
    o.setAddress(address);
    o.setStatus(OrderStatus.DRAFT);
    o.setNote(req.note());
    o.setDoNumber(OrderNumberGenerator.generate());

    for (CreateOrderRequest.Item item : req.items()) {
      if (item.productId() == null || item.quantity() <= 0)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item order tidak valid");

      Product p = productRepo.findById(item.productId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produk tidak ditemukan"));

      OrderItem oi = new OrderItem();
      oi.setOrder(o);
      oi.setProduct(p);
      oi.setQuantity(item.quantity());
      o.getItems().add(oi);
    }

    return toDto(orderRepo.save(o));
  }

  @PostMapping("/{id}/confirm")
  public OrderDto confirm(@RequestHeader("Authorization") String authHeader,
                          @PathVariable Long id,
                          @RequestBody ConfirmRequest req) {
    Customer c = authHelper.requireCustomer(authHeader);
    DeliveryOrder o = orderRepo.findByIdAndCustomerId(id, c.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tidak ditemukan"));

    if (o.getStatus() != OrderStatus.DELIVERED)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order belum dapat dikonfirmasi");

    if (req.receiverName() == null || req.receiverName().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama penerima wajib diisi");

    o.setReceiverName(req.receiverName().trim());
    o.setReceiverNote(req.note());
    o.setSignatureData(req.signatureData());
    o.setProofImageData(req.proofImageData());
    o.setConfirmedAt(java.time.LocalDateTime.now());
    o.setStatus(OrderStatus.CONFIRMED);

    return toDto(orderRepo.save(o));
  }

  private static OrderDto toDto(DeliveryOrder o) {
    Address a = o.getAddress();
    AddressDto address = new AddressDto(
        a.getId(),
        a.getLabel(),
        a.getRecipientName(),
        a.getPhone(),
        a.getAddressLine(),
        a.getCity(),
        a.getProvince(),
        a.getPostalCode(),
        a.getNotes(),
        a.isDefault()
    );

    List<OrderItemDto> items = o.getItems().stream()
        .map(oi -> new OrderItemDto(
            oi.getProduct().getId(),
            oi.getProduct().getName(),
            oi.getProduct().getUnit(),
            oi.getProduct().getPrice(),
            oi.getQuantity()
        ))
        .toList();

    return new OrderDto(
        o.getId(),
        o.getStatus().name(),
        o.getNote(),
        o.getCreatedAt(),
        o.getUpdatedAt(),
        o.getConfirmedAt(),
        o.getReceiverName(),
        o.getReceiverNote(),
        address,
        items
    );
  }
}
