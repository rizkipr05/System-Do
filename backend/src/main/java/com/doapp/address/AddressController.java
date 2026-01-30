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
                           @RequestBody AddressDto req) {
    Customer c = authHelper.requireCustomer(authHeader);
    Address a = new Address();
    a.setCustomer(c);
    applyDto(a, req);

    if (addressRepo.findByCustomerIdOrderByIsDefaultDescIdDesc(c.getId()).isEmpty())
      a.setDefault(true);

    return toDto(addressRepo.save(a));
  }

  @PutMapping("/{id}")
  public AddressDto update(@RequestHeader("Authorization") String authHeader,
                           @PathVariable Long id,
                           @RequestBody AddressDto req) {
    Customer c = authHelper.requireCustomer(authHeader);
    Address a = addressRepo.findByIdAndCustomerId(id, c.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat tidak ditemukan"));
    applyDto(a, req);
    return toDto(addressRepo.save(a));
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

  private static void applyDto(Address a, AddressDto req) {
    if (req == null) return;
    a.setLabel(req.label());
    a.setRecipientName(req.recipientName());
    a.setPhone(req.phone());
    a.setAddressLine(req.addressLine());
    a.setCity(req.city());
    a.setProvince(req.province());
    a.setPostalCode(req.postalCode());
    a.setNotes(req.notes());
  }
}
