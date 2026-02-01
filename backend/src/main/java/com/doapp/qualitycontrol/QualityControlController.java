package com.doapp.qualitycontrol;

import com.doapp.address.Address;
import com.doapp.address.AddressRepository;
import com.doapp.address.dto.AddressDto;
import com.doapp.qualitycontrol.dto.QualityControlCreateOrderRequest;
import com.doapp.qualitycontrol.dto.QualityControlOwnerDto;
import com.doapp.qualitycontrol.dto.QualityControlOwnerRequest;
import com.doapp.qualitycontrol.dto.QualityControlOrderDto;
import com.doapp.qualitycontrol.dto.QualityControlOrderItemDto;
import com.doapp.qualitycontrol.dto.QualityControlProductDto;
import com.doapp.qualitycontrol.dto.QualityControlReportSummaryDto;
import com.doapp.qualitycontrol.dto.QualityControlUpdateStatusRequest;
import com.doapp.qualitycontrol.dto.AssignProjectControlRequest;
import com.doapp.qualitycontrol.dto.ReportItemDto;
import com.doapp.auth.AuthHelper;
import com.doapp.owner.Owner;
import com.doapp.owner.OwnerRepository;
import com.doapp.order.DeliveryOrder;
import com.doapp.order.OrderItem;
import com.doapp.order.OrderNumberGenerator;
import com.doapp.order.OrderRepository;
import com.doapp.order.OrderStatus;
import com.doapp.product.Product;
import com.doapp.product.ProductRepository;
import com.doapp.user.Role;
import com.doapp.user.User;
import com.doapp.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class QualityControlController {
  private final AuthHelper authHelper;
  private final UserRepository userRepo;
  private final OwnerRepository customerRepo;
  private final AddressRepository addressRepo;
  private final ProductRepository productRepo;
  private final OrderRepository orderRepo;
  private final PasswordEncoder passwordEncoder;

  public QualityControlController(AuthHelper authHelper,
                         UserRepository userRepo,
                         OwnerRepository customerRepo,
                         AddressRepository addressRepo,
                         ProductRepository productRepo,
                         OrderRepository orderRepo,
                         PasswordEncoder passwordEncoder) {
    this.authHelper = authHelper;
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
    this.addressRepo = addressRepo;
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/stats")
  public Map<String, Long> stats(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    long totalOwners = customerRepo.count();
    long inTransit = orderRepo.findAllByOrderByCreatedAtDesc().stream()
        .filter(o -> o.getStatus() == OrderStatus.IN_TRANSIT)
        .count();
    YearMonth ym = YearMonth.now();
    long ordersMonth = orderRepo.findAllByOrderByCreatedAtDesc().stream()
        .filter(o -> o.getCreatedAt() != null && YearMonth.from(o.getCreatedAt()).equals(ym))
        .count();
    return Map.of(
        "totalOwners", totalOwners,
        "ordersThisMonth", ordersMonth,
        "activeShipments", inTransit
    );
  }

  @GetMapping("/customers")
  public List<QualityControlOwnerDto> customers(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return customerRepo.findAll().stream()
        .map(c -> new QualityControlOwnerDto(
            c.getUser().getId(),
            c.getId(),
            c.getUser().getName(),
            c.getUser().getEmail(),
            c.getUser().getPhone(),
            c.getCompanyName(),
            c.getUser().isActive()
        ))
        .toList();
  }

  @PostMapping("/customers")
  public QualityControlOwnerDto createOwner(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody QualityControlOwnerRequest req) {
    authHelper.requireQualityControl(authHeader);

    if (req.name() == null || req.name().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama wajib diisi");
    if (req.email() == null || req.email().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email wajib diisi");
    if (req.password() == null || req.password().length() < 6)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password minimal 6 karakter");
    if (userRepo.existsByEmail(req.email()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email sudah terdaftar");

    User u = new User();
    u.setName(req.name().trim());
    u.setEmail(req.email().trim());
    u.setPhone(req.phone() == null ? null : req.phone().trim());
    u.setRole(Role.CUSTOMER);
    u.setActive(req.active() == null || req.active());
    u.setPasswordHash(passwordEncoder.encode(req.password()));
    userRepo.save(u);

    Owner c = new Owner();
    c.setUser(u);
    c.setOwnerCode("CUST-" + String.format("%04d", u.getId()));
    c.setCompanyName(req.companyName() == null ? null : req.companyName().trim());
    customerRepo.save(c);

    return new QualityControlOwnerDto(
        u.getId(),
        c.getId(),
        u.getName(),
        u.getEmail(),
        u.getPhone(),
        c.getCompanyName(),
        u.isActive()
    );
  }

  @PutMapping("/customers/{userId}")
  public QualityControlOwnerDto updateOwner(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long userId,
                                         @RequestBody QualityControlOwnerRequest req) {
    authHelper.requireQualityControl(authHeader);
    User u = userRepo.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    Owner c = customerRepo.findByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner tidak ditemukan"));

    if (req.name() != null && !req.name().isBlank()) u.setName(req.name().trim());
    if (req.phone() != null) u.setPhone(req.phone().trim());
    if (req.email() != null && !req.email().isBlank()) u.setEmail(req.email().trim());
    if (req.active() != null) u.setActive(req.active());

    if (req.companyName() != null) c.setCompanyName(req.companyName().trim());

    userRepo.save(u);
    customerRepo.save(c);

    return new QualityControlOwnerDto(
        u.getId(),
        c.getId(),
        u.getName(),
        u.getEmail(),
        u.getPhone(),
        c.getCompanyName(),
        u.isActive()
    );
  }

  @GetMapping("/customers/{userId}/orders")
  public List<QualityControlOrderDto> customerOrders(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long userId) {
    authHelper.requireQualityControl(authHeader);
    Owner c = customerRepo.findByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner tidak ditemukan"));
    return orderRepo.findByOwnerIdOrderByCreatedAtDesc(c.getId())
        .stream().map(QualityControlController::toQualityControlOrderDto).toList();
  }

  @GetMapping("/customers/{userId}/addresses")
  public List<AddressDto> customerAddresses(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long userId) {
    authHelper.requireQualityControl(authHeader);
    Owner c = customerRepo.findByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner tidak ditemukan"));
    return addressRepo.findByOwnerIdOrderByIsDefaultDescIdDesc(c.getId())
        .stream().map(QualityControlController::toAddressDto).toList();
  }

  @PostMapping("/customers/{userId}/addresses")
  public AddressDto createAddress(@RequestHeader("Authorization") String authHeader,
                                  @PathVariable Long userId,
                                  @RequestBody Map<String, Object> req) {
    authHelper.requireQualityControl(authHeader);
    Owner c = customerRepo.findByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner tidak ditemukan"));
    String addressLine = safe(getStr(req, "addressLine"), 255);
    if (addressLine == null)
      addressLine = safe(getStr(req, "address"), 255);
    if (addressLine == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alamat wajib diisi");
    String label = safe(getStr(req, "label"), 50);
    if (label == null) label = "Alamat Utama";
    String recipientName = safe(getStr(req, "recipientName"), 120);
    if (recipientName == null) recipientName = safe(c.getUser().getName(), 120);
    String phone = safe(getStr(req, "phone"), 30);
    if (phone == null) phone = safe(c.getUser().getPhone(), 50);
    if (recipientName == null || phone == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama penerima dan telepon wajib diisi");
    Address a = new Address();
    a.setOwner(c);
    a.setLabel(label);
    a.setRecipientName(recipientName);
    a.setPhone(phone);
    a.setAddressLine(addressLine);
    a.setCity(safe(getStr(req, "city"), 80));
    a.setProvince(safe(getStr(req, "province"), 80));
    a.setPostalCode(safe(getStr(req, "postalCode"), 10));
    a.setNotes(safe(getStr(req, "notes"), 500));
    try {
      return toAddressDto(addressRepo.save(a));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal menyimpan alamat");
    }
  }

  @PutMapping("/addresses/{id}")
  public AddressDto updateAddress(@RequestHeader("Authorization") String authHeader,
                                  @PathVariable Long id,
                                  @RequestBody AddressDto req) {
    authHelper.requireQualityControl(authHeader);
    Address a = addressRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat tidak ditemukan"));
    applyAddress(a, req);
    return toAddressDto(addressRepo.save(a));
  }

  @GetMapping("/products")
  public List<QualityControlProductDto> products(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return productRepo.findAll().stream()
        .map(p -> new QualityControlProductDto(
            p.getId(), p.getName(), p.getSku(), p.getUnit(), p.getPrice(), p.getStock(), p.isActive()
        ))
        .toList();
  }

  @PostMapping("/products")
  public QualityControlProductDto createProduct(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody QualityControlProductDto req) {
    authHelper.requireQualityControl(authHeader);
    Product p = new Product();
    p.setName(req.name());
    p.setSku(req.sku());
    p.setUnit(req.unit());
    p.setPrice(req.price());
    p.setStock(req.stock());
    p.setActive(req.active());
    productRepo.save(p);
    return new QualityControlProductDto(p.getId(), p.getName(), p.getSku(), p.getUnit(), p.getPrice(), p.getStock(), p.isActive());
  }

  @PutMapping("/products/{id}")
  public QualityControlProductDto updateProduct(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable Long id,
                                       @RequestBody QualityControlProductDto req) {
    authHelper.requireQualityControl(authHeader);
    Product p = productRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produk tidak ditemukan"));
    if (req.name() != null) p.setName(req.name());
    if (req.sku() != null) p.setSku(req.sku());
    if (req.unit() != null) p.setUnit(req.unit());
    if (req.price() != null) p.setPrice(req.price());
    p.setStock(req.stock());
    p.setActive(req.active());
    productRepo.save(p);
    return new QualityControlProductDto(p.getId(), p.getName(), p.getSku(), p.getUnit(), p.getPrice(), p.getStock(), p.isActive());
  }

  @GetMapping("/orders")
  public List<QualityControlOrderDto> orders(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return orderRepo.findAllByOrderByCreatedAtDesc().stream()
        .map(QualityControlController::toQualityControlOrderDto).toList();
  }

  @PostMapping("/orders/manual")
  public QualityControlOrderDto createManualOrder(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody QualityControlCreateOrderRequest req) {
    authHelper.requireQualityControl(authHeader);
    if (req.customerId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner wajib dipilih");
    if (req.addressId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alamat wajib dipilih");
    if (req.items() == null || req.items().isEmpty())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item wajib diisi");

    Owner c = customerRepo.findById(req.customerId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner tidak ditemukan"));
    Address address = addressRepo.findById(req.addressId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alamat tidak ditemukan"));

    DeliveryOrder o = new DeliveryOrder();
    o.setOwner(c);
    o.setAddress(address);
    o.setNote(req.note());
    o.setStatus(OrderStatus.DRAFT);
    o.setDoNumber(OrderNumberGenerator.generate());

    for (QualityControlCreateOrderRequest.Item item : req.items()) {
      Product p = productRepo.findById(item.productId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produk tidak ditemukan"));
      if (item.quantity() <= 0)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jumlah tidak valid");
      OrderItem oi = new OrderItem();
      oi.setOrder(o);
      oi.setProduct(p);
      oi.setQuantity(item.quantity());
      o.getItems().add(oi);
    }

    return toQualityControlOrderDto(orderRepo.save(o));
  }

  @PostMapping("/orders/{id}/status")
  public QualityControlOrderDto updateStatus(@RequestHeader("Authorization") String authHeader,
                                    @PathVariable Long id,
                                    @RequestBody QualityControlUpdateStatusRequest req) {
    authHelper.requireQualityControl(authHeader);
    DeliveryOrder o = orderRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tidak ditemukan"));
    if (req.status() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status wajib diisi");
    OrderStatus newStatus = OrderStatus.valueOf(req.status());
    if (newStatus == OrderStatus.READY_TO_SHIP && o.getStatus() != OrderStatus.READY_TO_SHIP) {
      for (OrderItem item : o.getItems()) {
        Product p = item.getProduct();
        if (p.getStock() < item.getQuantity())
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stok tidak cukup untuk " + p.getName());
      }
      for (OrderItem item : o.getItems()) {
        Product p = item.getProduct();
        p.setStock(p.getStock() - item.getQuantity());
        productRepo.save(p);
      }
    }
    o.setStatus(newStatus);
    return toQualityControlOrderDto(orderRepo.save(o));
  }

  @PostMapping("/orders/{id}/assign-driver")
  public QualityControlOrderDto assignProjectControl(@RequestHeader("Authorization") String authHeader,
                                    @PathVariable Long id,
                                    @RequestBody AssignProjectControlRequest req) {
    authHelper.requireQualityControl(authHeader);
    if (req.driverId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProjectControl wajib dipilih");
    User driver = userRepo.findById(req.driverId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProjectControl tidak ditemukan"));
    DeliveryOrder o = orderRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tidak ditemukan"));
    o.setProjectControl(driver);
    return toQualityControlOrderDto(orderRepo.save(o));
  }

  @PostMapping("/orders/{id}/reschedule")
  public QualityControlOrderDto reschedule(@RequestHeader("Authorization") String authHeader,
                                  @PathVariable Long id) {
    authHelper.requireQualityControl(authHeader);
    DeliveryOrder o = orderRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tidak ditemukan"));
    o.setStatus(OrderStatus.READY_TO_SHIP);
    return toQualityControlOrderDto(orderRepo.save(o));
  }

  @GetMapping("/drivers")
  public List<Map<String, Object>> drivers(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return userRepo.findByRole(Role.DRIVER).stream()
        .map(u -> {
          Map<String, Object> m = new HashMap<>();
          m.put("id", u.getId());
          m.put("name", u.getName());
          m.put("email", u.getEmail());
          return m;
        })
        .toList();
  }

  @GetMapping("/reports/summary")
  public QualityControlReportSummaryDto reportSummary(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    List<DeliveryOrder> orders = orderRepo.findAllByOrderByCreatedAtDesc();
    LocalDate today = LocalDate.now();
    YearMonth month = YearMonth.now();

    long ordersToday = orders.stream().filter(o -> o.getCreatedAt() != null && o.getCreatedAt().toLocalDate().equals(today)).count();
    long ordersMonth = orders.stream().filter(o -> o.getCreatedAt() != null && YearMonth.from(o.getCreatedAt()).equals(month)).count();
    long activeShipments = orders.stream().filter(o -> o.getStatus() == OrderStatus.IN_TRANSIT).count();

    Map<String, Long> statusCounts = orders.stream()
        .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

    Map<String, Long> productTotals = new HashMap<>();
    Map<String, Long> customerTotals = new HashMap<>();

    for (DeliveryOrder o : orders) {
      String customerName = o.getOwner().getUser().getName();
      customerTotals.put(customerName, customerTotals.getOrDefault(customerName, 0L) + 1);
      for (OrderItem item : o.getItems()) {
        String productName = item.getProduct().getName();
        productTotals.put(productName, productTotals.getOrDefault(productName, 0L) + item.getQuantity());
      }
    }

    List<ReportItemDto> topProducts = productTotals.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(5)
        .map(e -> new ReportItemDto(e.getKey(), e.getValue()))
        .toList();

    List<ReportItemDto> topOwners = customerTotals.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(5)
        .map(e -> new ReportItemDto(e.getKey(), e.getValue()))
        .toList();

    return new QualityControlReportSummaryDto(
        customerRepo.count(),
        ordersToday,
        ordersMonth,
        activeShipments,
        statusCounts,
        topProducts,
        topOwners
    );
  }

  @PostMapping("/notifications/trigger")
  public Map<String, String> triggerNotification(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody Map<String, String> req) {
    authHelper.requireQualityControl(authHeader);
    String type = req.getOrDefault("type", "");
    String orderId = req.getOrDefault("orderId", "-");
    return Map.of("message", "Notifikasi terkirim", "type", type, "orderId", orderId);
  }

  private static QualityControlOrderDto toQualityControlOrderDto(DeliveryOrder o) {
    List<QualityControlOrderItemDto> items = o.getItems().stream()
        .map(i -> new QualityControlOrderItemDto(i.getProduct().getId(), i.getProduct().getName(), i.getProduct().getPrice(), i.getQuantity()))
        .toList();
    String driverName = o.getProjectControl() == null ? null : o.getProjectControl().getName();
    return new QualityControlOrderDto(
        o.getId(),
        o.getDoNumber(),
        o.getStatus().name(),
        o.getNote(),
        o.getCreatedAt(),
        o.getOwner().getId(),
        o.getOwner().getUser().getName(),
        o.getAddress().getAddressLine(),
        driverName,
        items
    );
  }

  private static AddressDto toAddressDto(Address a) {
    return new AddressDto(
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
  }

  private static void applyAddress(Address a, AddressDto req) {
    if (req == null) return;
    a.setLabel(safe(req.label(), 120));
    a.setRecipientName(safe(req.recipientName(), 120));
    a.setPhone(safe(req.phone(), 50));
    a.setAddressLine(safe(req.addressLine(), 255));
    a.setCity(safe(req.city(), 120));
    a.setProvince(safe(req.province(), 120));
    a.setPostalCode(safe(req.postalCode(), 20));
    a.setNotes(safe(req.notes(), 500));
  }

  private static String safe(String v, int max) {
    if (v == null) return null;
    String t = v.trim();
    if (t.isEmpty()) return null;
    return t.length() > max ? t.substring(0, max) : t;
  }

  private static String getStr(Map<String, Object> req, String key) {
    if (req == null) return null;
    Object v = req.get(key);
    if (v == null) return null;
    String s = String.valueOf(v);
    return "null".equalsIgnoreCase(s) ? null : s;
  }
}
