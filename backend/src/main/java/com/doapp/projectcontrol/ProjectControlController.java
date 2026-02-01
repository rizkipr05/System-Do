package com.doapp.projectcontrol;

import com.doapp.auth.AuthHelper;
import com.doapp.projectcontrol.dto.ProjectControlOrderDto;
import com.doapp.projectcontrol.dto.ProjectControlOrderItemDto;
import com.doapp.projectcontrol.dto.ProjectControlUpdateStatusRequest;
import com.doapp.order.DeliveryOrder;
import com.doapp.order.OrderRepository;
import com.doapp.order.OrderStatus;
import com.doapp.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class ProjectControlController {
  private final AuthHelper authHelper;
  private final OrderRepository orderRepo;

  public ProjectControlController(AuthHelper authHelper, OrderRepository orderRepo) {
    this.authHelper = authHelper;
    this.orderRepo = orderRepo;
  }

  @GetMapping("/summary")
  public Map<String, Long> summary(@RequestHeader("Authorization") String authHeader) {
    User driver = authHelper.requireProjectControl(authHeader);
    List<DeliveryOrder> orders = orderRepo.findByProjectControlIdOrderByCreatedAtDesc(driver.getId());
    LocalDate today = LocalDate.now();
    long todayCount = orders.stream()
        .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().toLocalDate().equals(today))
        .count();
    long inTransit = orders.stream().filter(o -> o.getStatus() == OrderStatus.IN_TRANSIT).count();
    long done = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
    return Map.of("todayCount", todayCount, "inTransit", inTransit, "completed", done);
  }

  @GetMapping("/orders")
  public List<ProjectControlOrderDto> list(@RequestHeader("Authorization") String authHeader) {
    User driver = authHelper.requireProjectControl(authHeader);
    return orderRepo.findByProjectControlIdOrderByCreatedAtDesc(driver.getId())
        .stream().map(ProjectControlController::toDto).toList();
  }

  @GetMapping("/orders/{id}")
  public ProjectControlOrderDto detail(@RequestHeader("Authorization") String authHeader,
                               @PathVariable Long id) {
    User driver = authHelper.requireProjectControl(authHeader);
    DeliveryOrder o = orderRepo.findByIdAndProjectControlId(id, driver.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tidak ditemukan"));
    return toDto(o);
  }

  @PostMapping("/orders/{id}/status")
  public ProjectControlOrderDto updateStatus(@RequestHeader("Authorization") String authHeader,
                                     @PathVariable Long id,
                                     @RequestBody ProjectControlUpdateStatusRequest req) {
    User driver = authHelper.requireProjectControl(authHeader);
    DeliveryOrder o = orderRepo.findByIdAndProjectControlId(id, driver.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tidak ditemukan"));

    if (req.status() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status wajib diisi");

    OrderStatus newStatus = OrderStatus.valueOf(req.status());
    if (newStatus != OrderStatus.IN_TRANSIT && newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.FAILED)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status tidak valid");

    if (newStatus == OrderStatus.FAILED && (req.note() == null || req.note().isBlank()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catatan wajib diisi untuk gagal kirim");

    if (newStatus == OrderStatus.DELIVERED) {
      if (req.proofImageData() == null || req.proofImageData().isBlank())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto penerimaan wajib diisi");
      if (req.signatureData() == null || req.signatureData().isBlank())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tanda tangan wajib diisi");
      o.setProofImageData(req.proofImageData());
      o.setSignatureData(req.signatureData());
    }

    if (req.note() != null) o.setReceiverNote(req.note());
    o.setStatus(newStatus);
    return toDto(orderRepo.save(o));
  }

  private static ProjectControlOrderDto toDto(DeliveryOrder o) {
    List<ProjectControlOrderItemDto> items = o.getItems().stream()
        .map(i -> new ProjectControlOrderItemDto(i.getProduct().getId(), i.getProduct().getName(), i.getQuantity()))
        .toList();

    return new ProjectControlOrderDto(
        o.getId(),
        o.getDoNumber(),
        o.getStatus().name(),
        o.getCreatedAt(),
        o.getOwner().getUser().getName(),
        o.getAddress().getAddressLine(),
        o.getAddress().getCity(),
        o.getAddress().getPhone(),
        items
    );
  }
}
