package com.doapp.customer;

import com.doapp.auth.AuthHelper;
import com.doapp.customer.dto.CustomerProfileResponse;
import com.doapp.customer.dto.CustomerUpdateProfileRequest;
import com.doapp.user.User;
import com.doapp.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
  private final AuthHelper authHelper;
  private final UserRepository userRepo;
  private final CustomerRepository customerRepo;

  public CustomerController(AuthHelper authHelper, UserRepository userRepo, CustomerRepository customerRepo) {
    this.authHelper = authHelper;
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
  }

  @GetMapping("/me")
  public CustomerProfileResponse me(@RequestHeader("Authorization") String authHeader) {
    Customer c = authHelper.requireCustomer(authHeader);
    User u = c.getUser();

    return new CustomerProfileResponse(
        u.getId(),
        u.getName(),
        u.getEmail(),
        u.getPhone(),
        c.getCustomerCode(),
        c.getCompanyName()
    );
  }

  @PutMapping("/me")
  public CustomerProfileResponse update(@RequestHeader("Authorization") String authHeader,
                                @RequestBody CustomerUpdateProfileRequest req) {
    Customer c = authHelper.requireCustomer(authHeader);
    User u = c.getUser();

    if (req.name() == null || req.name().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama wajib diisi");

    u.setName(req.name().trim());
    u.setPhone(req.phone() == null ? null : req.phone().trim());
    userRepo.save(u);

    c.setCompanyName(req.companyName() == null ? null : req.companyName().trim());
    customerRepo.save(c);

    return new CustomerProfileResponse(
        u.getId(),
        u.getName(),
        u.getEmail(),
        u.getPhone(),
        c.getCustomerCode(),
        c.getCompanyName()
    );
  }
}
