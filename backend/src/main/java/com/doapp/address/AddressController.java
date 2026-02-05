package com.doapp.address;

import com.doapp.address.dto.AddressDto;
import com.doapp.auth.AuthHelper;
import com.doapp.customer.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
  private final AddressRepository addressRepo;
  private final AuthHelper authHelper;

  public AddressController(AddressRepository addressRepo, AuthHelper authHelper) {
    this.addressRepo = addressRepo;
    this.authHelper = authHelper;
  }

  @GetMapping
  public List<AddressDto> list(@RequestHeader("Authorization") String authHeader) {
    Customer c = authHelper.requireCustomer(authHeader);
    return addressRepo.findByCustomerIdOrderByIsDefaultDescIdDesc(c.getId())
        .stream()
        .map(AddressController::toDto)
        .toList();
  }

  @PostMapping
  public AddressDto create(@RequestHeader("Authorization") String authHeader,
                           @RequestBody Map<String, Object> req) {
    Customer c = authHelper.requireCustomer(authHeader);
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
    a.setCustomer(c);
    applyMap(a, req);
    a.setLabel(label);
    a.setRecipientName(recipientName);
    a.setPhone(phone);
    a.setAddressLine(addressLine);

    if (addressRepo.findByCustomerIdOrderByIsDefaultDescIdDesc(c.getId()).isEmpty())
      a.setDefault(true);

    try {
      return toDto(addressRepo.save(a));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal menyimpan alamat");
    }
  }

  @PutMapping("/{id}")
  public AddressDto update(@RequestHeader("Authorization") String authHeader,
                           @PathVariable Long id,
                           @RequestBody Map<String, Object> req) {
    Customer c = authHelper.requireCustomer(authHeader);
    Address a = addressRepo.findByIdAndCustomerId(id, c.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat tidak ditemukan"));
    String addressLine = safe(getStr(req, "addressLine"), 255);
    if (addressLine == null)
      addressLine = safe(getStr(req, "address"), 255);
    if (addressLine != null) {
      a.setAddressLine(addressLine);
    } else if (a.getAddressLine() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alamat wajib diisi");
    }
    applyMap(a, req);
    if (a.getRecipientName() == null) {
      a.setRecipientName(safe(c.getUser().getName(), 120));
    }
    if (a.getPhone() == null) {
      a.setPhone(safe(c.getUser().getPhone(), 30));
    }
    if (a.getRecipientName() == null || a.getPhone() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama penerima dan telepon wajib diisi");
    try {
      return toDto(addressRepo.save(a));
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal menyimpan alamat");
    }
  }

  @DeleteMapping("/{id}")
  public void delete(@RequestHeader("Authorization") String authHeader,
                     @PathVariable Long id) {
    Customer c = authHelper.requireCustomer(authHeader);
    Address a = addressRepo.findByIdAndCustomerId(id, c.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat tidak ditemukan"));
    addressRepo.delete(a);
  }

  @PostMapping("/{id}/default")
  public AddressDto setDefault(@RequestHeader("Authorization") String authHeader,
                               @PathVariable Long id) {
    Customer c = authHelper.requireCustomer(authHeader);
    List<Address> addresses = addressRepo.findByCustomerIdOrderByIsDefaultDescIdDesc(c.getId());
    Address selected = addresses.stream().filter(a -> a.getId().equals(id)).findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat tidak ditemukan"));

    for (Address a : addresses) {
      a.setDefault(a.getId().equals(selected.getId()));
    }
    addressRepo.saveAll(addresses);
    return toDto(selected);
  }

  private static AddressDto toDto(Address a) {
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

  private static void applyMap(Address a, Map<String, Object> req) {
    if (req == null) return;
    String label = safe(getStr(req, "label"), 50);
    if (label != null) a.setLabel(label);
    String recipientName = safe(getStr(req, "recipientName"), 120);
    if (recipientName != null) a.setRecipientName(recipientName);
    String phone = safe(getStr(req, "phone"), 30);
    if (phone != null) a.setPhone(phone);
    String city = safe(getStr(req, "city"), 80);
    if (city != null) a.setCity(city);
    String province = safe(getStr(req, "province"), 80);
    if (province != null) a.setProvince(province);
    String postal = safe(getStr(req, "postalCode"), 10);
    if (postal != null) a.setPostalCode(postal);
    String notes = safe(getStr(req, "notes"), 500);
    if (notes != null) a.setNotes(notes);
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
